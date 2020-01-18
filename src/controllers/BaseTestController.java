package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import models.services.Recorder;
import models.test.AssessmentManager;
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

    public abstract void updateLabels(String struct, String stage);
    public abstract String getScore();
    public abstract void setSummary(Region summary);
}