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
	public static final String[] scoreTexts = {"无声或“不知道”", "语义错误，结构错误", "部分或全部重复", "语义错误，结构正确", "语义正确，结构错误", "语义正确，结构正确"};

	@FXML
	private JFXTextField textScore;
	@FXML
	private JFXSlider sliderScore;
	@FXML
	private Label labelScore;
	@FXML
	private Label labelTarget;
	@FXML
	private Label labelStage;
	
	public void initialize() {
		recorder = new Recorder();
		displayer = new ResultDisplayer();
		manager = AssessmentManager.getInstance();
		manager.startGrammarAssessment(this);
		manager.nextQuestion();
		root.getChildren().remove(recordLabel);
		initScoreDisplay();
		initButtonListener();
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
		manager.getAssessment().analyzeResponse(textTranscribe.getText());
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
		sliderScore.setValue(0);
		labelScore.setText("");
		textTranscribe.setText("");
		btnNext.setDisable(true);
		resultBox.getChildren().clear();
	}

	private void initScoreDisplay() {
		textScore.setText("0");
		sliderScore.valueProperty().addListener((observable, oldValue, newValue) -> {
			sliderScore.setValue((int) Math.round(newValue.doubleValue()));
			labelScore.setText(scoreTexts[(int) sliderScore.getValue()]);
		});
		Bindings.bindBidirectional(textScore.textProperty(), sliderScore.valueProperty(), new StringConverter<Number>() {
			@Override
			public String toString(Number object) {
				return object.toString();
			}

			@Override
			public Integer fromString(String string) {
				return Integer.parseInt(string);
			}
		});
		sliderScore.setValue(0);
		labelScore.setText("");
	}

	private void initButtonListener() {
		btnNext.setDisable(true);
		textTranscribe.textProperty().addListener((observable, oldValue, newValue) -> {
			btnNext.setDisable(true);
		});
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
	public String getScore() {
		return String.valueOf(Math.round(Double.parseDouble(textScore.getText())));
	}

	@Override
	public void setSummary(Region summary) {
		monitor.stop();
		root.getChildren().add(summary);
	}
}
