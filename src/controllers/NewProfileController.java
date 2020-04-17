package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import models.profiles.Profile;
import models.profiles.ProfileWriter;
import views.ViewManager;
import views.items.ConfirmDialog;

import java.util.HashMap;

public class NewProfileController extends BaseController implements DialogControl {
	public static boolean isBeforeAssessment = false;
	private boolean isModified = false;
	private boolean isValidName = false;
	private boolean isGenderSelected = false;
	private ConfirmDialog confirmDialog;
	private ToggleGroup toggleGenderGroup;

	@FXML
	private JFXTextField textName;
	@FXML
	private JFXRadioButton selGenderMale;
	@FXML
	private JFXRadioButton selGenderFemale;
	@FXML
	private StackPane stackPane;
	@FXML
	private JFXButton btnSave;
	
	public void initialize() {
		toggleGenderGroup = new ToggleGroup();
		selGenderMale.setToggleGroup(toggleGenderGroup);
		selGenderFemale.setToggleGroup(toggleGenderGroup);
		toggleGenderGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> isGenderSelected = true);
		btnSave.setDisable(true);
		listenContentChange();
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		if (isModified) {
			stackPane.toFront();
			confirmDialog = new ConfirmDialog(this, stackPane, new JFXDialogLayout());
			confirmDialog.setText(ConfirmDialog.TEXT_NEWPROFILE);
			confirmDialog.show();
		} else {
			goBack();
		}
	}
	
	@FXML
	void onClickSave(ActionEvent event) {
		if (selGenderMale.isSelected()) {
			ProfileWriter.writeNewProfileToXML(new Profile(textName.getText(), "male"));
		} else if (selGenderFemale.isSelected()) {
			ProfileWriter.writeNewProfileToXML(new Profile(textName.getText(), "female"));
		}
		goBack();
	}
	
	private void listenContentChange() {
		ChangeListener<String> contentListener = (observable, oldValue, newValue) -> {
			isModified = true;
			if (newValue.matches("([\u4E00-\u9FA5]+)")) {
				textName.setFocusColor(new Color(0.251, 0.349, 0.506, 1.0));
				isValidName = true;
				if (isGenderSelected)
					btnSave.setDisable(false);
			} else {
				btnSave.setDisable(true);
				textName.setFocusColor(new Color(0.906, 0, 0, 1.0));
				isValidName = false;
			}
		};
		textName.textProperty().addListener(contentListener);
		toggleGenderGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (isValidName)
				btnSave.setDisable(false);
		});
	}

	private void goBack() {
		if (isBeforeAssessment) {
			displayProfileSelectScene();
		} else {
			displayScene(PropertyManager.getResourceProperty("profile"));
		}
	}

	@Override
	public void onClickNoDialog() {
		confirmDialog.close();
		stackPane.toBack();
		goBack();
	}

	@Override
	public void onClickYesDialog() {
		confirmDialog.close();
		stackPane.toBack();
		goBack();
	}

}
