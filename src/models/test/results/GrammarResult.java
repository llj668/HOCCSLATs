package models.test.results;

import models.profiles.Age;
import models.test.grammar.Utterance;
import org.jetbrains.annotations.NotNull;
import views.items.GrammarResultItem;
import views.items.ResultItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GrammarResult extends BaseResult {
	public double score;
	public List<GrammarStage> stageResults;
	public HashMap<GrammarStructure, Utterance> unscored;
	public boolean isAllScored = true;

	public GrammarResult(Age testAge) {
		super(testAge);
		stageResults = new LinkedList<>();
	}
	
	public GrammarResult(List<GrammarStage> stageResults, Date testTime, Age testAge) {
		super(testAge);
		this.stageResults = stageResults;
		this.testTime = testTime;
		this.unscored = unscoredStructures();
	}

	@Override
	public void conclude() {
		testTime = new Date();
		this.unscored = unscoredStructures();
	}

	public void calculateScores() {
		double scoreTotal = 0;
		DecimalFormat df = new DecimalFormat("#.00");
		for (GrammarStage stage : stageResults) {
			double stageTotalScore = 0;
			for (Map.Entry<GrammarStructure, Utterance> entry : stage.getRecords().entrySet()) {
				stageTotalScore += entry.getKey().score;
			}
			double stageScore = stageTotalScore / stage.getRecords().size();
			stage.setStageScore(Double.parseDouble(df.format(stageScore)));
			scoreTotal += stageScore;
		}
		score = Double.parseDouble(df.format(scoreTotal / stageResults.size()));
	}

	public HashMap<GrammarStructure, Utterance> unscoredStructures() {
		HashMap<GrammarStructure, Utterance> structures = new HashMap<>();
		for (GrammarStage stage : stageResults) {
			for (Map.Entry<GrammarStructure, Utterance> entry : stage.getRecords().entrySet()) {
				if (entry.getKey().score == -1) {
					structures.put(entry.getKey(), entry.getValue());
				}
			}
		}
		if (structures.size() != 0) {
			stageResults.add(new GrammarStage(structures));
			isAllScored = false;
		} else {
			calculateScores();
			isAllScored = true;
		}
		return structures;
	}

	@Override
	public ResultItem toResultItem() {
		return new GrammarResultItem(this);
	}

}
