package views;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import application.PropertyManager;
import controllers.ProfileController;
import controllers.ViewProfileController;
import controllers.items.BaseSummaryController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import models.profiles.Profile;
import models.services.TempMonitor;

public class ViewManager {
	final int initWidth = 1280;
	final int initHeight = 720;
	
	private Stage stage;
	private static ViewManager instance;
	
    synchronized public static ViewManager getInstance(Stage stage) {
		if(instance == null){
			instance = new ViewManager(stage);
		}   	
    	return instance;
	}
    
    synchronized public static ViewManager getInstance() {
    	return instance;
	}

	private ViewManager(Stage stage) {
		this.stage = stage;
	}
	
	public void switchScene(String path) {
		FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource(path));
            AnchorPane root = loader.load();
            this.stage.setScene( setDecoratedScene(root) );
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void switchProfileSelectScene() {
		FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource(PropertyManager.getResourceProperty("profile")));
            AnchorPane root = loader.load();
            ProfileController controller = loader.getController();
            controller.updateControllerBeforeAssessment();
            
            this.stage.setScene( setDecoratedScene(root) );
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void switchProfileViewScene(Profile profile) {
		FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource(PropertyManager.getResourceProperty("viewprofile")));
            AnchorPane root = loader.load();
            ViewProfileController controller = loader.getController();
            controller.displayProfile(profile);
            
            this.stage.setScene( setDecoratedScene(root) );
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public Map.Entry<Region, BaseSummaryController> getItemFromFXML(String path) {
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource(path));
            AnchorPane root = loader.load();
            BaseSummaryController controller = loader.getController();

            return new AbstractMap.SimpleEntry<>(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	private Scene setDecoratedScene(AnchorPane root) {
        VBox sceneRoot = new VBox();
        sceneRoot.getStylesheets().add(ViewManager.class.getResource(PropertyManager.getResourceProperty("stage_css")).toString());
        sceneRoot.setId("scene_root");

        VBox top = new VBox();
        top.setId("top");
        top.setPrefSize(1280,50);

        AnchorPane title = new AnchorPane();
        Rectangle close = new Rectangle();
        close.setWidth(40);
        close.setHeight(40);
        close.setId("win_close");
        close.setFill(new ImagePattern(new Image(PropertyManager.getResourceProperty("winclose_img"))));
        close.setOnMouseEntered(event -> close.setFill(new ImagePattern(new Image(PropertyManager.getResourceProperty("winclosepress_img")))));
        close.setOnMouseExited(event -> close.setFill(new ImagePattern(new Image(PropertyManager.getResourceProperty("winclose_img")))));
        close.setOnMouseClicked(event -> {
            TempMonitor.getInstance().stop();
            Platform.exit();
        });
        title.getChildren().add(close);
        AnchorPane.setRightAnchor(close, 10.0);
        AnchorPane.setTopAnchor(close, 5.0);
        top.getChildren().add(title);

        sceneRoot.getChildren().addAll(top, root);
        DragUtil.addDragListener(stage, top);
        return new Scene( sceneRoot );
    }
	
	private Scene setAutoResizeScene(AnchorPane root) {
        Scale scale = new Scale(1, 1, 0, 0);
        scale.xProperty().bind(root.widthProperty().divide(initWidth));
        scale.yProperty().bind(scale.xProperty());
        root.getTransforms().add(scale);
        
        Scene scene = new Scene( root );
        scene.rootProperty().addListener(new ChangeListener<Parent>() {
            @Override 
            public void changed(ObservableValue<? extends Parent> arg0, Parent oldValue, Parent newValue){
                scene.rootProperty().removeListener(this);
                scene.setRoot(root);
                ((Region) newValue).setPrefWidth(initWidth);
                ((Region) newValue).setPrefHeight(initHeight);
                root.getChildren().clear();
                root.getChildren().add(newValue);
                scene.rootProperty().addListener(this);
            }
        });
        return scene;
	}

}
