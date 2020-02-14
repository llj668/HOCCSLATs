package controllers;

import application.PropertyManager;
import com.jfoenix.controls.JFXButton;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import controllers.items.BaseSummaryController;
import controllers.items.GrammarSummaryController;
import controllers.items.ItemController;
import controllers.items.PronunSummaryController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import models.profiles.Profile;
import models.test.results.GrammarResult;
import models.test.results.PronunResult;
import views.ViewManager;
import views.items.GrammarResultItem;
import views.items.ProfileListItem;
import views.items.ResultItem;

import java.util.Collections;
import java.util.Map;

public class ViewProfileController {

	@FXML
	private AnchorPane root;
	@FXML
	private JFXListView<ResultItem> grammarResultList;
	@FXML
	private JFXListView<ResultItem> pronunResultList;
	@FXML
	private JFXButton btnBack;
	@FXML
	private Label labelName;
	@FXML
	private Label labelGender;
	@FXML
	private Label labelRecent;

	private Region summary;
	
	public void initialize() {
		grammarResultList.setCellFactory(lv -> new JFXListCell<ResultItem>() {
			@Override
			public void updateItem(ResultItem item, boolean empty)
			{
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(item);
					setOnMouseClicked(mouseClickedEvent -> {
						if (mouseClickedEvent.getButton().equals(MouseButton.PRIMARY) && mouseClickedEvent.getClickCount() == 2) {
							Map.Entry<Region, BaseSummaryController> entry = ViewManager.getInstance().getItemFromFXML(
									PropertyManager.getResourceProperty("grammarsum"));
							summary = entry.getKey();
							summary.setLayoutY(90);
							GrammarSummaryController controller = (GrammarSummaryController) entry.getValue();
							root.getChildren().add(summary);
							controller.setResult(item.grammarResult);
							setBackBtnBehavior();
						}
					});
				}
			}
		});

		pronunResultList.setCellFactory(lv -> new JFXListCell<ResultItem>() {
			@Override
			public void updateItem(ResultItem item, boolean empty)
			{
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(item);
					setOnMouseClicked(mouseClickedEvent -> {
						if (mouseClickedEvent.getButton().equals(MouseButton.PRIMARY) && mouseClickedEvent.getClickCount() == 2) {
							Map.Entry<Region, BaseSummaryController> entry = ViewManager.getInstance().getItemFromFXML(
									PropertyManager.getResourceProperty("pronunsum"));
							summary = entry.getKey();
							summary.setLayoutY(90);
							PronunSummaryController controller = (PronunSummaryController) entry.getValue();
							root.getChildren().add(summary);
							controller.setResult(item.pronunResult);
							setBackBtnBehavior();
						}
					});
				}
			}
		});
	}
	
	public void displayProfile(Profile profile) {
		initProfileInfo(profile);
		for (GrammarResult result : profile.getGrammarResults()) {
			grammarResultList.getItems().add(result.toResultItem());
		}
		for (PronunResult result : profile.getPronunResults()) {
			pronunResultList.getItems().add(result.toResultItem());
		}
	}
	
	@FXML
	void onClickBack(ActionEvent event) {
		ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("profile"));
	}

	private void setBackBtnBehavior() {
		btnBack.setOnAction(e -> {
			root.getChildren().remove(summary);
			btnBack.setOnAction(event -> ViewManager.getInstance().switchScene(PropertyManager.getResourceProperty("profile")));
		});
	}

	private void initProfileInfo(Profile profile) {
		labelName.setText(profile.getName());
		labelGender.setText(profile.getGender());
		Collections.sort(profile.getGrammarResults());
		if (profile.getGrammarResults().size() == 0 || profile.getPronunResults().size() == 0) {
			labelRecent.setText("暂无");
		} else {
			labelRecent.setText(profile.getGrammarResults().get(0).getTestTime());
		}
	}
}
