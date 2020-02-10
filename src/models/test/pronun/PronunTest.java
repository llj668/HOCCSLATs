package models.test.pronun;

import controllers.BaseTestController;
import controllers.items.ItemController;
import javafx.scene.layout.Region;
import models.services.jpinyin.PinyinException;
import models.services.jpinyin.PinyinFormat;
import models.services.jpinyin.PinyinHelper;
import models.test.Assessment;
import models.test.AssessmentManager;
import models.test.Question;
import models.test.Response;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;
import models.test.results.PronunResult;
import org.apache.commons.lang3.StringUtils;
import views.ViewManager;

import java.io.File;
import java.util.Map;

public class PronunTest extends Assessment {
	final static String PRONUN_QUESTION_PATH = "./src/resources/questions/pronun/";
	private PronunResult results;
	private Syllable syllable;

	public PronunTest() {
		super();
		this.getQuestionList();
		results = new PronunResult(AssessmentManager.getInstance().getTestAge());
	}

	@Override
	public Response analyzeResponse(String response) {
		syllable = new Syllable(target, response);
		syllable.identifyConsonantsCorrect();
		syllable.identifyErrorPatterns();
		return syllable;
	}

	@Override
	public void getQuestionList() {
		File[] files = new File(PRONUN_QUESTION_PATH).listFiles();
		assert files != null;
		for (File file : files) {
			String[] str = StringUtils.substringBefore(file.getName(), ".").split("-");
			questionList.add(new Question(file.getPath(), PronunItems.targets[Integer.parseInt(str[0])]));
		}
	}

	@Override
	public void writeResult(BaseTestController controller, Question question) {
		if (question != null && syllable != null) {
			results.syllables.add(this.syllable);
		}
	}

	@Override
	public void saveResult() {
		results.conclude();
	}

	@Override
	public Map.Entry<Region, ItemController> end() {
		return ViewManager.getInstance().getItemFromFXML(ViewManager.ITEM_PRONUNSUMMARY);
	}

	@Override
	public BaseResult getResult() {
		return results;
	}

	public PronunResult getResults() {
		return results;
	}
}
