package models.test.grammar;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import models.test.Response;
import models.test.results.GrammarStructure;

import java.util.List;
import java.util.Map;

public class Utterance implements Response {
    private String utterance;
    private List<Map.Entry<String, String>> analyzedUtterance;

    public Utterance(String utterance) {
        this.utterance = utterance;
    }

    public Utterance(String utterance, List<Map.Entry<String, String>> analyzedUtterance) {
        this.utterance = utterance;
        this.analyzedUtterance = analyzedUtterance;
    }

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public List<Map.Entry<String, String>> getAnalyzedUtterance() {
        return analyzedUtterance;
    }

    public void setAnalyzedUtterance(List<Map.Entry<String, String>> analyzedUtterance) {
        this.analyzedUtterance = analyzedUtterance;
    }
}
