package controllers;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import models.services.Recorder;
import models.test.AssessmentManager;
import models.test.Question;
import models.test.grammar.GrammarTest;
import models.test.grammar.Utterance;
import views.ResultDisplayer;
import views.items.ConfirmDialog;

/**
 * Controller of the grammar test page
 */
public class GrammarTestController extends BaseTestController {
	@FXML
	private JFXComboBox<Label> quickSelBox;

	private boolean isAnalyzed = false;
	private boolean isQuickSelected = false;
	
	public void initialize() {
		recorder = new Recorder();
		displayer = new ResultDisplayer();
		manager = AssessmentManager.getInstance();
		manager.startGrammarAssessment(this);
		setSampleAnswerBox(manager.nextQuestion());
		root.getChildren().remove(recordLabel);
		btnAnalyze.setDisable(true);
		initFileMonitor();

		// quick select listener
		quickSelBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Utterance utterance = (Utterance) newValue.getUserData();
				GrammarTest assessment = (GrammarTest) manager.getAssessment();
				Question question = manager.getQuestion();
				assessment.setUtterance(utterance);
				assessment.setMarked(question.getSampleAnswers().get(utterance));
				displayer.displayGrammarResult(utterance, question, resultBox);
				isQuickSelected = true;
			}
		});

		// transcription listener
		textTranscribe.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!isDataFound || newValue.equals(""))
				btnAnalyze.setDisable(true);
			else
				btnAnalyze.setDisable(false);
			isQuickSelected = false;
			isAnalyzed = false;
			quickSelBox.getSelectionModel().clearSelection();
			resultBox.getChildren().clear();
		});
	}

	@FXML
	void onClickRecord(ActionEvent event) {
		recorder.startRecord();
		btnStopRecord.toFront();
	}

	@FXML
	void onClickStopRecord(ActionEvent event) {
		recorder.stopRecord();
		btnStopRecord.toBack();
	}

	@FXML
	void onClickAnalyze(ActionEvent event) {
		manager.getAssessment().analyzeResponse(textTranscribe.getText(), true);
		isAnalyzed = true;
		isQuickSelected = false;
		quickSelBox.getSelectionModel().clearSelection();
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
		if (!isAnalyzed && !isQuickSelected) {
			manager.getAssessment().analyzeResponse(textTranscribe.getText(), false);
			isAnalyzed = true;
		}
	}

	@Override
	public void getNextQuestion() {
		setSampleAnswerBox(manager.nextQuestion());
		textTranscribe.setText("");
		resultBox.getChildren().clear();
		isAnalyzed = false;
		isQuickSelected = false;
	}

	@Override
	public void onFileChanged(String fileName) {
		if (fileName.equalsIgnoreCase("temp_sound.wav"))
			callSpeechToText();
	}

	@Override
	public void updateLabels(String struct) {}

	@Override
	public void setSummary(Region summary) {
		monitor.stop();
		root.getChildren().add(summary);
	}

	private void setSampleAnswerBox(Question question) {
		quickSelBox.getItems().clear();
		if (question != null) {
			for (Utterance utterance : question.getSampleAnswers().keySet()) {
				Label label = new Label(utterance.getUtterance());
				label.setUserData(utterance);
				quickSelBox.getItems().add(label);
			}
		}
	}
}
