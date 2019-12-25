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
import test.TestManager;
import views.ViewManager;
import views.items.ConfirmDialog;
import views.items.ProfileListItem;

public class TestMenuController {
	private TestManager testManager;
	
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
		testManager = TestManager.getInstance();
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
		
	}
	
	@FXML
	void onClickPronunTest(ActionEvent event) {
		
	}

}
