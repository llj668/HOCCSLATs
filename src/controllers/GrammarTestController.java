package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.profiles.Profile;
import models.test.AssessmentManager;
import models.test.Question;
import views.ViewManager;
import views.items.ConfirmDialog;

public class GrammarTestController {
	private AssessmentManager manager;
	
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
	private ImageView imgQuestion;
	@FXML
	private JFXTextArea textTranscribe;
	@FXML
	private JFXListView<String> resultList;
	@FXML
	private JFXToggleButton toggleCorrect;
	@FXML
	private JFXToggleButton toggleHalfCorrect;
	@FXML
	private JFXToggleButton toggleWrong;
	
	public void initialize() {
		manager = AssessmentManager.getInstance();
		manager.startGrammarAssessment();
		imgQuestion.setImage(new Image(manager.getAssessment().getNextQuestion().path));

		final ToggleGroup toggleGenderGroup = new ToggleGroup();
		toggleCorrect.setToggleGroup(toggleGenderGroup);
		toggleHalfCorrect.setToggleGroup(toggleGenderGroup);
		toggleWrong.setToggleGroup(toggleGenderGroup);
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
		manager.getAssessment().analyzeResponse(textTranscribe.getText());
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_TESTMENU);
	}

	@FXML
	void onClickNext(ActionEvent event) {
		Question question = manager.getAssessment().getNextQuestion();
		if (question != null) {
			imgQuestion.setImage(new Image(question.path));
		}
	}

}
