package models.profiles;

import java.util.*;

import models.test.results.GrammarResult;
import models.test.results.PronunResult;
import views.items.ProfileListItem;

public class Profile {
	private String name;
	private String gender;
	private String profileName;
	private List<Age> ages;
	private ArrayList<GrammarResult> grammarResults;
	private ArrayList<PronunResult> pronunResults;

	public Profile(String name, String gender) {
		this.name = name;
		this.gender = gender;
	}

	public Profile(String name, String gender, String profileName, List<Age> ages) {
		this.name = name;
		this.gender = gender;
		this.profileName = profileName;
		this.ages = ages;
	}

	public ProfileListItem toProfileListItem() {
		return new ProfileListItem(this.name, getGender(), this.ages);
	}

	public String getName() {
		return this.name;
	}
	public String getProfileName() {
		return this.profileName;
	}
	public List<Age> getAges() {
		return this.ages;
	}
	public String getGenderString() {
		return this.gender;
	}
	public String getGender() {
		if (this.gender.equals("male")) {
			return "男";
		} else if (this.gender.equals("female")) {
			return "女";
		}
		return null;
	}

	public ArrayList<GrammarResult> getGrammarResults() {
		if (grammarResults == null) {
			this.grammarResults = new ArrayList<>();
			this.grammarResults = ProfileReader.readGrammarResultsFromXML(this.profileName);
		}
		return grammarResults;
	}

	public ArrayList<PronunResult> getPronunResults() {
		if (pronunResults == null) {
			this.pronunResults = new ArrayList<>();
			this.pronunResults = ProfileReader.readPronunResultsFromXML(this.profileName);
		}
		return pronunResults;
	}

}
