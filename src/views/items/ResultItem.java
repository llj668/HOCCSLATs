package views.items;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;

public class ResultItem extends AnchorPane {

    public ResultItem() {
        Canvas canvas = new Canvas();
        canvas.setHeight(100);
        this.getChildren().add(canvas);
    }

}
