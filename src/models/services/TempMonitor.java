package models.services;

import application.PropertyManager;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class TempMonitor extends Observable implements Runnable {
    private static final String tempFilePath = PropertyManager.getResourceProperty("temp_file_path");
    private static boolean isStop = false;
    public static TempMonitor instance;

    public synchronized static TempMonitor getInstance() {
        if (instance == null) {
            return new TempMonitor();
        } else {
            isStop = false;
            return instance;
        }
    }

    private TempMonitor() {
        isStop = false;
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Paths.get(tempFilePath).register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStop) {
                WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                if (key == null)
                    continue;
                for (WatchEvent<?> event : key.pollEvents()) {
                    setChanged();
                    notifyObservers(event.context().toString());
                }
                boolean valid = key.reset();
                if (!valid)
                    break;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isStop = true;
    }
}
