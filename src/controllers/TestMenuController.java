package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import models.profiles.Profile;
import models.test.AssessmentManager;
import views.items.SelectStageDialog;

public class TestMenuController extends BaseController implements DialogControl {
	private AssessmentManager manager;
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
		manager = AssessmentManager.getInstance();
		profile = AssessmentManager.profile;
		labelName.setText(profile.getName());
		labelAge.setText(manager.getTestAge().toString());
		labelGender.setText(profile.getGender());
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		displayScene(PropertyManager.getResourceProperty("mainmenu"));
	}
	
	@FXML
	void onClickChangeProfile(ActionEvent event) {
		displayProfileSelectScene();
	}
	
	@FXML
	void onClickGrammarTest(ActionEvent event) {
		stackPane.toFront();
		dialog = new SelectStageDialog(this, stackPane, new JFXDialogLayout());
		dialog.show();
	}
	
	@FXML
	void onClickPronunTest(ActionEvent event) {
		displayScene(PropertyManager.getResourceProperty("pronuntest"));
	}

	@Override
	public void onClickNoDialog() {
		dialog.close();
		stackPane.toBack();
	}

	@Override
	public void onClickYesDialog() {
		manager.setTestQueue(dialog.getSelections());
		displayScene(PropertyManager.getResourceProperty("grammartest"));
	}
}
