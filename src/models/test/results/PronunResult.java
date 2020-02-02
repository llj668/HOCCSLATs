package models.test.results;

import models.test.pronun.ErrorPattern;

import java.util.LinkedList;
import java.util.List;

public class PronunResult extends BaseResult {
    public double pcc;
    public List<String> presentConsonants;
    public List<String> absentConsonants;
    public List<ErrorPattern> errors;

    public PronunResult(String testAge) {
        super(testAge);
        presentConsonants = new LinkedList<>();
        absentConsonants = new LinkedList<>();
        errors = new LinkedList<>();
    }

    public PronunResult(String testAge, List<String> presentConsonants, List<ErrorPattern> errors) {
        super(testAge);
        this.presentConsonants = presentConsonants;
        this.absentConsonants = getAbsentFromPresent(presentConsonants);
        this.errors = errors;
    }

    private List<String> getAbsentFromPresent(List<String> presentConsonants) {
        return null;
    }

}
