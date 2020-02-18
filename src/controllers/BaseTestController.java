package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.services.Recorder;
import models.services.TempMonitor;
import models.test.AssessmentManager;
import models.test.WebCommunicator;
import views.ResultDisplayer;
import views.ViewManager;
import views.items.ConfirmDialog;
import views.items.TranscribeSpinner;

import java.util.Observable;
import java.util.Observer;

public abstract class BaseTestController implements DialogControl, Observer {
    public AssessmentManager manager;
    public Recorder recorder;
    public ConfirmDialog confirmDialog;
    public ResultDisplayer displayer;
    TempMonitor monitor;
    TranscribeSpinner spinner;
    boolean isTranscribing = false;

    @FXML
    public AnchorPane root;
    @FXML
    public ImageView imgQuestion;
    @FXML
    public StackPane stackPane;
    @FXML
    public VBox resultBox;
    @FXML
    public JFXButton btnAnalyze;
    @FXML
    public JFXButton btnNext;
    @FXML
    public JFXButton btnRecord;
    @FXML
    public JFXButton btnStopRecord;
    @FXML
    public JFXTextArea textTranscribe;
    @FXML
    public Label recordLabel;

    public void initFileMonitor() {
        monitor = TempMonitor.getInstance();
        monitor.addObserver(this);
        new Thread(monitor).start();
    }

    public void callSpeechToText() {
        if (!isTranscribing) {
            isTranscribing = true;
            spinner = new TranscribeSpinner();
            root.getChildren().add(spinner);
            btnRecord.setDisable(true);
            btnStopRecord.setDisable(true);
            new Thread(WebCommunicator::speechToText).start();
        }
    }

    public void finishSpeechToText(String result) {
        Platform.runLater(() -> {
            textTranscribe.setText(result);
            btnRecord.setDisable(false);
            btnStopRecord.setDisable(false);
            root.getChildren().remove(spinner);
            isTranscribing = false;
        });
    }

    public void showOnRecording() {
        Platform.runLater(() -> root.getChildren().add(recordLabel));
    }

    public void showStopRecording() {
        Platform.runLater(() -> root.getChildren().remove(recordLabel));
    }

    @Override
    public void onClickNoDialog() {
        manager.clearAll();
        monitor.stop();
        ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("testmenu"));
    }

    @Override
    public void onClickYesDialog() {
        confirmDialog.close();
        stackPane.toBack();
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> onFileChanged((String) arg));
    }

    public abstract void onFileChanged(String fileName);
    public abstract void updateLabels(String struct, String stage);
    public abstract String getScore();
    public abstract void setSummary(Region summary);
}
