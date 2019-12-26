package models.test.grammar;

import models.test.Assessment;
import models.test.Question;
import models.test.results.GrammarResult;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class GrammarTest extends Assessment {
	final static String GRAMMAR_QUESTION_PATH = "./src/resources/questions/grammar/";
	private GrammarResult results;

	public GrammarTest() {
		super();
		results = new GrammarResult();
	}

	@Override
	public void analyzeResponse(String response) {

	}

	@Override
	public void getQuestionList() {
		File[] files = new File(GRAMMAR_QUESTION_PATH).listFiles();
		for (File file : files) {
			String[] str = StringUtils.substringBefore(file.getName(), ".").split("-");
			questionList.add(new Question(file.getPath(), str[0], str[1]));
		}
	}

	public GrammarResult getResults() {
		return results;
	}
}
