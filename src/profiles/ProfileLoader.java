package profiles;

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
	final static String PROFILE_PATH = "./src/profiles/files/";
	
	public void refreshProfileList() {
		profiles = new HashMap<ProfileListItem, Profile>();
		for (File file : new File(PROFILE_PATH).listFiles()) {
			Profile profile = readProfileFromFile(file.getName());
			profiles.put(profile.toProfileListItem(), profile);
		}
	}
	
	public static void deleteProfiles() {
		for (ProfileListItem item : profiles.keySet()) {
			if (item.isSelectedForDelete) {
				new File(PROFILE_PATH + profiles.get(item).getProfileName()).delete();
			}
		}
	}

	private Profile readProfileFromFile(String fileName) {
		String name = null, ages = null, gender = null;
	    BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(PROFILE_PATH + fileName));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
        	    String[] info = tempString.split(",");
        	    switch (info[0]) {
				case "name":
					name = info[1];
					break;
				case "ages":
					ages = info[1];
					break;
				case "gender":
					gender = info[1];
					break;

				default:
					break;
				}
            } 			    
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		return new Profile(name, ages, gender, fileName);
	}
}
