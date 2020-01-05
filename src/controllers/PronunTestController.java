package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import models.test.AssessmentManager;
import views.ViewManager;

public class PronunTestController extends BaseTestController {
	
	@FXML
	private JFXButton btnBack;
	@FXML
	private JFXButton btnRecord;
	@FXML
	private JFXButton btnStopRecord;
	@FXML
	private JFXButton btnAnalyze;
	@FXML
	private JFXButton btnNext;
	@FXML
	private JFXTextArea textTranscribe;
	@FXML
	private JFXTextArea textTarget;
	@FXML
	private JFXListView<String> resultList;
	
	public void initialize() {
		manager = AssessmentManager.getInstance();
		manager.startPronunAssessment(this);
		manager.nextQuestion();
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
		textTarget.setText(manager.getAssessment().analyzeResponse(textTranscribe.getText()));
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_TESTMENU);
	}

	@FXML
	void onClickNext(ActionEvent event) {
		manager.nextQuestion();
	}

}
