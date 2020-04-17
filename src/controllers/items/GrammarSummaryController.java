package controllers.items;

import application.LocalStrings;
import application.PropertyManager;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.BaseTestController;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import models.profiles.Profile;
import models.profiles.ProfileWriter;
import models.test.Question;
import models.test.grammar.Utterance;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
import views.ResultDisplayer;
import views.items.ConfirmDialog;

import java.util.LinkedList;
import java.util.List;

/**
 * Controller of the grammar summary
 */
public class GrammarSummaryController extends BaseSummaryController {
    // MCUS
    public static final String[] scoreTexts = {"Silence or \"I don't know\"","Incorrect meaning, incorrect form","Partial or full repetition","Incorrect meaning, correct form","Correct meaning, incorrect form","Correct meaning, correct form"};

    @FXML
    private JFXSlider sliderScore;
    @FXML
    private Label labelEva;
    @FXML
    private VBox responseBox;
    @FXML
    private JFXComboBox<Label> stageComboBox;
    @FXML
    private ImageView imageView;
    @FXML
    private Label labelAge;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelScore;

    private Profile profile;
    private GrammarResult result;
    private ResultDisplayer displayer;
    private JFXTreeTableView<GrammarStructure> stageTable;
    private JFXTreeTableView<Question> questionTable;
    private ChangeListener<Number> sliderListener;
    private Utterance curResponse;

    public void initialize() {
        displayer = new ResultDisplayer();
        pane.setLayoutY(90);
        pane.getChildren().remove(btnDiscard);
        sliderScore.setValue(0);
        labelEva.setText("");

        sliderListener = (observable, oldValue, newValue) -> {
            int value = (int) Math.round(newValue.doubleValue());
            sliderScore.setValue(value);
            labelEva.setText(scoreTexts[(int) sliderScore.getValue()]);
            if ((int) Math.round(oldValue.doubleValue()) != value) {
                stageTable.getSelectionModel().getSelectedItem().getValue().score = value;
                stageTable.refresh();
            }
        };
    }

    /**
     * set a grammar result to the controller and display
     * @param result
     * @param profile
     */
    @Override
    public void setResult(BaseResult result, Profile profile) {
        this.result = (GrammarResult) result;
        this.profile = profile;
        this.labelAge.setText(this.result.testAge.toString());
        this.labelTime.setText(this.result.getTestTime());
        if (this.result.isAllScored) {
            this.labelScore.setText(String.valueOf(this.result.score));
        } else {
            this.labelScore.setText("N/A");
        }

        setStageComboBox();
        stageComboBox.setValue(stageComboBox.getItems().get(0));
    }

    /**
     * Set the page to be after grammar test, can discard grammar result
     * @param controller
     */
    @Override
    public void setOnAfterTest(BaseTestController controller) {
        pane.getChildren().add(btnDiscard);
    }

    @FXML
    void onClickDiscard(ActionEvent event) {
        stackPane.toFront();
        confirmDialog = new ConfirmDialog(this, stackPane, new JFXDialogLayout());
        confirmDialog.setText(ConfirmDialog.TEXT_BACKINTEST);
        confirmDialog.show();
    }

    /**
     * Save the result into profile and prompt
     * @param event
     */
    @FXML
    void onClickSave(ActionEvent event) {
        result.stageResults = removeUnscoredStage();
        if (!profile.getGrammarResults().contains(result))
            profile.getGrammarResults().add(result);
        ProfileWriter.updateGrammarResultToXML(profile, result);
        stackPane.toFront();
        confirmDialog = new ConfirmDialog(this, stackPane, new JFXDialogLayout());
        confirmDialog.setText(ConfirmDialog.TEXT_SAVEPROFILE);
        confirmDialog.show();
    }

    /**
     * Initialize combo box
     */
    private void setStageComboBox() {
        Label questionLabel = new Label("Questions");
        questionLabel.setId("0");
        questionLabel.setUserData("question");
        stageComboBox.getItems().add(questionLabel);
        for (GrammarStage stage : result.stageResults) {
            String labelText = "";
            String labelId = "";
            switch (stage.getStageNo()) {
                case 1:
                    labelText = "Stage I";
                    labelId = "1";
                    break;
                case 2:
                    labelText = "Stage II";
                    labelId = "2";
                    break;
                case 3:
                    labelText = "Stage III";
                    labelId = "3";
                    break;
                case 4:
                    labelText = "Stage IV";
                    labelId = "4";
                    break;
                case -1:
                    labelText = "Unmarked";
                    labelId = "-1";
                    break;
                default:
                    break;
            }
            Label stageLabel = new Label(labelText);
            stageLabel.setId(labelId);
            stageLabel.setUserData("stage");
            stageComboBox.getItems().add(stageLabel);
        }

        stageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            responseBox.getChildren().clear();
            imageView.setImage(null);
            if (newValue.getUserData().toString().equals("stage")) {
                if (!pane.getChildren().contains(sliderScore))
                    pane.getChildren().addAll(sliderScore, labelEva);
                for (GrammarStage stage : result.stageResults) {
                    if (stage.getStageNo() == Integer.parseInt(newValue.getId()))
                        displayStageResult(stage);
                }
            } else {
                pane.getChildren().removeAll(sliderScore, labelEva);
                displayQuestions();
            }
        });
    }

    /**
     * Display the test questions in the table
     */
    private void displayQuestions() {
        ObservableList<Question> questions = FXCollections.observableArrayList(result.questions.keySet());
        TreeItem<Question> root = new RecursiveTreeItem<>(questions, RecursiveTreeObject::getChildren);

        JFXTreeTableColumn<Question, String> idColumn = new JFXTreeTableColumn<>("ID");
        idColumn.setPrefWidth(75);
        idColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Question, String> param) ->{
            if (idColumn.validateValue(param))
                return param.getValue().getValue().getIdProperty();
            else
                return idColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<Question, String> stageColumn = new JFXTreeTableColumn<>("Stage");
        stageColumn.setPrefWidth(75);
        stageColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Question, String> param) ->{
            if (idColumn.validateValue(param))
                return param.getValue().getValue().getStageProperty();
            else
                return idColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<Question, String> utteranceColumn = new JFXTreeTableColumn<>("Response");
        utteranceColumn.setPrefWidth(250);
        utteranceColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Question, String> param) ->{
            if (idColumn.validateValue(param))
                return result.getUtteranceProperty(param.getValue().getValue());
            else
                return idColumn.getComputedValue(param);
        });

        pane.getChildren().remove(stageTable);
        questionTable = new JFXTreeTableView<>(root);
        questionTable.setLayoutX(450);
        questionTable.setLayoutY(80);
        questionTable.setPrefHeight(500);
        questionTable.setShowRoot(false);
        questionTable.getColumns().setAll(idColumn, stageColumn, utteranceColumn);
        questionTable.getStylesheets().add(GrammarSummaryController.class.getResource(PropertyManager.getResourceProperty("table_css")).toString());

        questionTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            responseBox.getChildren().clear();
            curResponse = result.questions.get(newValue.getValue());
            displayer.displayGrammarResult(curResponse, newValue.getValue(), responseBox);
            imageView.setImage(new Image(newValue.getValue().getPath()));
        });
        pane.getChildren().add(questionTable);
    }

    /**
     * Display the results in one stage
     * @param stage
     */
    private void displayStageResult(GrammarStage stage) {
        ObservableList<GrammarStructure> questions = FXCollections.observableArrayList(stage.getRecords().keySet());
        TreeItem<GrammarStructure> root = new RecursiveTreeItem<>(questions, RecursiveTreeObject::getChildren);

        JFXTreeTableColumn<GrammarStructure, String> nameColumn = new JFXTreeTableColumn<>("Structure");
        nameColumn.setPrefWidth(95);
        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<GrammarStructure, String> param) ->{
            if (nameColumn.validateValue(param))
                return param.getValue().getValue().getNameProperty();
            else
                return nameColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<GrammarStructure, String> scoreColumn = new JFXTreeTableColumn<>("Score");
        scoreColumn.setPrefWidth(75);
        scoreColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<GrammarStructure, String> param) ->{
            if (scoreColumn.validateValue(param))
                return param.getValue().getValue().getScoreProperty();
            else
                return scoreColumn.getComputedValue(param);
        });


        JFXTreeTableColumn<GrammarStructure, String> evaluationColumn = new JFXTreeTableColumn<>("Rating");
        evaluationColumn.setPrefWidth(250);
        evaluationColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<GrammarStructure, String> param) ->{
            if (evaluationColumn.validateValue(param))
                return param.getValue().getValue().getEvaluationProperty();
            else
                return evaluationColumn.getComputedValue(param);
        });

        pane.getChildren().remove(stageTable);
        pane.getChildren().remove(questionTable);
        stageTable = new JFXTreeTableView<>(root);
        stageTable.setLayoutX(450);
        stageTable.setLayoutY(80);
        stageTable.setPrefHeight(500);
        stageTable.setShowRoot(false);
        stageTable.getColumns().setAll(nameColumn, scoreColumn, evaluationColumn);
        stageTable.getStylesheets().add(GrammarSummaryController.class.getResource(PropertyManager.getResourceProperty("table_css")).toString());

        stageTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            responseBox.getChildren().clear();
            sliderScore.setDisable(false);
            curResponse = stage.getRecords().get(newValue.getValue());
            displayer.displayGrammarResult(curResponse, result.getQuestion(curResponse), responseBox);
            imageView.setImage(getImage(newValue.getValue().name));
            if (newValue.getValue().score == -1) {
                sliderScore.valueProperty().removeListener(sliderListener);
                sliderScore.setValue(0);
                labelEva.setText("");
                sliderScore.valueProperty().addListener(sliderListener);
            } else {
                sliderScore.setValue(newValue.getValue().score);
            }
        });
        pane.getChildren().add(stageTable);
        resetScoreListener();
    }

    private List<GrammarStage> removeUnscoredStage() {
        List<GrammarStage> stages = new LinkedList<>();
        for (GrammarStage stage : result.stageResults) {
            if (stage.getStageNo() != -1)
                stages.add(stage);
        }
        return stages;
    }

    private void resetScoreListener() {
        sliderScore.valueProperty().removeListener(sliderListener);
        sliderScore.setValue(0);
        labelEva.setText("");
        sliderScore.valueProperty().addListener(sliderListener);
        sliderScore.setOnMouseReleased(event -> {
            result.stageResults = removeUnscoredStage();
            result.unscored = result.unscoredStructures();
            if (result.isAllScored) {
                labelScore.setText(String.valueOf(result.score));
                for (Label label : stageComboBox.getItems()) {
                    if (label.getId().equals("-1"))
                        label.setDisable(true);
                }
            }
        });
        sliderScore.toFront();
        sliderScore.setDisable(true);
    }

    private Image getImage(String target) {
        for (Question question : result.questions.keySet()) {
            if (question.getTargets().containsKey(target))
                return new Image(question.getPath());
        }
        return null;
    }
}
