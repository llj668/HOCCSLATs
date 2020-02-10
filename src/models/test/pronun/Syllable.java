package models.test.pronun;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.test.Response;
import models.test.results.GrammarStructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Syllable extends RecursiveTreeObject<Syllable> implements Response {
    private String response;
    private String target;
    private List<String> consonantsCorrect;
    private List<ErrorPattern> errorPatterns;

    public Syllable(String target, String response) {
        this.target = target;
        this.response = response;
        this.consonantsCorrect = new LinkedList<>();
        this.errorPatterns = new LinkedList<>();
    }

    public Syllable(HashMap<String, String> data) {
        this.target = data.get("target");
        this.response = data.get("response");
        this.consonantsCorrect = Arrays.asList(data.get("present_consonant").split(","));
        this.errorPatterns = new LinkedList<>();
        for (String e : data.get("error_pattern").split(",")) {
            this.errorPatterns.add(ErrorPattern.valueOf(e));
        }
    }

    public void identifyConsonantsCorrect() {
        consonantsCorrect.add("c");
    }

    public void identifyErrorPatterns() {
        errorPatterns.add(ErrorPattern.AFFRICATION);
    }

    public String getResponse() {
        return this.response;
    }
    public String getTarget() {
        return this.target;
    }
    public List<ErrorPattern> getErrorPatterns() {
        return this.errorPatterns;
    }
    public List<String> getConsonantsCorrect() {
        return this.consonantsCorrect;
    }
    public String getConsonantsCorrectAsString() {
        return String.join(",", this.consonantsCorrect);
    }
    public String getErrorPatternsAsString() {
        List<String> patterns = new LinkedList<>();
        for (ErrorPattern e : errorPatterns)
            patterns.add(e.name());
        return String.join(",", patterns);
    }
    public StringProperty getTargetProperty() {
        return new SimpleStringProperty(this.target);
    }
    public StringProperty getResponseProperty() {
        return new SimpleStringProperty(this.response);
    }
    public StringProperty getPresentConsonantProperty() {
        return new SimpleStringProperty(getConsonantsCorrectAsString());
    }
    public StringProperty getErrorProperty() {
        return new SimpleStringProperty(getErrorPatternsAsString());
    }
}
