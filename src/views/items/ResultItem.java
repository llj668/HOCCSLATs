package views.items;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import models.test.results.GrammarResult;
import models.test.results.PronunResult;

public class ResultItem extends AnchorPane {
    public GrammarResult grammarResult;
    public PronunResult pronunResult;

    public ResultItem(GrammarResult grammarResult, PronunResult pronunResult) {
        Canvas canvas = new Canvas();
        canvas.setHeight(100);
        this.getChildren().add(canvas);
        this.grammarResult = grammarResult;
        this.pronunResult = pronunResult;
    }

}
