package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import models.services.Recorder;
import models.test.AssessmentManager;
import views.ViewManager;
import views.items.ConfirmDialog;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public abstract class BaseTestController implements DialogControl {
    public AssessmentManager manager;
    public Recorder recorder;
    public ConfirmDialog confirmDialog;
    public String tempSoundFile = PropertyManager.getResourceProperty("temp_sound_path");
    String tempFilePath = PropertyManager.getResourceProperty("temp_file_path");
    Runnable watcherThread;
    WatchService watchService;

    @FXML
    public AnchorPane root;
    @FXML
    public ImageView imgQuestion;
    @FXML
    public StackPane stackPane;
    @FXML
    public JFXButton btnNext;

    public void initWatchService() {
        watcherThread = new Runnable() {
            @Override
            public void run() {
                try {
                    watchService = FileSystems.getDefault().newWatchService();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onClickNoDialog() {
        manager.clearAll();
        ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("testmenu"));
    }

    @Override
    public void onClickYesDialog() {
        confirmDialog.close();
        stackPane.toBack();
    }

    public abstract void updateLabels(String struct, String stage);
    public abstract String getScore();
    public abstract void setSummary(Region summary);
}
