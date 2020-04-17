package controllers;

import models.profiles.Profile;
import models.profiles.ProfileLoader;
import views.ViewManager;

public class BaseController {

    public void displayScene(String path) {
        ViewManager.getInstance().switchScene(path);
    }

    public void displayProfileSelectScene() {
        ViewManager.getInstance().switchProfileSelectScene();
    }

    public void displayProfileScene(Profile profile) {
        ViewManager.getInstance().switchProfileViewScene(profile);
    }
}
