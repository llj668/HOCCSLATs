package models.test.results;

import models.test.Response;
import org.jetbrains.annotations.NotNull;
import views.items.GrammarResultItem;
import views.items.ResultItem;

import java.text.SimpleDateFormat;
import java.util.*;

public class GrammarResult extends BaseResult implements Comparable<GrammarResult> {
	public Date testTime;
	public String testAge;
	public double score;
	public List<GrammarStage> stageResults;

	public GrammarResult(String testAge) {
		stageResults = new LinkedList<>();
		this.testAge = testAge;
	}
	
	public GrammarResult(List<GrammarStage> stageResults, Date testTime, String testAge, String testScore) {
		this.stageResults = stageResults;
		this.testTime = testTime;
		this.testAge = testAge;
		this.score = Double.parseDouble(testScore);
	}

	public void conclude() {
		testTime = new Date();
		double scoreTotal = 0;
		for (GrammarStage stage : stageResults) {
			double stageTotalScore = 0;
			for (Map.Entry<Response, GrammarStructure> entry : stage.getRecords().entrySet()) {
				stageTotalScore += entry.getValue().score;
			}
			double stageScore = stageTotalScore / stage.getRecords().size();
			stage.setStageScore(stageScore);
			scoreTotal += stageScore;
		}
		score = scoreTotal / stageResults.size();
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
