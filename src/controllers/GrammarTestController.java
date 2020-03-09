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
import javafx.scene.control.Toggle;
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
	@FXML
	private JFXToggleButton toggleClause;
	@FXML
	private JFXToggleButton togglePhrase;

	private ChangeListener<Toggle> toggleListener;
	private Utterance curResponse;
	private boolean isAnalyzed = false;
	final ToggleGroup toggleGroup = new ToggleGroup();
	
	public void initialize() {
		recorder = new Recorder();
		displayer = new ResultDisplayer();
		manager = AssessmentManager.getInstance();
		manager.startGrammarAssessment(this);
		manager.nextQuestion();
		root.getChildren().remove(recordLabel);
		initFileMonitor();
		setToggleButtons();
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
		curResponse = (Utterance) manager.getAssessment().analyzeResponse(textTranscribe.getText(), true);
		toggleClause.setDisable(false);
		togglePhrase.setDisable(false);
		toggleClause.setSelected(true);
		toggleGroup.selectedToggleProperty().addListener(toggleListener);
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
		} else {
			resetToggle();
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

	private void setToggleButtons() {
		toggleClause.setToggleGroup(toggleGroup);
		toggleClause.setUserData("clause");
		togglePhrase.setToggleGroup(toggleGroup);
		togglePhrase.setUserData("phrase");
		toggleClause.setDisable(true);
		togglePhrase.setDisable(true);

		toggleListener = (observable, oldValue, newValue) -> {
			if (newValue != null) {
				String cmd = (String) newValue.getUserData();
				if (cmd.equals("clause"))
					displayer.displayGrammarResult(curResponse.getAnalyzedUtterance(), resultBox);
				else if (cmd.equals("phrase"))
					displayer.displayGrammarResult(curResponse.getAnalyzedPhrase(), resultBox);
			}
		};
	}

	private void resetToggle() {
		toggleGroup.selectedToggleProperty().removeListener(toggleListener);
		toggleClause.setSelected(false);
		togglePhrase.setSelected(false);
		toggleClause.setDisable(true);
		togglePhrase.setDisable(true);
	}
}
