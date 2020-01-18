package models.test.grammar;

import controllers.BaseTestController;
import controllers.items.GrammarSummaryController;
import controllers.items.ItemController;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import models.profiles.ProfileWriter;
import models.test.Assessment;
import models.test.AssessmentManager;
import models.test.Question;
import models.test.Response;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import views.ViewManager;
import views.items.InitializePane;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class GrammarTest extends Assessment {
	final static String GRAMMAR_QUESTION_PATH = "./src/resources/questions/grammar/";
	final static String TEMP_ANALYZE_FILE = "./src/models/services/temp/nlp_temp.xml";
	private GrammarResult results;
	private Queue<String> testQueue;
	private GrammarStage stage;
	private String prevStage = "-1";

	public GrammarTest(AnchorPane root, Queue<String> testQueue) {
		super();
		this.testQueue = testQueue;
		this.results = new GrammarResult(AssessmentManager.getInstance().getTestAge());
		this.getQuestionList();
		this.initializePipeline(root);
	}

	@Override
	public String analyzeResponse(String response) {

		return null;
	}

	@Override
	public void getQuestionList() {
		File[] files = new File(GRAMMAR_QUESTION_PATH).listFiles();
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
			stage.addRecord(new Response("response"), new GrammarStructure(question.getTarget(), Integer.parseInt(controller.getScore())));
			prevStage = question.getStage();
		}
	}

	@Override
	public void saveResult() {
		results.stageResults.add(stage);
		results.conclude();
	}

	@Override
	public Map.Entry<Region, ItemController> end() {
		return ViewManager.getInstance().getItemFromFXML(ViewManager.ITEM_GRAMMARSUMMARY);
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
