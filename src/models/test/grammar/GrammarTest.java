package models.test.grammar;

import application.PropertyManager;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.dependency.IDependencyParser;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.hankcs.hanlp.dependency.perceptron.parser.KBeamArcEagerDependencyParser;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import controllers.BaseTestController;
import controllers.items.BaseSummaryController;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.test.*;
import models.test.reader.InventoryReader;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
//
import org.apache.commons.lang3.StringUtils;
//
import views.ViewManager;
import views.items.InitOverlay;
import views.items.ProcessingOverlay;

import java.io.*;
import java.util.*;

public class GrammarTest extends Assessment {
	private GrammarResult results;
	private Queue<String> testQueue;
	private Utterance utterance;
	private BaseTestController controller;
	private IDependencyParser parser = null;
	private DocVectorModel docVectorModel = null;
	private Map<String, Integer> marked;

	public GrammarTest(BaseTestController controller, Queue<String> testQueue) {
		super();
		this.testQueue = testQueue;
		this.controller = controller;
		this.results = new GrammarResult(AssessmentManager.getInstance().getTestAge());
		this.getQuestionList();
		this.initGrammarStages();
		this.setInitOverlay(controller);
	}

	private void initGrammarStages() {
		for (String str : testQueue)
			results.stageResults.add(new GrammarStage(Integer.parseInt(str)));
		for (Question question : questionList) {
			for (Map.Entry<String, Integer> target : question.getTargets().entrySet()) {
				if (results.getGrammarStage(target.getValue()) == null)
					continue;
				results.getGrammarStage(target.getValue()).addRecord(new GrammarStructure(target.getKey()), null);
			}
		}
	}

	@Override
	public Response analyzeResponse(String response, boolean showInBox) {
		utterance = new Utterance(response);
		ProcessingOverlay overlay = new ProcessingOverlay();
		if (showInBox) {
			controller.resultBox.getChildren().add(overlay);
			controller.btnAnalyze.setDisable(true);
			controller.btnNext.setDisable(true);
		}
		new Thread(() -> {
			List<Map.Entry<String, String>> analyzed_c = UtteranceBuilder.analyzeClause(response, parser);
			List<Map.Entry<String, String>> analyzed_p = UtteranceBuilder.analyzePhrase(response, parser);
			List<Map.Entry<String, String>> analyzed_w = UtteranceBuilder.analyzeWord(response, parser);
			utterance.setAnalyzedUtterance(analyzed_c);
			utterance.setAnalyzedPhrase(analyzed_p);
			utterance.setAnalyzedWord(analyzed_w);
			setMarked(UtteranceMarker.mark(AssessmentManager.getInstance().getQuestion(), this.utterance, docVectorModel));
			Platform.runLater(() -> {
				if (showInBox) {
					controller.btnAnalyze.setDisable(false);
					controller.btnNext.setDisable(false);
					controller.resultBox.getChildren().remove(overlay);
					controller.displayer.displayGrammarResult(utterance, AssessmentManager.getInstance().getQuestion(), controller.resultBox);
				} else {
					controller.getNextQuestion();
				}
			});
		}).start();
		return utterance;
	}

	@Override
	public void getQuestionList() {
		File[] files = new File(PropertyManager.getResourceProperty("grammar_question")).listFiles();
		for (File file : files) {
			if (StringUtils.substringAfter(file.getName(), ".").equals("jpg")) {
				Question question = InventoryReader.readQuestionFromXML(
						file.getPath(), StringUtils.substringBefore(file.getName(), "."), results.testAge);
				if (question == null)
					continue;
				for (String stageString : testQueue) {
					if (question.getStage().contains(stageString)) {
						questionList.add(question);
						InventoryReader.getSampleAnswersFromXML(question);
					}
				}
			}
		}
	}

	@Override
	public void writeResult(BaseTestController controller, Question question) {
		if (question != null) {
			results.updateResult(marked, this.utterance, question);
		}
	}

	@Override
	public void saveResult() {
		results.conclude();
	}

	@Override
	public Map.Entry<Region, BaseSummaryController> end() {
		return ViewManager.getInstance().getItemFromFXML(PropertyManager.getResourceProperty("grammarsum"));
	}

	private void setInitOverlay(BaseTestController controller) {
		InitOverlay overlay = new InitOverlay();
		new Thread(() -> {
			try {
				controller.root.getChildren().add(overlay);
				PerceptronLexicalAnalyzer segment = new PerceptronLexicalAnalyzer();
				parser = new NeuralNetworkDependencyParser();
				docVectorModel = new DocVectorModel(new WordVectorModel(PropertyManager.getResourceProperty("word2vec_path")));
				parser.setSegment(segment);
				docVectorModel.setSegment(segment);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				controller.isDataFound = false;
				Platform.runLater(() -> {
					controller.root.getChildren().remove(overlay);
					if (!controller.isDataFound)
						controller.onDataNotFound();
				});
			}
			Platform.runLater(() -> controller.root.getChildren().remove(overlay));
		}).start();
	}

	@Override
	public BaseResult getResult() {
		return results;
	}

	public GrammarResult getResults() {
		return results;
	}

	public void setUtterance(Utterance utterance) {
		this.utterance = utterance;
	}

	public void setMarked(Map<String, Integer> marked) {
		this.marked = marked;
	}
}
