package models.test.results;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GrammarResult {
	public Date testTime;
	public String testAge;
	public double scoreOverall;
	public Set<GrammarStage> stageResults;

	public GrammarResult() {
		stageResults = new HashSet<>();
	}
	
	public GrammarResult(Set<GrammarStage> stageResults) {
		this.stageResults = stageResults;
	}

}
