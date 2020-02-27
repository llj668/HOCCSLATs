package controllers.items;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;
import controllers.BaseTestController;
import controllers.DialogControl;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import models.profiles.Profile;
import models.test.results.BaseResult;
import views.ViewManager;
import views.items.ConfirmDialog;

public abstract class BaseSummaryController extends ItemController implements DialogControl {
    public ConfirmDialog confirmDialog;

    @FXML
    public AnchorPane pane;
    @FXML
    public StackPane stackPane;
    @FXML
    public JFXButton btnDiscard;
    @FXML
    public JFXButton btnSave;

    @Override
    public void onClickNoDialog() {
        ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("testmenu"));
    }

    @Override
    public void onClickYesDialog() {
        if (confirmDialog.isSingleAction) {
            if (pane.getChildren().contains(btnDiscard) && pane.getChildren().contains(btnSave))
                ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("testmenu"));
        }
        confirmDialog.close();
        stackPane.toBack();
    }

    public abstract void setResult(BaseResult result, Profile profile);
    public abstract void setOnAfterTest(BaseTestController controller);
}
