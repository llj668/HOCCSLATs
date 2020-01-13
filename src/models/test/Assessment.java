package models.test;

import controllers.BaseTestController;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Assessment {
    public Queue<Question> questionList;

    public Assessment() {
        questionList = new LinkedList<>();
    }

    public Question getNextQuestion() {
        return questionList.poll();
    }

    public abstract String analyzeResponse(String response);
    public abstract void getQuestionList();
    public abstract void writeResult(BaseTestController controller, Question question);
    public abstract void saveResult();
}
