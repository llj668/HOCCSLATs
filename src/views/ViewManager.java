package views;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ViewManager {
	public final static String PATH_MAIN = "/views/MainMenu.fxml";
	public final static String PATH_PROFILE = "/views/Profile.fxml";
	public final static String PATH_NEWPROFILE = "/views/NewProfile.fxml";
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
            AnchorPane root = (AnchorPane) loader.load();
            
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
            this.stage.setScene( scene );
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
