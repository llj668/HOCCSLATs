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
import views.ViewManager;
import views.items.ConfirmDialog;
import views.items.ProfileListItem;

public class NewProfileController extends Controller {
	private Boolean isModified = false;
	private ConfirmDialog confirmDialog;
	
	@FXML
	private JFXButton btnBack;
	@FXML
	private JFXButton btnSave;
	@FXML
	private JFXTextField textName;
	@FXML
	private JFXTextField textAge;
	@FXML
	private JFXRadioButton selGenderMale;
	@FXML
	private JFXRadioButton selGenderFemale;
	@FXML
	private StackPane stackPane;
	
	public void initialize() {
		stackPane.toBack();
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
	}
	
	private void listenContentChange() {
		ChangeListener<String> contentListener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				isModified = true;
			}
		};
		textName.textProperty().addListener(contentListener);
		textAge.textProperty().addListener(contentListener);
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
