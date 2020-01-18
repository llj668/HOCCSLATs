package models.test;

import controllers.BaseTestController;
import controllers.items.ItemController;
import javafx.scene.layout.Region;
import models.test.results.BaseResult;

import java.util.LinkedList;
import java.util.Map;
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
    public abstract Map.Entry<Region, ItemController> end();
    public abstract BaseResult getResult();
}
