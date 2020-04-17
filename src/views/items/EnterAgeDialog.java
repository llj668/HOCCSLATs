package views.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;

import controllers.DialogControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.regex.Pattern;


public class EnterAgeDialog extends JFXDialog {
	public JFXTextField textField;
	private JFXTextField yearField;
	private JFXTextField monthField;
	private HBox field;
	private JFXButton buttonNo;
	private JFXButton buttonYes;
	
	public EnterAgeDialog(DialogControl controller, StackPane dialogContainer, JFXDialogLayout content) {
		super(dialogContainer, content, DialogTransition.CENTER, false);
		field = new HBox();
		field.setAlignment(Pos.CENTER);
		field.setSpacing(15);

		yearField = new JFXTextField();
		yearField.setPromptText("0-9");
		monthField = new JFXTextField();
		monthField.setPromptText("0-11");
		yearField.setPrefWidth(110);
		monthField.setPrefWidth(110);
		Label yearLabel = new Label("Year");
		Label monthLabel = new Label("Month");

		buttonNo = new JFXButton("Cancel");
		buttonYes = new JFXButton("Confirm");
		buttonNo.setOnAction(ActionEvent -> {
		    controller.onClickNoDialog();
		});
		buttonYes.setOnAction(ActionEvent -> {
		    controller.onClickYesDialog();
		});

		buttonYes.setDisable(true);
		yearField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (yearField.getText().matches("\\d+")) {
				yearField.setFocusColor(new Color(0.251, 0.349, 0.506, 1.0));
				if (monthField.getText().matches("\\d+")) {
					buttonYes.setDisable(false);
				} else {
					buttonYes.setDisable(true);
				}
			} else {
				yearField.setFocusColor(new Color(0.906, 0, 0, 1.0));
			}
		});
		monthField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (monthField.getText().matches("\\d+")) {
				monthField.setFocusColor(new Color(0.251, 0.349, 0.506, 1.0));
				if (yearField.getText().matches("\\d+")) {
					buttonYes.setDisable(false);
				} else {
					buttonYes.setDisable(true);
				}
			} else {
				monthField.setFocusColor(new Color(0.906, 0, 0, 1.0));
			}
		});

		field.getChildren().addAll(yearField, yearLabel, monthField, monthLabel);
		content.setHeading(new Text("Please enter the age"));
		content.setBody(field);
		content.setActions(buttonNo, buttonYes);
	}
	
	public String getText() {
		return yearField.getText() + ";" + monthField.getText();
	}

}
