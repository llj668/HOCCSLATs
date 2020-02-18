package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.services.TempMonitor;
import views.ViewManager;


public class Main extends Application {
	public static TempMonitor monitor;

	@Override
	public void start(Stage primaryStage) {
		new PropertyManager();
		try {
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setResizable(false);
			
			ViewManager viewManager = ViewManager.getInstance(primaryStage);
			viewManager.switchScene(PropertyManager.getResourceProperty("mainmenu"));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
