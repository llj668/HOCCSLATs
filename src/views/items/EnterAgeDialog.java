package views.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;

import controllers.DialogControl;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class EnterAgeDialog extends JFXDialog {
	public JFXTextField textField;
	
	public EnterAgeDialog(DialogControl controller, StackPane dialogContainer, JFXDialogLayout content) {
		super(dialogContainer, content, DialogTransition.CENTER, false);
		textField = new JFXTextField();
		textField.setPromptText("年龄");
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (textField.getText().contains("；"))
				textField.setText(textField.getText().replaceAll("；", ";"));
		});

		content.setHeading(new Text("输入年龄"));
		content.setBody(textField);
		JFXButton buttonNo = new JFXButton("取消");
		JFXButton buttonYes = new JFXButton("确认");
		buttonNo.setOnAction(ActionEvent -> {
		    controller.onClickNoDialog();
		});
		buttonYes.setOnAction(ActionEvent -> {
		    controller.onClickYesDialog();
		});
		content.setActions(buttonNo, buttonYes);
	}
	
	public String getText() {
		return textField.getText();
	}

}
