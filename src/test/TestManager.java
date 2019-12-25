package test;

import profiles.Profile;

public class TestManager {
	private static Profile profile;
	private static TestManager instance;
	
    synchronized public static TestManager getInstance() {
		if(instance == null){
			instance = new TestManager();
		}   	
    	return instance;
	}
    
    private TestManager() {
    	
    }
    
    public static void setProfile(Profile profile) {
    	TestManager.profile = profile;
    }
}
