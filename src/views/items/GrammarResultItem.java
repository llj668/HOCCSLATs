package views.items;

import application.PropertyManager;
import controllers.items.PronunSummaryController;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;

import java.text.SimpleDateFormat;

public class GrammarResultItem extends ResultItem {
    private Label age;
    private Label time;
    private Label score;

    public GrammarResultItem(GrammarResult result) {
        super(result, null);
        initializeItemContent();
        if (result.isAllScored)
            addStageResults();
        this.getChildren().addAll(age, time, score);
        this.getStylesheets().add(GrammarResultItem.class.getResource(PropertyManager.getResourceProperty("resultitem_css")).toString());
    }

    private void initializeItemContent() {
        age = new Label("Test age: " + grammarResult.testAge.toString());
        age.setPrefSize(100, 30);
        age.setFont(Font.font("System", 15));

        time = new Label("Test time: " + grammarResult.getTestTime());
        time.setTranslateX(150);
        time.setPrefSize(250, 30);
        time.setFont(Font.font("System", 15));

        if (grammarResult.isAllScored) {
            score = new Label("Avg score: " + grammarResult.score);
            score.setPrefSize(120, 30);
            score.setId("label_scored");
        } else {
            score = new Label("Score incomplete");
            score.setPrefSize(200, 30);
            score.setId("label_unscored");
        }
        score.setTranslateY(40);
        score.setFont(Font.font("System", 15));
    }

    private void addStageResults() {
        int[] labelX = {150, 250, 150, 250};
        int[] labelY = {40, 40, 70, 70};
        int index = 0;
        for (GrammarStage stage : grammarResult.stageResults) {
            Label label = new Label();
            label.setPrefSize(120, 30);
            label.setFont(Font.font("System", 15));
            switch (stage.getStageNo()) {
                case 1:
                    label.setText("Stage I: " + stage.getStageScore());
                    break;
                case 2:
                    label.setText("Stage II: " + stage.getStageScore());
                    break;
                case 3:
                    label.setText("Stage III: " + stage.getStageScore());
                    break;
                case 4:
                    label.setText("Stage IV: " + stage.getStageScore());
                    break;
                default:
                    break;
            }
            label.setTranslateX(labelX[index]);
            label.setTranslateY(labelY[index]);
            this.getChildren().add(label);
            index++;
        }
    }

}
