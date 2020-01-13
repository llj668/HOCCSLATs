package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.services.Recorder;
import models.test.AssessmentManager;

public abstract class BaseTestController {
    public AssessmentManager manager;
    public Recorder recorder;

    @FXML
    public AnchorPane root;
    @FXML
    public ImageView imgQuestion;

    public abstract void updateLabels(String struct, String stage);
    public abstract String getScore();
}
