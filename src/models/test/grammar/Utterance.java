package models.test.grammar;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import models.test.Response;
import models.test.results.GrammarStructure;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utterance implements Response {
    private String utterance;
    private List<Map.Entry<String, String>> analyzedUtterance;
    private List<Map.Entry<String, String>> analyzedPhrase;
    private List<Map.Entry<String, String>> analyzedWord;
    private List<String> presentUtteranceStructures;
    private List<String> presentPhraseStructures;
    private List<String> presentWordStructures;

    public Utterance(String utterance) {
        this.utterance = utterance;
        this.presentUtteranceStructures = new LinkedList<>();
        this.presentPhraseStructures = new LinkedList<>();
        this.presentWordStructures = new LinkedList<>();
    }

    public Utterance(String utterance, List<Map.Entry<String, String>> analyzedUtterance, List<Map.Entry<String, String>> analyzedPhrase,
                     List<Map.Entry<String, String>> analyzedWord,
                     List<String> presentUtteranceStructures, List<String> presentPhraseStructures, List<String> presentWordStructures) {
        this.utterance = utterance;
        this.analyzedUtterance = analyzedUtterance;
        this.analyzedPhrase = analyzedPhrase;
        this.analyzedWord = analyzedWord;
        if (presentUtteranceStructures != null && presentPhraseStructures != null && presentWordStructures != null) {
            this.presentUtteranceStructures = presentUtteranceStructures;
            this.presentPhraseStructures = presentPhraseStructures;
            this.presentWordStructures = presentWordStructures;
        } else {
            this.presentUtteranceStructures = new LinkedList<>();
            this.presentPhraseStructures = new LinkedList<>();
            this.presentWordStructures = new LinkedList<>();
        }
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

    public List<Map.Entry<String, String>> getAnalyzedWord() {
        return analyzedWord;
    }

    public void setAnalyzedWord(List<Map.Entry<String, String>> analyzedWord) {
        this.analyzedWord = analyzedWord;
    }

    public List<String> getPresentUtteranceStructures() {
        return this.presentUtteranceStructures;
    }

    public void addPresentUtteranceStructure(String structure) {
        presentUtteranceStructures.add(structure);
    }

    public List<String> getPresentPhraseStructures() {
        return this.presentPhraseStructures;
    }

    public void addPresentPhraseStructure(String structure) {
        presentPhraseStructures.add(structure);
    }

    public List<String> getPresentWordStructures() {
        return this.presentWordStructures;
    }

    public void addPresentWordStructure(String structure) {
        presentWordStructures.add(structure);
    }
}
