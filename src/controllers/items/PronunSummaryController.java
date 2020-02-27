package controllers.items;

import application.PropertyManager;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.BaseTestController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import models.profiles.Profile;
import models.profiles.ProfileWriter;
import models.test.AssessmentManager;
import models.test.pronun.ErrorPattern;
import models.test.pronun.PronunItems;
import models.test.pronun.Syllable;
import models.test.results.*;
import org.jetbrains.annotations.NotNull;
import views.ResultDisplayer;
import views.items.ConfirmDialog;
import views.items.ResultItem;

import java.util.Map;

public class PronunSummaryController extends BaseSummaryController {

    @FXML
    private AnchorPane subpane;
    @FXML
    private AnchorPane hintPane;
    @FXML
    private AnchorPane hintPane1;
    @FXML
    private AnchorPane controlPane;
    @FXML
    private GridPane inventoryGrid;
    @FXML
    private JFXComboBox<Label> resultComboBox;
    @FXML
    private Label labelAge;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelPcc;
    @FXML
    private JFXToggleButton toggle75pc;
    @FXML
    private JFXToggleButton toggle90pc;

    private Profile profile;
    private PronunResult result;
    private ResultDisplayer displayer;

    public void initialize() {
        this.displayer = new ResultDisplayer();
        setToggleButtons();
        subpane.getChildren().clear();
        controlPane.getChildren().clear();
        pane.setLayoutY(90);
        pane.getChildren().removeAll(btnDiscard, btnSave);
    }

    @Override
    public void setResult(BaseResult result, Profile profile) {
        this.profile = profile;
        this.result = (PronunResult) result;
        this.labelAge.setText(this.result.testAge.toString());
        this.labelTime.setText(this.result.getTestTime());
        setResultComboBox();
        resultComboBox.setValue(resultComboBox.getItems().get(0));
        displayResult("音节");
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
        profile.getPronunResults().add(result);
        ProfileWriter.updatePronunResultToXML(profile);
        stackPane.toFront();
        confirmDialog = new ConfirmDialog(this, stackPane, new JFXDialogLayout());
        confirmDialog.setText(ConfirmDialog.TEXT_SAVEPROFILE);
        confirmDialog.show();
    }

    private void setResultComboBox() {
        Label syllableLabel = new Label("音节");
        Label inventoryLabel = new Label("发音量表");
        Label errorLabel = new Label("错误模式");
        resultComboBox.getItems().addAll(syllableLabel, inventoryLabel, errorLabel);

        resultComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            displayResult(newValue.getText());
        });
    }

    private void displayResult(String type) {
        switch (type) {
            case "音节":
                setSyllableTable();
                labelPcc.setText("总pcc：" + result.pcc);
                controlPane.getChildren().clear();
                controlPane.getChildren().add(labelPcc);
                break;
            case "发音量表":
                setInventoryTable("75");
                controlPane.getChildren().clear();
                controlPane.getChildren().addAll(toggle75pc, toggle90pc);
                toggle75pc.setSelected(true);
                break;
            case "错误模式":
                plotComparedErrorPatternMap(result.comparedMapErrorPattern);
                controlPane.getChildren().clear();
                break;
            default:
                break;
        }
    }

    private void setSyllableTable() {
        ObservableList<Syllable> syllables = FXCollections.observableArrayList(result.syllables);
        final TreeItem<Syllable> root = new RecursiveTreeItem<>(syllables, RecursiveTreeObject::getChildren);

        JFXTreeTableColumn<Syllable, String> targetColumn = new JFXTreeTableColumn<>("目标音节");
        targetColumn.setPrefWidth(150);
        targetColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Syllable, String> param) ->{
            if (targetColumn.validateValue(param))
                return param.getValue().getValue().getTargetProperty();
            else
                return targetColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<Syllable, String> responseColumn = new JFXTreeTableColumn<>("发音");
        responseColumn.setPrefWidth(150);
        responseColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Syllable, String> param) ->{
            if (responseColumn.validateValue(param))
                return param.getValue().getValue().getResponseProperty();
            else
                return responseColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<Syllable, String> presentColumn = new JFXTreeTableColumn<>("正确音");
        presentColumn.setPrefWidth(150);
        presentColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Syllable, String> param) ->{
            if (presentColumn.validateValue(param))
                return param.getValue().getValue().getPresentConsonantProperty();
            else
                return presentColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<Syllable, String> errorColumn = new JFXTreeTableColumn<>("错误模式");
        errorColumn.setPrefWidth(200);
        errorColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Syllable, String> param) ->{
            if (errorColumn.validateValue(param))
                return param.getValue().getValue().getErrorProperty();
            else
                return errorColumn.getComputedValue(param);
        });

        JFXTreeTableView<Syllable> table = new JFXTreeTableView<>(root);
        table.getColumns().setAll(targetColumn, responseColumn, presentColumn, errorColumn);
        addTableToPane(table);
    }

    private void setInventoryTable(String type) {
        if (type.equalsIgnoreCase("75")) {
            plotComparedInventoryMap(result.comparedMap75);
        } else if (type.equalsIgnoreCase("90")) {
            plotComparedInventoryMap(result.comparedMap90);
        }
    }

    private void plotComparedInventoryMap(Map<String, String> comparedMap) {
        subpane.getChildren().clear();
        subpane.getChildren().addAll(inventoryGrid, hintPane);
        displayer.displayConsonantInventory(comparedMap, inventoryGrid);
    }

    private void plotComparedErrorPatternMap(Map<ErrorPattern, String> comparedMap) {
        subpane.getChildren().clear();
        JFXListView<Label> patternList = new JFXListView<>();
        patternList.setPrefSize(600, 400);
        patternList.setLayoutY(10);
        patternList.getStylesheets().add(PronunSummaryController.class.getResource(PropertyManager.getResourceProperty("inventory_css")).toString());
        for (Map.Entry<ErrorPattern, String> errors : comparedMap.entrySet()) {
            Label label = new Label(PronunItems.patternName.get(errors.getKey()));
            if (errors.getValue().equals("green"))
                label.setId("green_label");
            else if (errors.getValue().equals("red"))
                label.setId("red_label");
            patternList.getItems().add(label);
        }
        subpane.getChildren().addAll(patternList, hintPane1);
    }

    private void addTableToPane(@NotNull JFXTreeTableView table) {
        table.setPrefHeight(500);
        table.setShowRoot(false);
        subpane.getChildren().clear();
        subpane.getChildren().add(table);
    }

    private void setToggleButtons() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        toggle75pc.setToggleGroup(toggleGroup);
        toggle75pc.setUserData("75");
        toggle90pc.setToggleGroup(toggleGroup);
        toggle90pc.setUserData("90");

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            setInventoryTable((String) toggleGroup.getSelectedToggle().getUserData());
        });
    }

}
