package profiles;

import java.util.HashMap;

import views.items.ProfileListItem;

public class Profile {
	private String name;
	private String ages;
	private String gender;
	private String profileName;
	public Boolean isSelectedForDelete = false;

	public Profile(String name, String ages, String gender, String fileName) {
		this.name = name;
		this.ages = ages;
		this.gender = gender;
		this.profileName = fileName;
	}

	public ProfileListItem toProfileListItem() {
		HashMap<String, String> cmd = new HashMap<String, String>(); 
		cmd.put("name", name);
		cmd.put("ages", ages);
		cmd.put("gender", gender);
		return new ProfileListItem(cmd);
	}

	public String getProfileName() {
		return profileName;
	}
}
