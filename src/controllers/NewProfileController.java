package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import models.profiles.Profile;
import models.profiles.ProfileWriter;
import views.ViewManager;
import views.items.ConfirmDialog;
import views.items.ProfileListItem;

import java.util.HashMap;

public class NewProfileController implements DialogControl {
	private Boolean isModified = false;
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
			confirmDialog.show();
		} else {
			ViewManager.getInstance().switchScene(ViewManager.PATH_PROFILE);
		}
	}
	
	@FXML
	void onClickSave(ActionEvent event) {
		HashMap<String, String> info = new HashMap<>();
		info.put("name", textName.getText());
		if (selGenderMale.isSelected()) {
			info.put("gender", "male");
		} else if (selGenderFemale.isSelected()) {
			info.put("gender", "female");
		}
		ProfileWriter.writeNewProfileToXML(new Profile(info));
	}
	
	private void listenContentChange() {
		ChangeListener<String> contentListener = (observable, oldValue, newValue) -> isModified = true;
		textName.textProperty().addListener(contentListener);
	}

	@Override
	public void onClickNoDialog() {
		confirmDialog.close();
		stackPane.toBack();
		ViewManager.getInstance().switchScene(ViewManager.PATH_PROFILE);
	}

	@Override
	public void onClickYesDialog() {
		confirmDialog.close();
		stackPane.toBack();
		ViewManager.getInstance().switchScene(ViewManager.PATH_PROFILE);
	}

}
