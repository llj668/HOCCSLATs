package views.items;

import com.jfoenix.controls.JFXSpinner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class InitializePane extends VBox {

    public InitializePane() {
        setPrefWidth(1280);
        setPrefHeight(630);
        setTranslateX(0);
        setTranslateY(90);
        setSpacing(20);
        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        setOpacity(0.85);

        JFXSpinner spinner = new JFXSpinner();
        Label label = new Label("正在初始化...");
        label.setFont(Font.font("System", 20));
        this.getChildren().addAll(spinner, label);
        this.setAlignment(Pos.CENTER);
    }
}
