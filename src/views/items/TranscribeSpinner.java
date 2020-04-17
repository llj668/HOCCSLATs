package views.items;

import com.jfoenix.controls.JFXSpinner;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TranscribeSpinner extends HBox {

    public TranscribeSpinner() {
        setLayoutX(1000);
        setLayoutY(120);
        JFXSpinner spinner = new JFXSpinner();
        spinner.setRadius(12);
        Label label = new Label("Transcribing...");
        setSpacing(15);
        getChildren().addAll(spinner, label);
        this.setAlignment(Pos.CENTER);
    }

}
