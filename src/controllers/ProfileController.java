package controllers;

import java.util.Set;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import profiles.ProfileLoader;
import views.ViewManager;
import views.items.ProfileListItem;

public class ProfileController {
	private Set<ProfileListItem> profileItems;
	
	@FXML
	private AnchorPane root;
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
		new ProfileLoader().refreshProfileList();
		profileList.setCellFactory(lv -> new JFXListCell<ProfileListItem>() {
            @Override
            public void updateItem(ProfileListItem item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                	setGraphic(item);
                }
            }
		});
		displayProfileList();
	}
	
	private void displayProfileList() {
		profileItems = ProfileLoader.profiles.keySet();
		profileList.getItems().clear();
		profileList.getItems().addAll(profileItems);
		profileList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProfileListItem>() {
			@Override
			public void changed(ObservableValue<? extends ProfileListItem> arg0, ProfileListItem oldValue, ProfileListItem newValue) {
				if (newValue != null) {
					newValue.onSelectForView();
				}
                if (oldValue != null) {
                	oldValue.onLoseFocus();
                }
            }
		});
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_MAIN);
	}
	
	@FXML
	void onClickNew(ActionEvent event) {
		ViewManager.getInstance().switchScene(ViewManager.PATH_NEWPROFILE);
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


}
