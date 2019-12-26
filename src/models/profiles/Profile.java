package models.profiles;

import java.util.HashMap;

import views.items.ProfileListItem;

public class Profile {
	private HashMap<String, String> info;
	private String profileName;
	public Boolean isSelectedForDelete = false;

	public Profile(HashMap<String, String> info, String fileName) {
		this.info = info;
		this.profileName = fileName;
	}

	public ProfileListItem toProfileListItem() {
		return new ProfileListItem(info);
	}

	public String getProfileName() {
		return profileName;
	}

	public HashMap<String, String> getInfo() {
		return info;
	}
	
	public String getGender() {
		if (info.get("gender").equals("male")) {
			return "男";
		} else if (info.get("gender").equals("female")) {
			return "女";
		}
		return null;
	}
}
