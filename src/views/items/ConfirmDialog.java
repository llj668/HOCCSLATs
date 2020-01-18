package views.items;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import controllers.DialogControl;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ConfirmDialog extends JFXDialog {
	public static final String[] TEXT_NEWPROFILE = {"有未保存的更改", "是否要保存未保存的修改？", "不保存", "保存"};
	public static final String[] TEXT_BACKINTEST = {"结果未保存", "现在返回将会丢弃所有未保存的测试结果，是否返回？", "返回（丢弃）", "取消"};
	public static final String[] TEXT_SAVEPROFILE = {"结果已保存", "现在可以在对应的档案中查看此次测试结果", "确定"};
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
