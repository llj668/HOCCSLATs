package controllers;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import models.services.Recorder;
import models.services.jpinyin.PinyinException;
import models.services.jpinyin.PinyinFormat;
import models.services.jpinyin.PinyinHelper;
import models.test.AssessmentManager;
import models.test.pronun.Syllable;
import views.ResultDisplayer;
import views.items.ConfirmDialog;

public class PronunTestController extends BaseTestController {

	@FXML
	private JFXTextArea textPinyin;
	@FXML
	private Label labelTarget;

	private boolean isNext = false;
	
	public void initialize() {
		recorder = new Recorder();
		displayer = new ResultDisplayer();
		manager = AssessmentManager.getInstance();
		manager.startPronunAssessment(this);
		manager.nextQuestion();
		root.getChildren().remove(recordLabel);
		initTextFieldListener();
		initFileMonitor();
	}

	@FXML
	void onClickRecord(ActionEvent event) {
		btnStopRecord.toFront();
		recorder.startRecord();
	}

	@FXML
	void onClickStopRecord(ActionEvent event) {
		btnStopRecord.toBack();
		recorder.stopRecord();
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
		isNext = true;
		manager.nextQuestion();
		textTranscribe.setText("");
		textPinyin.setText("");
		btnNext.setDisable(true);
		resultBox.getChildren().clear();
		isNext = false;
	}

	@Override
	public void onFileChanged(String fileName) {
		if (fileName.equalsIgnoreCase("temp_sound.wav"))
			callSpeechToText();
	}

	@Override
	public void updateLabels(String struct) {
		labelTarget.setText(struct);
	}

	@Override
	public void setSummary(Region summary) {
		monitor.stop();
		root.getChildren().add(summary);
	}

	@Override
	public void getNextQuestion() {

	}

	private void initTextFieldListener() {
		btnNext.setDisable(true);
		textTranscribe.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!isNext) {
				btnNext.setDisable(true);
				try {
					textPinyin.setText(PinyinHelper.convertToPinyinString(newValue, ",", PinyinFormat.WITH_TONE_NUMBER));
				} catch (PinyinException e) {
					e.printStackTrace();
				}
			}
		});
		textPinyin.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!isNext) {
				Syllable syllable = (Syllable) manager.getAssessment().analyzeResponse(textPinyin.getText(), true);
				displayer.displayPronunResult(syllable, resultBox);
				btnNext.setDisable(false);
			}
		});
	}
}
