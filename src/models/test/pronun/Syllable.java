package models.test.pronun;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.test.Response;

import java.util.*;

public class Syllable extends RecursiveTreeObject<Syllable> implements Response {
    public static final int error_threshold = 2;
    private double pcc;
    private String response;
    private String target;
    private List<String> phonemesCorrect;
    private Map<ErrorPattern, Integer> errorPatterns;

    public Syllable(String target, String response) {
        this.target = target;
        this.response = response;
        this.phonemesCorrect = new LinkedList<>();
        this.errorPatterns = new LinkedHashMap<>();
    }

    public Syllable(HashMap<String, String> data) {
        this.pcc = Double.parseDouble(data.get("pcc"));
        this.target = data.get("target");
        this.response = data.get("response");
        this.phonemesCorrect = Arrays.asList(data.get("present_consonant").split(","));
        this.errorPatterns = new LinkedHashMap<>();
    }

    public void identifyPhonemesAndErrorPatterns() {
        PinyinIdentifier identifier = new PinyinIdentifier(response, target);
        Map.Entry<List<String>, Double> calculated = identifier.calculatePcc();
        phonemesCorrect = calculated.getKey();
        pcc = calculated.getValue();
        errorPatterns = identifier.compareErrorPatterns();
    }

    public double getPcc() {
        return pcc;
    }
    public String getResponse() {
        return this.response;
    }
    public String getTarget() {
        return this.target;
    }
    public Map<ErrorPattern, Integer> getErrorPatterns() {
        return this.errorPatterns;
    }
    public void setErrorPatterns(Map<ErrorPattern, Integer> errorPatterns) {
        this.errorPatterns = errorPatterns;
    }
    public List<String> getPhonemesCorrect() {
        return this.phonemesCorrect;
    }
    public String getConsonantsCorrectAsString() {
        return String.join(",", this.phonemesCorrect);
    }
    public String getErrorPatternsAsString() {
        if (errorPatterns.size() == 0)
            return "NONE";
        List<String> patterns = new LinkedList<>();
        for (Map.Entry<ErrorPattern, Integer> entry : errorPatterns.entrySet()) {
            if (entry.getValue() >= error_threshold)
                patterns.add(entry.getKey().name());
        }
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
