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

	public GrammarResult(Age testAge) {
		super(testAge);
		stageResults = new LinkedList<>();
	}
	
	public GrammarResult(List<GrammarStage> stageResults, Date testTime, Age testAge, String testScore) {
		super(testAge);
		this.stageResults = stageResults;
		this.testTime = testTime;
		this.score = Double.parseDouble(testScore);
	}

	@Override
	public void conclude() {
		testTime = new Date();
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

	@Override
	public ResultItem toResultItem() {
		return new GrammarResultItem(this);
	}

}
