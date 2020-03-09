package controllers.items;

import application.PropertyManager;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.BaseTestController;
import controllers.DialogControl;
import controllers.GrammarTestController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.profiles.Profile;
import models.profiles.ProfileWriter;
import models.test.AssessmentManager;
import models.test.Question;
import models.test.grammar.Utterance;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
import org.apache.commons.lang3.StringUtils;
import views.ResultDisplayer;
import views.ViewManager;
import views.items.ConfirmDialog;

import javax.swing.text.View;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GrammarSummaryController extends BaseSummaryController {
    public static final String[] scoreTexts = {"无声或“不知道”", "语义错误，结构错误", "部分或全部重复", "语义错误，结构正确", "语义正确，结构错误", "语义正确，结构正确"};

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
    @FXML
    private JFXToggleButton toggleClause;
    @FXML
    private JFXToggleButton togglePhrase;

    private Profile profile;
    private GrammarResult result;
    private ResultDisplayer displayer;
    private JFXTreeTableView<GrammarStructure> table;
    private ChangeListener<Number> sliderListener;
    private ChangeListener<Toggle> toggleListener;
    private Map<String, Image> questionImages;
    private Utterance curResponse;
    private boolean isAfterTest = false;
    final ToggleGroup toggleGroup = new ToggleGroup();

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
                table.getSelectionModel().getSelectedItem().getValue().score = value;
                table.refresh();
            }
        };
    }

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
        setImageList();
        setToggleButtons();
        stageComboBox.setValue(stageComboBox.getItems().get(0));
        displayStageResult(this.result.stageResults.get(0));
    }

    @Override
    public void setOnAfterTest(BaseTestController controller) {
        pane.getChildren().add(btnDiscard);
        isAfterTest = true;
    }

    @FXML
    void onClickDiscard(ActionEvent event) {
        stackPane.toFront();
        confirmDialog = new ConfirmDialog(this, stackPane, new JFXDialogLayout());
        confirmDialog.setText(ConfirmDialog.TEXT_BACKINTEST);
        confirmDialog.show();
    }

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

    private void setStageComboBox() {
        for (GrammarStage stage : result.stageResults) {
            String labelText = "";
            String labelId = "";
            switch (stage.getStageNo()) {
                case 1:
                    labelText = "阶段一";
                    labelId = "1";
                    break;
                case 2:
                    labelText = "阶段二";
                    labelId = "2";
                    break;
                case 3:
                    labelText = "阶段三";
                    labelId = "3";
                    break;
                case 4:
                    labelText = "阶段四";
                    labelId = "4";
                    break;
                case -1:
                    labelText = "未打分";
                    labelId = "-1";
                    break;
                default:
                    break;
            }
            Label stageLabel = new Label(labelText);
            stageLabel.setId(labelId);
            stageComboBox.getItems().add(stageLabel);
        }

        stageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            resetToggle();
            responseBox.getChildren().clear();
            imageView.setImage(null);
            for (GrammarStage stage : result.stageResults) {
                if (stage.getStageNo() == Integer.parseInt(newValue.getId())) {
                    displayStageResult(stage);
                }
            }
        });
    }

    private void displayStageResult(GrammarStage stage) {
        ObservableList<GrammarStructure> questions = FXCollections.observableArrayList(stage.getRecords().keySet());
        TreeItem<GrammarStructure> root = new RecursiveTreeItem<>(questions, RecursiveTreeObject::getChildren);

        JFXTreeTableColumn<GrammarStructure, String> nameColumn = new JFXTreeTableColumn<>("结构");
        nameColumn.setPrefWidth(150);
        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<GrammarStructure, String> param) ->{
            if (nameColumn.validateValue(param))
                return param.getValue().getValue().getNameProperty();
            else
                return nameColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<GrammarStructure, String> scoreColumn = new JFXTreeTableColumn<>("得分");
        scoreColumn.setPrefWidth(150);
        scoreColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<GrammarStructure, String> param) ->{
            if (scoreColumn.validateValue(param))
                return param.getValue().getValue().getScoreProperty();
            else
                return scoreColumn.getComputedValue(param);
        });


        JFXTreeTableColumn<GrammarStructure, String> evaluationColumn = new JFXTreeTableColumn<>("评价");
        evaluationColumn.setPrefWidth(250);
        evaluationColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<GrammarStructure, String> param) ->{
            if (evaluationColumn.validateValue(param))
                return param.getValue().getValue().getEvaluationProperty();
            else
                return evaluationColumn.getComputedValue(param);
        });

        pane.getChildren().remove(table);
        table = new JFXTreeTableView<>(root);
        table.setLayoutX(450);
        table.setLayoutY(80);
        table.setPrefHeight(350);
        table.setShowRoot(false);
        table.getColumns().setAll(nameColumn, scoreColumn, evaluationColumn);

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            responseBox.getChildren().clear();
            sliderScore.setDisable(false);
            curResponse = stage.getRecords().get(newValue.getValue());
            toggleClause.setDisable(false);
            togglePhrase.setDisable(false);
            toggleClause.setSelected(false);
            toggleClause.setSelected(true);
            imageView.setImage(questionImages.get(newValue.getValue().name.name()));
            if (newValue.getValue().score == -1) {
                sliderScore.setValue(0);
                labelEva.setText("");
            } else {
                sliderScore.setValue(newValue.getValue().score);
            }
        });
        pane.getChildren().add(table);
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

    private void setImageList() {
        questionImages = new HashMap<>();
        File[] files = new File(PropertyManager.getResourceProperty("grammar_question")).listFiles();
        assert files != null;
        for (File file : files) {
            String[] str = StringUtils.substringBefore(file.getName(), ".").split("-");
            String no = str[0];
            String target = str[1];
            for (GrammarStage stage : result.stageResults) {
                if (no.equals(String.valueOf(stage.getStageNo())))
                    questionImages.put(target, new Image(StringUtils.substring(file.getPath(),5)));
            }
        }
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
                    displayer.displayGrammarResult(curResponse.getAnalyzedUtterance(), responseBox);
                else if (cmd.equals("phrase"))
                    displayer.displayGrammarResult(curResponse.getAnalyzedPhrase(), responseBox);
            }
        };

        toggleGroup.selectedToggleProperty().addListener(toggleListener);
    }

    private void resetToggle() {
        toggleGroup.selectedToggleProperty().removeListener(toggleListener);
        toggleClause.setSelected(false);
        togglePhrase.setSelected(false);
        toggleClause.setDisable(true);
        togglePhrase.setDisable(true);
        toggleGroup.selectedToggleProperty().addListener(toggleListener);
    }
}
