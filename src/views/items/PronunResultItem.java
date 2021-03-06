package views.items;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import models.test.results.GrammarResult;
import models.test.results.PronunResult;

public class PronunResultItem extends ResultItem {
    private Label age;
    private Label time;
    private Label pcc;

    public PronunResultItem(PronunResult pronunResult) {
        super(null, pronunResult);
        initializeItemContent();
    }

    private void initializeItemContent() {
        age = new Label("Test age: " + pronunResult.testAge.toString());
        age.setPrefSize(100, 30);
        age.setFont(Font.font("System", 15));

        time = new Label("Test time: " + pronunResult.getTestTime());
        time.setTranslateY(35);
        time.setPrefSize(250, 30);
        time.setFont(Font.font("System", 15));

        pcc = new Label("PCC: " + pronunResult.pcc);
        pcc.setTranslateY(70);
        pcc.setPrefSize(100, 30);
        pcc.setFont(Font.font("System", 15));

        this.getChildren().addAll(age, time, pcc);
    }
}
