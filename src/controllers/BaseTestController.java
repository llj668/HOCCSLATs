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

public abstract class BaseTestController implements DialogControl {
    public AssessmentManager manager;
    public Recorder recorder;
    public ConfirmDialog confirmDialog;

    @FXML
    public AnchorPane root;
    @FXML
    public ImageView imgQuestion;
    @FXML
    public StackPane stackPane;
    @FXML
    public JFXButton btnNext;

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
