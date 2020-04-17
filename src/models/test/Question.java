package models.test;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.test.grammar.Utterance;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class Question extends RecursiveTreeObject<Question> {
    private String id;
    private String path;
    private String target;
    private Map<String, Integer> targets;
    private Map<Utterance, Map<String, Integer>> sampleAnswers;
    private String text;
    private String stage = "";

    public Question(String id, String path, String stage, String text, Map<String, Integer> targets) {
        this.id = id;
        this.path = StringUtils.substring(path,5);
        this.stage = stage;
        this.text = text;
        this.targets = targets;
    }

    public Question(String path, String target) {
        this.path = StringUtils.substring(path,5);
        this.target = target;
    }

    public String getPath() {
        return path;
    }

    public String getTarget() {
        return target;
    }
    public Map<String, Integer> getTargets() {
        return targets;
    }

    public String getText() {
        return text;
    }

    public String getStage() {
        return stage;
    }
    public StringProperty getStageProperty() {
        String str = stage.replace("1", "I")
                .replace("2", "II")
                .replace("3", "III")
                .replace("4", "IV");
        return new SimpleStringProperty(str);
    }

    public String getId() {
        return id;
    }
    public StringProperty getIdProperty() {
        return new SimpleStringProperty(id);
    }

    public Map<Utterance, Map<String, Integer>> getSampleAnswers() {
        return sampleAnswers;
    }
    public void setSampleAnswers(Map<Utterance, Map<String, Integer>> answers) {
        this.sampleAnswers = answers;
    }
}
