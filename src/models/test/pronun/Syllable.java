package models.test.pronun;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.test.Response;

import java.util.*;

public class Syllable extends RecursiveTreeObject<Syllable> implements Response {
    private double pcc;
    private String response;
    private String target;
    private List<String> phonemesCorrect;
    private List<ErrorPattern> errorPatterns;

    public Syllable(String target, String response) {
        this.target = target;
        this.response = response;
        this.phonemesCorrect = new LinkedList<>();
        this.errorPatterns = new LinkedList<>();
    }

    public Syllable(HashMap<String, String> data) {
        this.pcc = Double.parseDouble(data.get("pcc"));
        this.target = data.get("target");
        this.response = data.get("response");
        this.phonemesCorrect = Arrays.asList(data.get("present_consonant").split(","));
        this.errorPatterns = new LinkedList<>();
        for (String e : data.get("error_pattern").split(",")) {
            this.errorPatterns.add(ErrorPattern.valueOf(e));
        }
    }

    public void identifyPhonemesCorrect() {
        List<Collection<String>> responsePhonemes = PinyinIdentifier.parsePhonemeList(response);
        List<Collection<String>> targetPhonemes = PinyinIdentifier.parsePhonemeList(target);
        Map.Entry<List<String>, Double> calculated = PinyinIdentifier.calculatePcc(responsePhonemes, targetPhonemes);
        this.phonemesCorrect = calculated.getKey();
        this.pcc = calculated.getValue();
    }

    public void identifyErrorPatterns() {
        errorPatterns.add(ErrorPattern.AFFRICATION);
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
    public List<ErrorPattern> getErrorPatterns() {
        return this.errorPatterns;
    }
    public List<String> getPhonemesCorrect() {
        return this.phonemesCorrect;
    }
    public String getConsonantsCorrectAsString() {
        return String.join(",", this.phonemesCorrect);
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
