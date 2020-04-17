package views;

import application.PropertyManager;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.test.Question;
import models.test.grammar.Utterance;
import models.test.pronun.ErrorPattern;
import models.test.pronun.PronunItems;
import models.test.pronun.Syllable;

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

    public void displayGrammarResult(Utterance utterance, Question question, VBox container) {
        container.getChildren().clear();
        container.setSpacing(verticalSpacing);
        if (utterance != null) {
            // Utterance display
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

            // Presented clause level structure display
            HBox clauseBox = getPresentHBox(new Label("C: "), utterance.getPresentUtteranceStructures());
            // Presented phrase level structure display
            HBox phraseBox = getPresentHBox(new Label("P: "), utterance.getPresentPhraseStructures());
            // Presented word level structure display
            HBox wordBox = getPresentHBox(new Label("W: "), utterance.getPresentWordStructures());
            // Absent structure display
            HBox absentBox = new HBox();
            if (question != null) {
                absentBox.setSpacing(horizontalSpacing);
                absentBox.getChildren().add(new Label("Err: "));
                for (String structure : question.getTargets().keySet()) {
                    if (!utterance.getPresentUtteranceStructures().contains(structure) &&
                            !utterance.getPresentPhraseStructures().contains(structure) &&
                            !utterance.getPresentWordStructures().contains(structure)) {
                        Label label = new Label(structure);
                        label.setId("label_absent");
                        absentBox.getChildren().add(label);
                    }
                }
            }
            container.getChildren().addAll(clauseBox, phraseBox, wordBox, absentBox);
        }
    }

    public void displayPronunResult(Syllable syllable, VBox container) {
        container.getChildren().clear();
        Label presentLabel = new Label("Correct consonants: " + syllable.getConsonantsCorrectAsString());
        Label errorLabel = new Label("Error patterns: ");
        presentLabel.setFont(this.font);
        errorLabel.setFont(this.font);
        container.getChildren().addAll(presentLabel, errorLabel);

        int errorCount = 0;
        for (Map.Entry<ErrorPattern, Integer> entry : syllable.getErrorPatterns().entrySet()) {
            if (entry.getValue() == 0)
                continue;
            Label label = new Label(PronunItems.patternName.get(entry.getKey()));
            label.setFont(this.font);
            container.getChildren().add(label);
            errorCount++;
        }

        if (errorCount == 0)
            errorLabel.setText("Error pattern: None");
        container.setSpacing(verticalSpacing);
    }

    public void displayConsonantInventory(Map<String, String> comparedMap, GridPane container) {
        clearInventoryGrid(container);
        container.getStylesheets().add(ResultDisplayer.class.getResource(PropertyManager.getResourceProperty("inventory_css")).toString());

        for (String consonant : comparedMap.keySet()) {
            Label label = new Label(consonant);
            int column = 0;
            if (comparedMap.get(consonant).equalsIgnoreCase("green")) {
                column = 1;
                label.setId("green_label");
            } else if (comparedMap.get(consonant).equalsIgnoreCase("red")) {
                column = 2;
                label.setId("red_label");
            } else if (comparedMap.get(consonant).equalsIgnoreCase("normal_present")) {
                column = 1;
                label.setId("normal_label");
            } else if (comparedMap.get(consonant).equalsIgnoreCase("normal_absent")) {
                column = 2;
                label.setId("normal_label");
            }

            HBox box = null;
            switch (PronunItems.consonantType.get(consonant)) {
                case "plosive":
                    box = (HBox) getNodeByRowColumnIndex(1, column, container);
                    box.getChildren().add(label);
                    break;
                case "nasal":
                    box = (HBox) getNodeByRowColumnIndex(2, column, container);
                    box.getChildren().add(label);
                    break;
                case "affricate":
                    box = (HBox) getNodeByRowColumnIndex(3, column, container);
                    box.getChildren().add(label);
                    break;
                case "fricative":
                    box = (HBox) getNodeByRowColumnIndex(4, column, container);
                    box.getChildren().add(label);
                    break;
                case "approxi":
                    box = (HBox) getNodeByRowColumnIndex(5, column, container);
                    box.getChildren().add(label);
                    break;
                case "l_approxi":
                    box = (HBox) getNodeByRowColumnIndex(6, column, container);
                    box.getChildren().add(label);
                    break;
                default:
                    break;
            }
        }
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

        structureBox.getChildren().add(segment);
        if (!entry.getValue().equals("标点"))
            structureBox.getChildren().addAll(separator, value);
        return structureBox;
    }

    private HBox getPresentHBox(Label title, List<String> present) {
        System.out.println(present);
        HBox box = new HBox();
        box.setSpacing(horizontalSpacing);
        box.getChildren().add(title);
        for (String structure : present) {
            Label label = new Label(structure);
            label.setId("label_present");
            box.getChildren().add(label);
        }
        return box;
    }

    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        for (Node node : gridPane.getChildren()) {
            if (node instanceof HBox && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }

    private void clearInventoryGrid(GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof HBox)
                ((HBox) node).getChildren().clear();
        }
    }
}
