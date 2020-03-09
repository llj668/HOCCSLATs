package models.test.grammar;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import models.test.Response;
import models.test.results.GrammarStructure;

import java.util.List;
import java.util.Map;

public class Utterance implements Response {
    private String utterance;
    private List<Map.Entry<String, String>> analyzedUtterance;
    private List<Map.Entry<String, String>> analyzedPhrase;

    public Utterance(String utterance) {
        this.utterance = utterance;
    }

    public Utterance(String utterance, List<Map.Entry<String, String>> analyzedUtterance, List<Map.Entry<String, String>> analyzedPhrase) {
        this.utterance = utterance;
        this.analyzedUtterance = analyzedUtterance;
        this.analyzedPhrase = analyzedPhrase;
    }

    public String getUtterance() {
        return utterance;
    }

    public List<Map.Entry<String, String>> getAnalyzedUtterance() {
        return analyzedUtterance;
    }

    public void setAnalyzedUtterance(List<Map.Entry<String, String>> analyzedUtterance) {
        this.analyzedUtterance = analyzedUtterance;
    }

    public List<Map.Entry<String, String>> getAnalyzedPhrase() {
        return analyzedPhrase;
    }

    public void setAnalyzedPhrase(List<Map.Entry<String, String>> analyzedPhrase) {
        this.analyzedPhrase = analyzedPhrase;
    }
}
