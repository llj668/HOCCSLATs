package models.test.results;

import models.test.grammar.Utterance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GrammarStage implements Comparable<GrammarStage> {
    private int stageNo;
    private double stageScore = -1;
    private HashMap<GrammarStructure, Utterance> records;

    public GrammarStage(int stageNo) {
        records = new HashMap<>();
        this.stageNo = stageNo;
    }

    public GrammarStage(HashMap<GrammarStructure, Utterance> unscored) {
        this.stageNo = -1;
        this.records = unscored;
    }

    public void addRecord(GrammarStructure structure, Utterance utterance) {
        records.put(structure, utterance);
    }

    public int getStageNo() {
        return stageNo;
    }

    public double getStageScore() {
        return stageScore;
    }

    public void setStageScore(double stageScore) {
        this.stageScore = stageScore;
    }

    public HashMap<GrammarStructure, Utterance> getRecords() {
        return records;
    }

    @Override
    public int compareTo(@NotNull GrammarStage o) {
        return this.stageNo - o.stageNo;
    }
}
