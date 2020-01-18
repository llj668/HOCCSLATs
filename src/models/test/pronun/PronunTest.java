package models.test.pronun;

import controllers.BaseTestController;
import controllers.items.ItemController;
import javafx.scene.layout.Region;
import models.services.jpinyin.PinyinException;
import models.services.jpinyin.PinyinFormat;
import models.services.jpinyin.PinyinHelper;
import models.test.Assessment;
import models.test.Question;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.PronunResult;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

public class PronunTest extends Assessment {
	final static String PRONUN_QUESTION_PATH = "./src/resources/questions/pronun/";
	private PronunResult results;

	public PronunTest() {
		super();
		results = new PronunResult();
	}

	@Override
	public String analyzeResponse(String response) {
		String analyzed = null;
		try {
			analyzed = PinyinHelper.convertToPinyinString(response, ",", PinyinFormat.WITH_TONE_MARK);
		} catch (PinyinException e) {
			e.printStackTrace();
		}
		return analyzed;
	}

	@Override
	public void getQuestionList() {
		File[] files = new File(PRONUN_QUESTION_PATH).listFiles();
		for (File file : files) {
			String[] str = StringUtils.substringBefore(file.getName(), ".").split("-");
			questionList.add(new Question(file.getPath(), str[1], PronunItems.targets[Integer.parseInt(str[0])]));
		}
	}

	@Override
	public void writeResult(BaseTestController controller, Question question) {

	}

	@Override
	public void saveResult() {

	}

	@Override
	public Map.Entry<Region, ItemController> end() {
		return null;
	}

	@Override
	public BaseResult getResult() {
		return results;
	}

	public PronunResult getResults() {
		return results;
	}
}
