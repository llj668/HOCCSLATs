package views.items;

import com.jfoenix.controls.JFXSpinner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ProcessingOverlay extends VBox {

    public ProcessingOverlay() {
        setPrefWidth(425);
        setPrefHeight(190);
        setSpacing(10);
        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        setOpacity(0.85);

        JFXSpinner spinner = new JFXSpinner();
        Label label = new Label("正在分析...");
        label.setFont(Font.font("System", 15));
        this.getChildren().addAll(spinner, label);
        this.setAlignment(Pos.CENTER);
    }
}
