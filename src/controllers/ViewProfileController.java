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
import profiles.Profile;
import views.ViewManager;
import views.items.ConfirmDialog;
import views.items.ProfileListItem;

public class ViewProfileController extends Controller {
	private Boolean isModified = false;
	private ConfirmDialog confirmDialog;
	
	@FXML
	private JFXButton btnBack;
	
	public void initialize() {
	}
	
	public void displayProfile(Profile profile) {

	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_PROFILE);
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
	}

	@Override
	public void onClickNoDialog() {
	}

	@Override
	public void onClickYesDialog() {
	}

}
