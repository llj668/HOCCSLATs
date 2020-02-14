package controllers.items;

import application.PropertyManager;
import controllers.BaseTestController;
import controllers.DialogControl;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import models.test.results.BaseResult;
import views.ViewManager;
import views.items.ConfirmDialog;

public abstract class BaseSummaryController extends ItemController implements DialogControl {
    public ConfirmDialog confirmDialog;

    @FXML
    public StackPane stackPane;

    @Override
    public void onClickNoDialog() {
        ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("testmenu"));
    }

    @Override
    public void onClickYesDialog() {
        if (confirmDialog.isSingleAction) {
            ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("testmenu"));
        }
        confirmDialog.close();
        stackPane.toBack();
    }

    public abstract void setResult(BaseResult result);
    public abstract void setOnAfterTest(BaseTestController controller);
}
