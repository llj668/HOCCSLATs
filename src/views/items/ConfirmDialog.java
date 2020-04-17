package views.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import controllers.DialogControl;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ConfirmDialog extends JFXDialog {
	public static final String[] TEXT_NEWPROFILE = {"Changes not saved", "Do you want to save all changes?", "No", "Yes"};
	public static final String[] TEXT_BACKINTEST = {"Result not saved", "If go back now, the test result will be discarded. Proceed?", "Back (discard)", "Cancel"};
	public static final String[] TEXT_SAVEPROFILE = {"Result saved", "The result of this test can be viewed from the profile now.", "OK"};
	public static final String[] TEXT_NODATAFILE = {"Data files not found", "The auto-analysis will be disabled.", "OK"};
	JFXButton buttonNo;
	JFXButton buttonYes;
	JFXDialogLayout content;
	public boolean isSingleAction;
	
	public ConfirmDialog(DialogControl controller, StackPane dialogContainer, JFXDialogLayout content) {
		super(dialogContainer, content, DialogTransition.CENTER, false);
		this.content = content;
		buttonNo = new JFXButton();
		buttonYes = new JFXButton();
		buttonNo.setOnAction(ActionEvent -> {
		    controller.onClickNoDialog();
		});
		buttonYes.setOnAction(ActionEvent -> {
		    controller.onClickYesDialog();
		});
		content.setActions(buttonNo, buttonYes);
	}

	public void setText(String[] texts) {
		if (texts.length == 3) {
			content.setHeading(new Text(texts[0]));
			content.setBody(new Text(texts[1]));
			buttonYes.setText(texts[2]);
			content.setActions(buttonYes);
			isSingleAction = true;
		} else {
			content.setHeading(new Text(texts[0]));
			content.setBody(new Text(texts[1]));
			buttonNo.setText(texts[2]);
			buttonYes.setText(texts[3]);
		}
	}

}
