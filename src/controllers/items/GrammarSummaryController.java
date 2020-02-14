package controllers.items;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.BaseTestController;
import controllers.DialogControl;
import controllers.GrammarTestController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.profiles.ProfileWriter;
import models.test.AssessmentManager;
import models.test.grammar.Utterance;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
import views.ResultDisplayer;
import views.ViewManager;
import views.items.ConfirmDialog;

import javax.swing.text.View;

public class GrammarSummaryController extends BaseSummaryController {
    @FXML
    private AnchorPane pane;
    @FXML
    private VBox responseBox;
    @FXML
    private JFXComboBox<Label> stageComboBox;
    @FXML
    private JFXButton btnDiscard;
    @FXML
    private JFXButton btnSave;
    @FXML
    private Label labelAge;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelScore;

    private GrammarResult result;
    private ResultDisplayer displayer;

    public void initialize() {
        displayer = new ResultDisplayer();
        pane.setLayoutY(90);
        pane.getChildren().removeAll(btnDiscard, btnSave);
    }

    @Override
    public void setResult(BaseResult result) {
        this.result = (GrammarResult) result;
        this.labelAge.setText(this.result.testAge.toString());
        this.labelTime.setText(this.result.getTestTime());
        this.labelScore.setText(String.valueOf(this.result.score));
        setStageComboBox();
        stageComboBox.setValue(stageComboBox.getItems().get(0));
        displayStageResult(this.result.stageResults.get(0));
    }

    @Override
    public void setOnAfterTest(BaseTestController controller) {
        pane.getChildren().addAll(btnDiscard, btnSave);
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
        AssessmentManager.profile.getGrammarResults().add(result);
        ProfileWriter.updateProfileResultToXML(AssessmentManager.profile, "grammar");
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
                default:
                    break;
            }
            Label stageLabel = new Label(labelText);
            stageLabel.setId(labelId);
            stageComboBox.getItems().add(stageLabel);
        }

        stageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            responseBox.getChildren().clear();
            for (GrammarStage stage : result.stageResults) {
                if (stage.getStageNo() == Integer.parseInt(newValue.getId())) {
                    displayStageResult(stage);
                }
            }
        });
    }

    private void displayStageResult(GrammarStage stage) {
        ObservableList<GrammarStructure> questions = FXCollections.observableArrayList(stage.getRecords().keySet());
        final TreeItem<GrammarStructure> root = new RecursiveTreeItem<>(questions, RecursiveTreeObject::getChildren);

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

        JFXTreeTableView<GrammarStructure> table = new JFXTreeTableView<>(root);
        table.setLayoutX(450);
        table.setLayoutY(80);
        table.setPrefHeight(350);
        table.setShowRoot(false);
        table.getColumns().setAll(nameColumn, scoreColumn, evaluationColumn);

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            responseBox.getChildren().clear();
            Utterance response = stage.getRecords().get(newValue.getValue());
            displayer.displayGrammarResult(response, responseBox);
        });
        pane.getChildren().add(table);
    }
}
