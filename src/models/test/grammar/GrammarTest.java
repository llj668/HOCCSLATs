package models.test.grammar;

import application.PropertyManager;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import controllers.BaseTestController;
import controllers.items.BaseSummaryController;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import models.test.Assessment;
import models.test.AssessmentManager;
import models.test.Question;
import models.test.Response;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
import org.apache.commons.lang3.StringUtils;
import views.ViewManager;
import views.items.InitializePane;

import java.io.*;
import java.util.*;

public class GrammarTest extends Assessment {
	final static String TEMP_ANALYZE_FILE = "./src/models/services/temp/nlp_temp.xml";
	private GrammarResult results;
	private Queue<String> testQueue;
	private GrammarStage stage;
	private Utterance utterance;
	private String prevStage = "-1";

	public GrammarTest(AnchorPane root, Queue<String> testQueue) {
		super();
		this.testQueue = testQueue;
		this.results = new GrammarResult(AssessmentManager.getInstance().getTestAge());
		this.getQuestionList();
		this.initializePipeline(root);
	}

	@Override
	public Response analyzeResponse(String response) {
		this.utterance = new Utterance(response);
		try {
			CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
			Sentence sentence = analyzer.analyze(response);
			List<Map.Entry<String, String>> analyzed = new LinkedList<>();
			for (IWord word : sentence.wordList) {
				analyzed.add(new AbstractMap.SimpleEntry<>(word.getValue(), word.getLabel()));
			}
			utterance.setAnalyzedUtterance(analyzed);
			return utterance;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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
			stage.addRecord(new GrammarStructure(question.getTarget(), Integer.parseInt(controller.getScore())), this.utterance);
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

	@Override
	public BaseResult getResult() {
		return results;
	}

	private void initializePipeline(AnchorPane root) {
		InitializePane initializePane = new InitializePane();
		root.getChildren().add(initializePane);
		new Thread(() -> {

			System.out.println("NLP initialization complete");
			Platform.runLater(() -> root.getChildren().remove(initializePane));
		}).start();
	}

	public GrammarResult getResults() {
		return results;
	}
}
