package models.test.results;

import java.util.HashMap;

public class GrammarStage {
    private int stageNo;
    private double stageScore;
    private HashMap<String, GrammarStructure> records;

    public GrammarStage() {
        records = new HashMap<>();
    }

    public void addRecord(String s, GrammarStructure structure) {
        records.put(s, structure);
    }

    public int getStageNo() {
        return stageNo;
    }

    public void setStageNo(int stageNo) {
        this.stageNo = stageNo;
    }

    public double getStageScore() {
        return stageScore;
    }

    public void setStageScore(double stageScore) {
        this.stageScore = stageScore;
    }

    public HashMap<String, GrammarStructure> getRecords() {
        return records;
    }
}
