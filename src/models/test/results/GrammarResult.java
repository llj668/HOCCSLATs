package models.test.results;

import application.PropertyManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.profiles.Age;
import models.test.Question;
import models.test.grammar.Utterance;
import models.test.reader.InventoryReader;
import org.apache.commons.lang3.StringUtils;
import views.items.GrammarResultItem;
import views.items.ResultItem;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GrammarResult extends BaseResult {
	public double score;
	public List<GrammarStage> stageResults;
	public Map<Question, Utterance> questions;
	public HashMap<GrammarStructure, Utterance> unscored;
	public boolean isAllScored = true;

	public GrammarResult(Age testAge) {
		super(testAge);
		stageResults = new LinkedList<>();
		questions = new LinkedHashMap<>();
	}
	
	public GrammarResult(List<GrammarStage> stageResults, Date testTime, Age testAge, Map<String, Utterance> ids) {
		super(testAge);
		this.stageResults = stageResults;
		this.testTime = testTime;
		this.unscored = unscoredStructures();
		this.questions = getQuestions(ids);
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

	public GrammarStage getGrammarStage(int index) {
		for (GrammarStage stage : stageResults) {
			if (stage.getStageNo() == index)
				return stage;
		}
		return null;
	}

	private LinkedHashMap<Question, Utterance> getQuestions(Map<String, Utterance> ids) {
		LinkedHashMap<Question, Utterance> questions = new LinkedHashMap<>();
		File[] files = new File(PropertyManager.getResourceProperty("grammar_question")).listFiles();
		for (File file : files) {
			if (StringUtils.substringAfter(file.getName(), ".").equals("jpg")) {
				Question question = InventoryReader.readQuestionFromXML(
						file.getPath(), StringUtils.substringBefore(file.getName(), "."), testAge);
				if (question == null || !ids.containsKey(question.getId()))
					continue;
				questions.put(question, ids.get(question.getId()));
			}
		}
		return questions;
	}

	public void updateResult(Map<String, Integer> marked, Utterance utterance, Question question) {
		questions.put(question, utterance);
		for (Map.Entry<String, Integer> markedStructure : marked.entrySet()) {
			for (GrammarStage stage : stageResults) {
				for (Map.Entry<GrammarStructure, Utterance> record : stage.getRecords().entrySet()) {
					if (record.getKey().name.equals(markedStructure.getKey())) {
						record.getKey().score = markedStructure.getValue();
						record.setValue(utterance);
					}
				}
			}
		}
	}

	@Override
	public ResultItem toResultItem() {
		return new GrammarResultItem(this);
	}

	public Question getQuestion(Utterance utterance) {
		for (Question question : questions.keySet()) {
			if (questions.get(question) == utterance)
				return question;
		}
		return null;
	}

	public StringProperty getUtteranceProperty(Question question) {
		String utterance = questions.get(question).getUtterance();
		if (utterance.equals(""))
			return new SimpleStringProperty("无");
		else if (utterance.equals("全部或部分重复"))
			return new SimpleStringProperty("全部或部分重复");
		else
			return new SimpleStringProperty(utterance);
	}

}
