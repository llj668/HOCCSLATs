package models.test.results;

import models.test.grammar.Utterance;
import org.jetbrains.annotations.NotNull;
import views.items.GrammarResultItem;
import views.items.ResultItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GrammarResult extends BaseResult implements Comparable<GrammarResult> {
	public double score;
	public List<GrammarStage> stageResults;

	public GrammarResult(String testAge) {
		super(testAge);
		stageResults = new LinkedList<>();
	}
	
	public GrammarResult(List<GrammarStage> stageResults, Date testTime, String testAge, String testScore) {
		super(testAge);
		this.stageResults = stageResults;
		this.testTime = testTime;
		this.score = Double.parseDouble(testScore);
	}

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

	public ResultItem toGrammarResultItem() {
		return new GrammarResultItem(this);
	}

	public String getTestTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(testTime);
	}

	@Override
	public int compareTo(@NotNull GrammarResult o) {
		if (o.testTime.getTime() > this.testTime.getTime()) {
			return 1;
		} else {
			return -1;
		}
	}
}
