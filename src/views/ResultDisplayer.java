package views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.test.grammar.Utterance;
import models.test.pronun.ErrorPattern;
import models.test.pronun.Syllable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResultDisplayer {
    final private double fontSize = 15;
    final private double horizontalSpacing = 10;
    final private double verticalSpacing = 10;
    private Font font;

    public ResultDisplayer() {
        font = new Font("System", fontSize);
    }

    public void displayGrammarResult(Utterance utterance, VBox container) {
        container.getChildren().clear();
        double containerWidth = container.getWidth();
        double addedWidth = 0;
        HBox utteranceLine = new HBox();
        utteranceLine.setSpacing(horizontalSpacing);
        for (Map.Entry<String, String> entry : utterance.getAnalyzedUtterance()) {
            VBox segment = getVBoxSegment(entry);

            addedWidth += segment.getPrefWidth() + horizontalSpacing;
            if (addedWidth > containerWidth) {
                container.getChildren().add(utteranceLine);
                utteranceLine = new HBox();
                utteranceLine.setSpacing(horizontalSpacing);
                utteranceLine.getChildren().add(segment);
                addedWidth = segment.getWidth();
            } else {
                utteranceLine.getChildren().add(segment);
            }
        }
        container.getChildren().add(utteranceLine);
        container.setSpacing(verticalSpacing);
    }

    public void displayPronunResult(Syllable syllable, VBox container) {
        container.getChildren().clear();
        Label presentLabel = new Label("正确辅音：" + syllable.getConsonantsCorrectAsString());
        Label errorLabel = new Label("错误模式：");
        presentLabel.setFont(this.font);
        errorLabel.setFont(this.font);
        container.getChildren().addAll(presentLabel, errorLabel);

        for (ErrorPattern error : syllable.getErrorPatterns()) {
            Label label = new Label(error.name());
            label.setFont(this.font);
            container.getChildren().add(label);
        }
        container.setSpacing(verticalSpacing);
    }

    private VBox getVBoxSegment(Map.Entry<String, String> entry) {
        VBox structureBox = new VBox();
        structureBox.setAlignment(Pos.CENTER);

        Label segment = new Label(entry.getKey());
        segment.setFont(font);
        Label value = new Label(entry.getValue());
        value.setFont(font);

        double boxWidth = Math.max(fontSize * segment.getText().length(), fontSize * value.getText().length()) + 20;
        structureBox.setPrefWidth(boxWidth);
        Separator separator = new Separator();
        separator.setMaxWidth(boxWidth - 10);

        structureBox.getChildren().addAll(segment, separator, value);
        return structureBox;
    }
}
