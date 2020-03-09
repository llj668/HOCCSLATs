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
		Label yearLabel = new Label("岁");
		Label monthLabel = new Label("月");

		buttonNo = new JFXButton("取消");
		buttonYes = new JFXButton("确认");
		buttonNo.setOnAction(ActionEvent -> {
		    controller.onClickNoDialog();
		});
		buttonYes.setOnAction(ActionEvent -> {
		    controller.onClickYesDialog();
		});

		buttonYes.setDisable(true);
		ChangeListener<String> textFieldListener = (observable, oldValue, newValue) -> {
			if (yearField.getText().matches("\\d+") && monthField.getText().matches("\\d+")) {
				buttonYes.setDisable(false);
			} else {
				buttonYes.setDisable(true);
			}
		};
		yearField.textProperty().addListener(textFieldListener);
		monthField.textProperty().addListener(textFieldListener);

		field.getChildren().addAll(yearField, yearLabel, monthField, monthLabel);
		content.setHeading(new Text("输入年龄"));
		content.setBody(field);
		content.setActions(buttonNo, buttonYes);
	}
	
	public String getText() {
		return yearField.getText() + ";" + monthField.getText();
	}

}
