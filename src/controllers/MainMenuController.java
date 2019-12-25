package controllers;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import views.ViewManager;

public class MainMenuController {

	@FXML
	private JFXButton btnAssessment;
	@FXML
	private JFXButton btnProfile;
	@FXML
	private JFXButton btnExit;
	
	@FXML
	void onClickAssessment(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_MAIN);
	}
	
	@FXML
	void onClickProfile(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_PROFILE);
	}
	
	@FXML
	void onClickExit(ActionEvent event) {
		Platform.exit();
	}

}
