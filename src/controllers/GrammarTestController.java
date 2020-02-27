package controllers;

import com.jfoenix.controls.*;
import controllers.items.GrammarSummaryController;
import controllers.items.ItemController;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import models.profiles.Profile;
import models.services.Recorder;
import models.test.AssessmentManager;
import models.test.Question;
import models.test.WebCommunicator;
import models.test.grammar.Utterance;
import models.test.results.GrammarResult;
import views.ResultDisplayer;
import views.ViewManager;
import views.items.ConfirmDialog;

import java.util.Map;

public class GrammarTestController extends BaseTestController {
	@FXML
	private Label labelTarget;
	@FXML
	private Label labelStage;

	private boolean isAnalyzed = false;
	
	public void initialize() {
		recorder = new Recorder();
		displayer = new ResultDisplayer();
		manager = AssessmentManager.getInstance();
		manager.startGrammarAssessment(this);
		manager.nextQuestion();
		root.getChildren().remove(recordLabel);
		initFileMonitor();
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
		if (!isAnalyzed) {
			manager.getAssessment().analyzeResponse(textTranscribe.getText(), false);
			isAnalyzed = true;
		}
		manager.nextQuestion();
		textTranscribe.setText("");
		resultBox.getChildren().clear();
		isAnalyzed = false;
	}

	@Override
	public void onFileChanged(String fileName) {
		if (fileName.equalsIgnoreCase("temp_sound.wav"))
			callSpeechToText();
	}

	@Override
	public void updateLabels(String struct, String stage) {
		labelTarget.setText(struct);
		labelStage.setText(stage);
	}

	@Override
	public void setSummary(Region summary) {
		monitor.stop();
		root.getChildren().add(summary);
	}
}
