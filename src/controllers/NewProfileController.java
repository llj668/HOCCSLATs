package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import models.profiles.Profile;
import models.profiles.ProfileWriter;
import views.ViewManager;
import views.items.ConfirmDialog;

import java.util.HashMap;

public class NewProfileController implements DialogControl {
	public static boolean isBeforeAssessment = false;
	private boolean isModified = false;
	private ConfirmDialog confirmDialog;

	@FXML
	private JFXTextField textName;
	@FXML
	private JFXRadioButton selGenderMale;
	@FXML
	private JFXRadioButton selGenderFemale;
	@FXML
	private StackPane stackPane;
	
	public void initialize() {
		final ToggleGroup toggleGenderGroup = new ToggleGroup();
		selGenderMale.setToggleGroup(toggleGenderGroup);
		selGenderFemale.setToggleGroup(toggleGenderGroup);
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
			ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("profile"));
		}
	}
	
	@FXML
	void onClickSave(ActionEvent event) {
		if (selGenderMale.isSelected()) {
			ProfileWriter.writeNewProfileToXML(new Profile(textName.getText(), "male"));
		} else if (selGenderFemale.isSelected()) {
			ProfileWriter.writeNewProfileToXML(new Profile(textName.getText(), "female"));
		}
	}
	
	private void listenContentChange() {
		ChangeListener<String> contentListener = (observable, oldValue, newValue) -> isModified = true;
		textName.textProperty().addListener(contentListener);
	}

	private void goBack() {
		if (isBeforeAssessment) {
			ViewManager.getInstance().switchProfileSelectScene();
		} else {
			ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("profile"));
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
