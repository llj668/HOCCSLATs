package models.test;

import controllers.BaseTestController;
import javafx.scene.image.Image;
import models.profiles.Profile;
import models.test.grammar.GrammarTest;
import models.test.pronun.PronunTest;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class AssessmentManager {
	public static Profile profile;
	private static AssessmentManager instance;
	private Assessment assessment;
	private BaseTestController controller;
	private String testAge;
	private Queue<String> testQueue;
	
    synchronized public static AssessmentManager getInstance() {
		if (instance == null) {
			instance = new AssessmentManager();
		}
    	return instance;
	}
    
    private AssessmentManager() {
    	testQueue = new LinkedList<>();
    }

    public void nextQuestion() {
		Question question = assessment.getNextQuestion();
		if (question != null) {
			controller.imgQuestion.setImage(new Image(question.path));
		}
	}

    public void startGrammarAssessment(BaseTestController controller) {
		this.controller = controller;
		this.assessment = new GrammarTest(controller.root, testQueue);

	}

	public void startPronunAssessment(BaseTestController controller) {
		this.controller = controller;
		this.assessment = new PronunTest();
	}

	public String getTestAge() {
		return testAge;
	}

	public void setTestAge(String testAge) {
		this.testAge = testAge;
	}

	public Assessment getAssessment() {
		return assessment;
	}

	public void setTestQueue(Queue<String> testQueue) {
		this.testQueue = testQueue;
	}
}
