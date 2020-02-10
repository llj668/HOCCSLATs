package views.items;

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
        addStageResults();
        this.getChildren().addAll(age, time, score);
    }

    private void initializeItemContent() {
        age = new Label("测试年龄：" + grammarResult.testAge);
        age.setPrefSize(100, 30);
        age.setFont(Font.font("System", 15));

        time = new Label("测试时间：" + grammarResult.getTestTime());
        time.setTranslateX(150);
        time.setPrefSize(250, 30);
        time.setFont(Font.font("System", 15));

        score = new Label("总分：" + grammarResult.score);
        score.setTranslateY(40);
        score.setPrefSize(100, 30);
        score.setFont(Font.font("System", 15));
    }

    private void addStageResults() {
        for (GrammarStage stage : grammarResult.stageResults) {
            Label label = new Label();
            label.setPrefSize(100, 30);
            label.setFont(Font.font("System", 15));
            switch (stage.getStageNo()) {
                case 1:
                    label.setText("阶段一：" + stage.getStageScore());
                    label.setTranslateX(150);
                    label.setTranslateY(40);
                    break;
                case 2:
                    label.setText("阶段二：" + stage.getStageScore());
                    label.setTranslateX(250);
                    label.setTranslateY(40);
                    break;
                case 3:
                    label.setText("阶段三：" + stage.getStageScore());
                    label.setTranslateX(150);
                    label.setTranslateY(70);
                    break;
                case 4:
                    label.setText("阶段四：" + stage.getStageScore());
                    label.setTranslateX(250);
                    label.setTranslateY(70);
                    break;
                default:
                    break;
            }
            this.getChildren().add(label);
        }
    }

}
