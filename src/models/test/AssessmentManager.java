package models.test;

import models.profiles.Profile;
import models.test.grammar.GrammarTest;

public class AssessmentManager {
	public static Profile profile;
	private static AssessmentManager instance;
	private Assessment assessment;
	private String testAge;
	
    synchronized public static AssessmentManager getInstance() {
		if (instance == null) {
			instance = new AssessmentManager();
		}
    	return instance;
	}
    
    private AssessmentManager() {
    	
    }

    public void startGrammarAssessment() {
    	assessment = new GrammarTest();
	}

	public String getTestAge() {
		return testAge;
	}

	public void setTestAge(String testAge) {
		this.testAge = testAge;
	}

	public Assessment getAssessment() {
		return assessment;
	}
}
