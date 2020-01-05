package views.items;

import com.jfoenix.controls.*;
import controllers.DialogControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SelectStageDialog extends JFXDialog {
	JFXCheckBox selStage1;
	JFXCheckBox selStage2;
	JFXCheckBox selStage3;
	JFXCheckBox selStage4;
	JFXCheckBox[] boxes;
	JFXCheckBox selAll;

	public SelectStageDialog(DialogControl controller, StackPane dialogContainer, JFXDialogLayout content) {
		super(dialogContainer, content, DialogTransition.CENTER, false);
		content.setHeading(new Text("句法/语法测试：选择需要测试的阶段"));
		content.setBody(setSelections());
		JFXButton buttonNo = new JFXButton("取消");
		JFXButton buttonYes = new JFXButton("确认");
		buttonNo.setOnAction(ActionEvent -> controller.onClickNoDialog());
		buttonYes.setOnAction(ActionEvent -> controller.onClickYesDialog());
		content.setActions(buttonNo, buttonYes);
	}

	private VBox setSelections() {
		VBox selectionBox = new VBox(15);
		selStage1 = new JFXCheckBox("第一段");
		selStage2 = new JFXCheckBox("第二段");
		selStage3 = new JFXCheckBox("第三段");
		selStage4 = new JFXCheckBox("第四段");
		selAll = new JFXCheckBox("全选");
		boxes = new JFXCheckBox[]{selStage1, selStage2, selStage3, selStage4};
		decorateSelAll();
		decorateCheckBox(boxes);
		Separator separator = new Separator();
		separator.setMaxWidth(100);
		selectionBox.getChildren().addAll(selStage1, selStage2, selStage3, selStage4, separator, selAll);
		return selectionBox;
	}

	private void decorateCheckBox(JFXCheckBox[] checkBoxes) {
		for (JFXCheckBox checkBox : checkBoxes) {
			checkBox.setPrefSize(USE_COMPUTED_SIZE, 20);
			checkBox.setFont(Font.font("System", 15));
			checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != selAll.isSelected()) {
					if (newValue) {
						boolean isAllSelected = true;
						for (JFXCheckBox box : boxes) {
							if (!box.isSelected()) {
								isAllSelected = false;
								break;
							}
						}
						if (isAllSelected)
							selAll.setSelected(true);
					} else if (selAll.isSelected()) {
						selAll.setSelected(false);
					}
				}
			});
		}
	}

	private void decorateSelAll() {
		selAll.setPrefSize(USE_COMPUTED_SIZE, 20);
		selAll.setFont(Font.font("System", 15));
		selAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				for (JFXCheckBox box : boxes) {
					box.setSelected(true);
				}
			}
		});
	}
	
	public Queue<String> getSelections() {
		Queue<String> selectedStage = new LinkedList<>();
		if (selStage1.isSelected()) {selectedStage.add("1");}
		if (selStage2.isSelected()) {selectedStage.add("2");}
		if (selStage3.isSelected()) {selectedStage.add("3");}
		if (selStage4.isSelected()) {selectedStage.add("4");}
		return selectedStage;
	}

}
