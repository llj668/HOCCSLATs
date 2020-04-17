package controllers;

import java.util.Map;
import java.util.Set;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import models.profiles.Age;
import models.profiles.Profile;
import models.profiles.ProfileLoader;
import models.test.AssessmentManager;
import views.items.EnterAgeDialog;
import views.items.ProfileListItem;

public class ProfileController extends BaseController implements DialogControl {
	private Set<ProfileListItem> profileItems;
	private EnterAgeDialog ageDialog;
	private boolean isBeforeAssessment = false;
	
	@FXML
	private AnchorPane root;
	@FXML
	private StackPane stackPane;
	@FXML
	private Label header;
	@FXML
	private JFXButton btnBack;
	@FXML
	private JFXButton btnNew;
	@FXML
	private JFXButton btnDelete;
	@FXML
	private JFXButton btnCancel;
	@FXML
	private JFXButton btnConfirm;
	@FXML
	private JFXListView<ProfileListItem> profileList;
	
	public void initialize() {
		NewProfileController.isBeforeAssessment = false;
		profileList.setCellFactory(lv -> new JFXListCell<ProfileListItem>() {
            @Override
            public void updateItem(ProfileListItem item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                	setGraphic(item);
					setOnMouseClicked(mouseClickedEvent -> {
						if (mouseClickedEvent.getButton().equals(MouseButton.PRIMARY) && mouseClickedEvent.getClickCount() == 2) {
							if (isBeforeAssessment)
								onSelectProfileForAssessment(ProfileLoader.profiles.get(item));
							else
								displayProfileScene(ProfileLoader.profiles.get(item));
						}
					});
                }
            }
		});
		new ProfileLoader().refreshProfileList();
		displayProfileList();
	}
	
	private void displayProfileList() {
		profileItems = ProfileLoader.profiles.keySet();
		profileList.getItems().clear();
		profileList.getItems().addAll(profileItems);
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		displayScene(PropertyManager.getResourceProperty("mainmenu"));
	}
	
	@FXML
	void onClickNew(ActionEvent event) {
		displayScene(PropertyManager.getResourceProperty("newprofile"));
	}

	@FXML
	void onClickDelete(ActionEvent event) {
		btnCancel.toFront();
		btnConfirm.toFront();
		for (ProfileListItem item : profileList.getItems()) {
			item.displayDeleteCheckbox();
		}
	}
	
	@FXML
	void onClickConfirmDelete(ActionEvent event) {
		ProfileLoader.deleteProfiles();
		new ProfileLoader().refreshProfileList();
		displayProfileList();
		exitDeleteMode();
	}
	
	@FXML
	void onClickCancelDelete(ActionEvent event) {
		exitDeleteMode();
		for (ProfileListItem item : profileList.getItems()) {
			item.clearDeleteSelect();
		}
	}
	
	private void exitDeleteMode() {
		btnCancel.toBack();
		btnConfirm.toBack();
		for (ProfileListItem item : profileList.getItems()) {
			item.hideDeleteCheckbox();
		}
	}
	
	public void updateControllerBeforeAssessment() {
		NewProfileController.isBeforeAssessment = true;
		root.getChildren().removeAll(btnDelete, btnCancel, btnConfirm);
		isBeforeAssessment = true;
	}
	
	public void onSelectProfileForAssessment(Profile profile) {
		AssessmentManager.profile = profile;
		stackPane.toFront();
		ageDialog = new EnterAgeDialog(this, stackPane, new JFXDialogLayout());
		ageDialog.show();
	}

	@Override
	public void onClickNoDialog() {
		AssessmentManager.profile = null;
		ageDialog.close();
		stackPane.toBack();
	}

	@Override
	public void onClickYesDialog() {
		AssessmentManager.getInstance().setTestAge(new Age(ageDialog.getText()));
		displayScene(PropertyManager.getResourceProperty("testmenu"));
	}

}
