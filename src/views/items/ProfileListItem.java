package views.items;

import java.util.HashMap;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import profiles.ProfileLoader;
import test.TestManager;
import views.ViewManager;

public class ProfileListItem extends AnchorPane {
	private JFXButton selButton;
	private JFXCheckBox selDelete;
	private HashMap<String, String> profileInfo;
	private Label name;
	private Label ages;
	private Label gender;
	private Boolean isDeleteMode = false;
	public Boolean isSelectedForDelete = false;

	public ProfileListItem(HashMap<String, String> info) {
		this.profileInfo = info;
		initializeItemContent();
		this.getChildren().addAll(name, ages, gender);
	}
	
	private void initializeItemContent() {
		name = new Label("姓名: " + profileInfo.get("name"));
		name.setPrefSize(150, 50);
		name.setTranslateX(50);
		name.setFont(Font.font("System", 20));
		
		ages = new Label("年龄: " + profileInfo.get("ages"));
		ages.setPrefSize(200, 50);
		ages.setTranslateX(250);
		ages.setFont(Font.font("System", 20));
		
		String genderString = profileInfo.get("gender") == "male" ? "男" : "女";
		gender = new Label("性别: " + genderString);
		gender.setPrefSize(200, 50);
		gender.setTranslateX(450);
		gender.setFont(Font.font("System", 20));
		
		selButton = new JFXButton("选择");
		selButton.setTranslateX(1100);
		selButton.setPrefSize(100, 50);
		selButton.setTextFill(Color.AZURE);
		selButton.setFont(Font.font("System", 20));
		selButton.setOnAction(event -> ViewManager.getInstance().switchProfileViewScene(ProfileLoader.profiles.get(this)));
		
		selDelete = new JFXCheckBox("删除");
		selDelete.setTranslateX(1100);
		selDelete.setPrefSize(USE_COMPUTED_SIZE, 50);
		selDelete.setFont(Font.font("System", 20));
		selDelete.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
                isSelectedForDelete = newValue;
            }
		});
	}

	public void setHandleSelectForAssessment() {
		selButton.setOnAction(event -> {
			ViewManager.getInstance().switchScene(ViewManager.PATH_TESTMENU);
			TestManager.setProfile(ProfileLoader.profiles.get(this));
		});
	}
	
	public void onSelectForView() {
		if (!isDeleteMode) {
			this.getChildren().add(selButton);
		}
	}
	
	public void onLoseFocus() {
		if (!isDeleteMode) {
			this.getChildren().remove(selButton);
		}
	}
	
	public void displayDeleteCheckbox() {
		isDeleteMode = true;
		this.getChildren().add(selDelete);
	}
	
	public void hideDeleteCheckbox() {
		isDeleteMode = false;
		this.getChildren().remove(selDelete);
	}
	
	public void clearDeleteSelect() {
		selDelete.setSelected(false);
	}
}
