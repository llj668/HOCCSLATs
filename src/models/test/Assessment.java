package models.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Assessment {
    public Queue<Question> questionList;

    public Assessment() {
        questionList = new LinkedList<>();
        getQuestionList();
    }

    public Question getNextQuestion() {
        return questionList.poll();
    }

    public abstract void analyzeResponse(String response);
    public abstract void getQuestionList();
}
