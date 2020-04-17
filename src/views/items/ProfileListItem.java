package views.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import controllers.ProfileController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import models.profiles.Age;
import models.profiles.ProfileLoader;
import views.ViewManager;

public class ProfileListItem extends HBox {
	private JFXCheckBox selDelete;
	private Label name;
	private Label age;
	private Label gender;
	private HBox ageContainer;
	public Boolean isSelectedForDelete = false;

	public ProfileListItem(String nameStr, String genderStr, List<Age> agesList) {
		this.getStylesheets().add(ProfileListItem.class.getResource("/resources/styles/profileListItemStyle.css").toString());
		this.setId("item");
		this.setAlignment(Pos.CENTER_LEFT);
		initializeItemContent(nameStr, genderStr, agesList);
		this.getChildren().addAll(name, gender, age, ageContainer);
	}
	
	private void initializeItemContent(String nameStr, String genderStr, List<Age> agesList) {
		name = new Label("Name: " + nameStr);
		name.setPrefSize(150, 50);
		name.setTranslateX(50);
		name.setFont(Font.font("System", 20));

		age = new Label("Test age: ");
		age.setPrefSize(100, 50);
		age.setTranslateX(200);
		age.setFont(Font.font("System", 20));

		ageContainer = new HBox();
		ageContainer.setPrefSize(400, 50);
		ageContainer.setTranslateX(200);
		ageContainer.setSpacing(10);
		ageContainer.setId("age_container");
		ageContainer.setAlignment(Pos.CENTER_LEFT);
		if (agesList.size() == 0) {
			Label label = new Label("None");
			label.setPrefSize(40, 30);
			label.setFont(Font.font("System", 15));
			label.setAlignment(Pos.CENTER);
			ageContainer.getChildren().add(label);
		} else {
			for (Age age : agesList) {
				Label label = new Label(age.toString());
				label.setPrefSize(40, 30);
				label.setFont(Font.font("System", 15));
				label.setAlignment(Pos.CENTER);
				ageContainer.getChildren().add(label);
			}
		}

		gender = new Label("Gender: " + genderStr);
		gender.setPrefSize(200, 50);
		gender.setTranslateX(200);
		gender.setFont(Font.font("System", 20));
		
		selDelete = new JFXCheckBox("Delete");
		selDelete.setTranslateX(200);
		selDelete.setPrefSize(USE_COMPUTED_SIZE, 50);
		selDelete.setFont(Font.font("System", 20));
		selDelete.setCheckedColor(Color.CRIMSON);
		selDelete.selectedProperty().addListener((arg0, oldValue, newValue) -> isSelectedForDelete = newValue);
	}
	
	public void displayDeleteCheckbox() {
		this.getChildren().add(selDelete);
	}
	
	public void hideDeleteCheckbox() {
		this.getChildren().remove(selDelete);
	}
	
	public void clearDeleteSelect() {
		selDelete.setSelected(false);
	}
}
