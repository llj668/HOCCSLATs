package models.test.results;

import models.test.Response;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GrammarStage implements Comparable<GrammarStage> {
    private int stageNo;
    private double stageScore;
    private HashMap<Response, GrammarStructure> records;

    public GrammarStage(int stageNo) {
        records = new HashMap<>();
        this.stageNo = stageNo;
    }

    public void addRecord(Response response, GrammarStructure structure) {
        records.put(response, structure);
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

    public HashMap<Response, GrammarStructure> getRecords() {
        return records;
    }

    @Override
    public int compareTo(@NotNull GrammarStage o) {
        return this.stageNo - o.stageNo;
    }
}
