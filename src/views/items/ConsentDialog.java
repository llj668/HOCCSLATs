package views.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import controllers.DialogControl;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ConsentDialog extends JFXDialog {
	
	public ConsentDialog(DialogControl controller, StackPane dialogContainer, JFXDialogLayout content) {
		super(dialogContainer, content, DialogTransition.CENTER, false);
//		content.setLayoutX(dialogContainer.getWidth()*0.8);
//		content.setLayoutY(dialogContainer.getHeight()*0.8);
		content.setHeading(new Text("同意许可"));
		content.setBody(new Text("内容"));
		JFXButton buttonNo = new JFXButton("不同意");
		JFXButton buttonYes = new JFXButton("同意");
		buttonNo.setOnAction(ActionEvent -> {
		    controller.onClickNoDialog();
		});
		buttonYes.setOnAction(ActionEvent -> {
		    controller.onClickYesDialog();
		});
		content.setActions(buttonNo, buttonYes);
	}

}
