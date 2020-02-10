package controllers;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.services.jpinyin.PinyinException;
import models.services.jpinyin.PinyinFormat;
import models.services.jpinyin.PinyinHelper;
import models.test.AssessmentManager;
import models.test.pronun.Syllable;
import views.ResultDisplayer;
import views.ViewManager;
import views.items.ConfirmDialog;

public class PronunTestController extends BaseTestController {
	private ResultDisplayer displayer;

	@FXML
	private JFXButton btnStopRecord;
	@FXML
	private JFXButton btnNext;
	@FXML
	private JFXTextArea textTranscribe;
	@FXML
	private JFXTextArea textPinyin;
	@FXML
	private VBox resultBox;
	@FXML
	private Label labelTarget;
	
	public void initialize() {
		displayer = new ResultDisplayer();
		manager = AssessmentManager.getInstance();
		manager.startPronunAssessment(this);
		manager.nextQuestion();
		initTextFieldListener();
	}

	@FXML
	void onClickRecord(ActionEvent event) {
		btnStopRecord.toFront();
	}

	@FXML
	void onClickStopRecord(ActionEvent event) {
		btnStopRecord.toBack();
	}

	@FXML
	void onClickAnalyze(ActionEvent event) {
		Syllable syllable = (Syllable) manager.getAssessment().analyzeResponse(textPinyin.getText());
		displayer.displayPronunResult(syllable, resultBox);
		btnNext.setDisable(false);
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		stackPane.toFront();
		confirmDialog = new ConfirmDialog(this, stackPane, new JFXDialogLayout());
		confirmDialog.setText(ConfirmDialog.TEXT_BACKINTEST);
		confirmDialog.show();
	}

	@FXML
	void onClickNext(ActionEvent event) {
		manager.nextQuestion();
		textTranscribe.setText("");
		btnNext.setDisable(true);
		resultBox.getChildren().clear();
	}

	@Override
	public void updateLabels(String struct, String stage) {
		labelTarget.setText(struct);
	}

	@Override
	public String getScore() {
		return null;
	}

	@Override
	public void setSummary(Region summary) {
		root.getChildren().add(summary);
	}

	@Override
	public void onClickNoDialog() {
		ViewManager.getInstance().switchScene(ViewManager.PATH_TESTMENU);
	}

	@Override
	public void onClickYesDialog() {
		confirmDialog.close();
		stackPane.toBack();
	}

	private void initTextFieldListener() {
		btnNext.setDisable(true);
		textTranscribe.textProperty().addListener((observable, oldValue, newValue) -> {
			btnNext.setDisable(true);
			try {
				textPinyin.setText(PinyinHelper.convertToPinyinString(newValue, ",", PinyinFormat.WITH_TONE_NUMBER));
			} catch (PinyinException e) {
				e.printStackTrace();
			}
		});
	}
}
