package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.profiles.Profile;
import models.test.AssessmentManager;
import views.ViewManager;

public class TestMenuController {
	private AssessmentManager assessmentManager;
	private Profile profile;
	
	@FXML
	private Label labelName;
	@FXML
	private Label labelAge;
	@FXML
	private Label labelGender;
	@FXML
	private JFXButton btnBack;
	@FXML
	private JFXButton btnChangeProfile;
	@FXML
	private JFXButton btnGrammar;
	@FXML
	private JFXButton btnPronun;
	
	public void initialize() {
		assessmentManager = AssessmentManager.getInstance();
		profile = AssessmentManager.profile;
		labelName.setText(profile.getInfo().get("name"));
		labelAge.setText(assessmentManager.getTestAge());
		labelGender.setText(profile.getGender());
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_MAIN);
	}
	
	@FXML
	void onClickChangeProfile(ActionEvent event) {
		ViewManager.getInstance().switchProfileSelectScene();
	}
	
	@FXML
	void onClickGrammarTest(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_GRAMMARTEST);
	}
	
	@FXML
	void onClickPronunTest(ActionEvent event) {
		
	}
}
