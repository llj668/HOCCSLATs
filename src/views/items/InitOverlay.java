package views.items;

import com.jfoenix.controls.JFXSpinner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class InitOverlay extends VBox {

    public InitOverlay() {
        setPrefWidth(1280);
        setPrefHeight(630);
        setSpacing(20);
        setLayoutY(90);
        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        setOpacity(0.85);

        JFXSpinner spinner = new JFXSpinner();
        Label label = new Label("Initializing...");
        label.setFont(Font.font("System", 20));
        this.getChildren().addAll(spinner, label);
        this.setAlignment(Pos.CENTER);
    }
}
