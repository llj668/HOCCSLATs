package models.profiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import views.items.ProfileListItem;

public class ProfileLoader {
	public static HashMap<ProfileListItem, Profile> profiles;
	
	public void refreshProfileList() {
		profiles = new HashMap<>();
		for (File file : new File(ProfileReader.PROFILE_PATH).listFiles()) {
			Profile profile = ProfileReader.readProfileFromXML(file);
			profiles.put(profile.toProfileListItem(), profile);
		}
	}
	
	public static void deleteProfiles() {
		for (ProfileListItem item : profiles.keySet()) {
			if (item.isSelectedForDelete) {
				new File(ProfileReader.PROFILE_PATH + profiles.get(item).getProfileName()).delete();
			}
		}
	}
	
	public void writeProfileToFile() {
		
	}

}
