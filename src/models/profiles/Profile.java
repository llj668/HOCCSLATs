package models.profiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import models.test.results.GrammarResult;
import models.test.results.PronunResult;
import views.items.ProfileListItem;

public class Profile {
	private HashMap<String, String> info;	// name, ages, gender, profileName
	private ArrayList<GrammarResult> grammarResults;
	private ArrayList<PronunResult> pronunResults;

	public Profile(HashMap<String, String> info) {
		this.grammarResults = new ArrayList<>();
		this.pronunResults = new ArrayList<>();
		this.info = info;
	}

	public ProfileListItem toProfileListItem() {
		return new ProfileListItem(info);
	}

	public String getProfileName() {
		return info.get("profileName");
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

	public ArrayList<GrammarResult> getGrammarResults() {
		return grammarResults;
	}

	public void setGrammarResults(ArrayList<GrammarResult> grammarResults) {
		this.grammarResults = grammarResults;
	}

	public ArrayList<PronunResult> getPronunResults() {
		return pronunResults;
	}

	public void setPronunResults(ArrayList<PronunResult> pronunResults) {
		this.pronunResults = pronunResults;
	}
}
