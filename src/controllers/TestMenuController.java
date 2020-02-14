package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import models.profiles.Profile;
import models.test.AssessmentManager;
import views.ViewManager;
import views.items.SelectStageDialog;

import javax.swing.text.View;

public class TestMenuController implements DialogControl {
	private AssessmentManager assessmentManager;
	private Profile profile;
	private SelectStageDialog dialog;

	@FXML
	private StackPane stackPane;
	@FXML
	private Label labelName;
	@FXML
	private Label labelAge;
	@FXML
	private Label labelGender;
	
	public void initialize() {
		assessmentManager = AssessmentManager.getInstance();
		profile = AssessmentManager.profile;
		labelName.setText(profile.getName());
		labelAge.setText(assessmentManager.getTestAge().toString());
		labelGender.setText(profile.getGender());
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("mainmenu"));
	}
	
	@FXML
	void onClickChangeProfile(ActionEvent event) {
		ViewManager.getInstance().switchProfileSelectScene();
	}
	
	@FXML
	void onClickGrammarTest(ActionEvent event) {
		stackPane.toFront();
		dialog = new SelectStageDialog(this, stackPane, new JFXDialogLayout());
		dialog.show();
	}
	
	@FXML
	void onClickPronunTest(ActionEvent event) {
		ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("pronuntest"));
	}

	@Override
	public void onClickNoDialog() {
		dialog.close();
		stackPane.toBack();
	}

	@Override
	public void onClickYesDialog() {
		assessmentManager.setTestQueue(dialog.getSelections());
		ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("grammartest"));
	}
}
