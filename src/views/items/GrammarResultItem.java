package views.items;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import models.test.results.GrammarResult;

public class GrammarResultItem extends ResultItem {
    private GrammarResult result;
    private Label age;

    public GrammarResultItem(GrammarResult result) {
        super();
        this.result = result;
        initializeItemContent();
        this.getChildren().addAll(age);
    }

    private void initializeItemContent() {
        age = new Label("测试年龄：" + result.testAge);
        age.setPrefSize(100, 30);
        age.setFont(Font.font("System", 15));
    }

}
