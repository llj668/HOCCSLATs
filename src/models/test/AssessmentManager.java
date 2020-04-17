package models.test;

import controllers.BaseTestController;
import controllers.items.BaseSummaryController;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import models.profiles.Age;
import models.profiles.Profile;
import models.test.grammar.GrammarTest;
import models.test.pronun.PronunTest;

import java.util.*;

public class AssessmentManager {
	public static Profile profile;
	private static AssessmentManager instance;
	private Assessment assessment;
	private BaseTestController controller;
	private Age testAge;
	private Queue<String> testQueue;
	private Question question;
	
    synchronized public static AssessmentManager getInstance() {
		if (instance == null) {
			instance = new AssessmentManager();
		}
    	return instance;
	}
    
    private AssessmentManager() {
    	testQueue = new LinkedList<>();
    }

    public Question nextQuestion() {
    	assessment.writeResult(controller, question);
		question = assessment.getNextQuestion();
		if (question != null) {
			controller.imgQuestion.setImage(new Image(question.getPath()));
			controller.updateLabels(question.getTarget());
			assessment.setTarget(question.getTarget());
		} else {
			assessment.saveResult();
			Map.Entry<Region, BaseSummaryController> entry = assessment.end();
			entry.getValue().setOnAfterTest(controller);
			entry.getValue().setResult(assessment.getResult(), profile);
			controller.setSummary(entry.getKey());
		}
		return question;
	}

    public void startGrammarAssessment(BaseTestController controller) {
		this.controller = controller;
		this.assessment = new GrammarTest(controller, testQueue);

	}

	public void startPronunAssessment(BaseTestController controller) {
		this.controller = controller;
		this.assessment = new PronunTest();
	}

	public void clearAll() {
		assessment = null;
		controller = null;
		testQueue = null;
		question = null;
	}

	public Age getTestAge() {
		return testAge;
	}

	public void setTestAge(Age testAge) {
		this.testAge = testAge;
	}

	public Assessment getAssessment() {
		return assessment;
	}

	public void setTestQueue(Queue<String> testQueue) {
		this.testQueue = testQueue;
	}

	public Question getQuestion() {
    	return question;
	}

	public BaseTestController getController() {
    	return controller;
	}
}
