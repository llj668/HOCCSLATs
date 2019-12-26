package models.test.results;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GrammarResult {
	public double correctOverall;
	public double correctStage1;
	public double correctStage2;
	public double correctStage3;
	public double correctStage4;
	public Set<GrammarStructure> stage1;
	public Set<GrammarStructure> stage2;
	public Set<GrammarStructure> stage3;
	public Set<GrammarStructure> stage4;

	public GrammarResult() {
		stage1 = new HashSet<>();
		stage2 = new HashSet<>();
		stage3 = new HashSet<>();
		stage4 = new HashSet<>();
	}
	
	public GrammarResult(Set<GrammarStructure> stage1, Set<GrammarStructure> stage2, Set<GrammarStructure> stage3, Set<GrammarStructure> stage4) {
		this.stage1 = stage1;
		this.stage2 = stage2;
		this.stage3 = stage3;
		this.stage4 = stage4;
	}

}
