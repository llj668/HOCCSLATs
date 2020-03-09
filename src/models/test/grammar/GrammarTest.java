package models.test.grammar;

import application.PropertyManager;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.intellij.vcs.log.Hash;
import controllers.BaseTestController;
import controllers.items.BaseSummaryController;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.test.*;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
import org.apache.commons.lang3.StringUtils;
import views.ViewManager;
import views.items.InitOverlay;
import views.items.ProcessingOverlay;

import java.io.*;
import java.util.*;

public class GrammarTest extends Assessment {
	private GrammarResult results;
	private Queue<String> testQueue;
	private GrammarStage stage;
	private Utterance utterance;
	private String prevStage = "-1";
	private BaseTestController controller;
	private NeuralNetworkDependencyParser parser;
	private DocVectorModel docVectorModel;

	public GrammarTest(BaseTestController controller, Queue<String> testQueue) {
		super();
		this.testQueue = testQueue;
		this.controller = controller;
		this.results = new GrammarResult(AssessmentManager.getInstance().getTestAge());
		this.getQuestionList();
		this.setInitOverlay(controller);
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
			utterance.setAnalyzedUtterance(analyzed_c);
			utterance.setAnalyzedPhrase(analyzed_p);
			for (Map.Entry<String, String> entry : analyzed_c) {
				System.out.print(entry.getKey() + "（" + entry.getValue() + "）");
			}
			System.out.println();
			for (Map.Entry<String, String> entry : analyzed_p) {
				System.out.print(entry.getKey() + "（" + entry.getValue() + "）");
			}
			System.out.println();
			Platform.runLater(() -> {
				if (showInBox) {
					controller.btnAnalyze.setDisable(false);
					controller.btnNext.setDisable(false);
					controller.resultBox.getChildren().remove(overlay);
					controller.displayer.displayGrammarResult(utterance.getAnalyzedUtterance(), controller.resultBox);
				}
			});
		}).start();
		return utterance;
	}

	@Override
	public void getQuestionList() {
		File[] files = new File(PropertyManager.getResourceProperty("grammar_question")).listFiles();
		for (File file : files) {
			String[] str = StringUtils.substringBefore(file.getName(), ".").split("-");
			if (testQueue.contains(str[0]))
				questionList.add(new Question(file.getPath(), str[0], str[1]));
		}
	}

	@Override
	public void writeResult(BaseTestController controller, Question question) {
		if (question != null) {
			if (!prevStage.equalsIgnoreCase(question.getStage())) {
				if (stage != null)
					results.stageResults.add(stage);
				stage = new GrammarStage(Integer.parseInt(question.getStage()));
			}
			int score = UtteranceMarker.mark(question, this.utterance, docVectorModel);
			System.out.println("score: " + score);
			stage.addRecord(new GrammarStructure(question.getTarget(), score), this.utterance);
			prevStage = question.getStage();
		}
	}

	@Override
	public void saveResult() {
		results.stageResults.add(stage);
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
				parser = new NeuralNetworkDependencyParser();
				docVectorModel = new DocVectorModel(new WordVectorModel(PropertyManager.getResourceProperty("word2vec_path")));
			} catch (IOException e) {
				e.printStackTrace();
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
}
