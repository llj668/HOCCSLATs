package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import views.ViewManager;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.initStyle(StageStyle.UTILITY);
			primaryStage.setResizable(false);
			
			ViewManager viewManager = ViewManager.getInstance(primaryStage);
			viewManager.switchScene(ViewManager.PATH_MAIN);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
