package models.test.pronun;

import application.PropertyManager;
import controllers.BaseTestController;
import controllers.items.BaseSummaryController;
import javafx.scene.layout.Region;
import models.services.jpinyin.PinyinException;
import models.services.jpinyin.PinyinFormat;
import models.services.jpinyin.PinyinHelper;
import models.test.Assessment;
import models.test.AssessmentManager;
import models.test.Question;
import models.test.Response;
import models.test.results.BaseResult;
import models.test.results.PronunResult;
import org.apache.commons.lang3.StringUtils;
import views.ViewManager;

import java.io.File;
import java.util.Map;

public class PronunTest extends Assessment {
	private PronunResult results;
	private Syllable syllable;

	public PronunTest() {
		super();
		this.getQuestionList();
		results = new PronunResult(AssessmentManager.getInstance().getTestAge());
	}

	@Override
	public Response analyzeResponse(String response) {
		try {
			syllable = new Syllable(PinyinHelper.convertToPinyinString(target, ",", PinyinFormat.WITH_TONE_NUMBER), response);
			syllable.identifyPhonemesCorrect();
			syllable.identifyErrorPatterns();
		} catch (PinyinException e) {
			e.printStackTrace();
		}
		return syllable;
	}

	@Override
	public void getQuestionList() {
		File[] files = new File(PropertyManager.getResourceProperty("pronun_question")).listFiles();
		assert files != null;
		for (File file : files) {
			String[] str = StringUtils.substringBefore(file.getName(), ".").split("-");
			questionList.add(new Question(file.getPath(), str[1]));
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
	public Map.Entry<Region, BaseSummaryController> end() {
		return ViewManager.getInstance().getItemFromFXML(PropertyManager.getResourceProperty("pronunsum"));
	}

	@Override
	public BaseResult getResult() {
		return results;
	}

	public PronunResult getResults() {
		return results;
	}
}
