package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import models.test.AssessmentManager;
import views.ViewManager;
import views.items.ConsentDialog;

public class MainMenuController implements DialogControl {
	private ConsentDialog consentDialog;
	
	@FXML
	private JFXButton btnAssessment;
	@FXML
	private JFXButton btnProfile;
	@FXML
	private JFXButton btnExit;
	@FXML
	private StackPane stackPane;
	
	@FXML
	void onClickAssessment(ActionEvent event) {
		stackPane.toFront();
		consentDialog = new ConsentDialog(this, stackPane, new JFXDialogLayout());
		consentDialog.show();
	}
	
	@FXML
	void onClickProfile(ActionEvent event) {
		ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("profile"));
	}
	
	@FXML
	void onClickExit(ActionEvent event) {
		Platform.exit();
	}

	@Override
	public void onClickNoDialog() {
		consentDialog.close();
		stackPane.toBack();
	}

	@Override
	public void onClickYesDialog() {
		AssessmentManager.getInstance();
		ViewManager.getInstance().switchProfileSelectScene();
	}

}
