package views.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import controllers.DialogControl;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ConfirmDialog extends JFXDialog {
	
	public ConfirmDialog(DialogControl controller, StackPane dialogContainer, JFXDialogLayout content) {
		super(dialogContainer, content, DialogTransition.CENTER, false);
		content.setHeading(new Text("有未保存的更改"));
		content.setBody(new Text("是否要保存未保存的修改？"));
		JFXButton buttonNo = new JFXButton("不保存");
		JFXButton buttonYes = new JFXButton("保存");
		buttonNo.setOnAction(ActionEvent -> {
		    controller.onClickNoDialog();
		});
		buttonYes.setOnAction(ActionEvent -> {
		    controller.onClickYesDialog();
		});
		content.setActions(buttonNo, buttonYes);
	}

}
