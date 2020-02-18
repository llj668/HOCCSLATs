package models.services;

import application.PropertyManager;
import models.test.AssessmentManager;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Recorder {
    String filePath = PropertyManager.getResourceProperty("temp_sound_path");
    double weight = 1; // threshold of sound
    int downSumMax = 20;

    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    boolean flag = true;

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        // 8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        // 8,16
        int channels = 1;
        // 1,2
        boolean signed = true;
        // true,false
        boolean bigEndian = false;
        // true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void startRecord() {
        try {
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

            // Create a thread to capture the microphone
            // data into an audio file and start the
            // thread running. It will run until the
            // Stop button is clicked. This method
            // will return after starting the thread.
            flag = true;
            new CaptureThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        flag = false;
        targetDataLine.stop();
        targetDataLine.close();
        AssessmentManager.getInstance().getController().showStopRecording();
    }

    class CaptureThread extends Thread {
        public void run() {
            AudioFileFormat.Type fileType = null;
            File audioFile = new File(filePath);

            fileType = AudioFileFormat.Type.WAVE;
            int downSum = 0;
            ByteArrayInputStream bais = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioInputStream ais = null;
            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                byte[] fragment = new byte[1024];

                ais = new AudioInputStream(targetDataLine);
                AssessmentManager.getInstance().getController().showOnRecording();
                while (flag) {

                    targetDataLine.read(fragment, 0, fragment.length);
                    // 当数组末位大于weight时开始存储字节（有声音传入），一旦开始不再需要判断末位
                    if (Math.abs(fragment[fragment.length-1]) > weight || baos.size() > 0) {
                        baos.write(fragment);
//                        // 判断语音是否停止
//                        if (Math.abs(fragment[fragment.length-1])<=weight) {
//                            downSum++;
//                        } else {
//                            downSum=0;
//                        }
//                        // 计数超过20说明此段时间没有声音传入(值也可更改)
//                        if(downSum>downSumMax){
//                            System.out.println("停止录入");
//                            break;
//                        }

                    }
                }

                //取得录音输入流
                audioFormat = getAudioFormat();
                byte audioData[] = baos.toByteArray();
                bais = new ByteArrayInputStream(audioData);
                ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
                //定义最终保存的文件名
                System.out.println("开始生成语音文件");
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                downSum = 0;
                stopRecord();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //关闭流
                try {
                    ais.close();
                    bais.close();
                    baos.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
