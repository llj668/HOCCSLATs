package models.test.results;

import models.test.pronun.ErrorPattern;
import models.test.pronun.Syllable;
import views.items.GrammarResultItem;
import views.items.PronunResultItem;
import views.items.ResultItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PronunResult extends BaseResult {
    public double pcc;
    public List<Syllable> syllables;
    public List<String> presentConsonants;
    public List<String> absentConsonants;
    public List<ErrorPattern> errors;

    public PronunResult(String testAge) {
        super(testAge);
        syllables = new LinkedList<>();
        presentConsonants = new LinkedList<>();
        absentConsonants = new LinkedList<>();
        errors = new LinkedList<>();
    }

    public PronunResult(String testAge, Date testTime, String pcc, List<Syllable> syllables, List<String> presentConsonants) {
        super(testAge);
        this.testTime = testTime;
        this.pcc = Double.parseDouble(pcc);
        this.syllables = syllables;
        this.presentConsonants = presentConsonants;
        this.absentConsonants = getAbsentFromPresent();
        this.errors = getErrorsFromSyllable();
    }

    private List<String> getAbsentFromPresent() {
        List<String> absent = new LinkedList<>();
        return absent;
    }

    private List<ErrorPattern> getErrorsFromSyllable() {
        List<ErrorPattern> errors = new LinkedList<>();
        for (Syllable syllable : syllables) {
            errors.addAll(syllable.getErrorPatterns());
        }
        return errors.stream().distinct().collect(Collectors.toList());
    }

    private List<String> getPresentsFromSyllable() {
        List<String> presents = new LinkedList<>();
        for (Syllable syllable : syllables) {
            presents.addAll(syllable.getConsonantsCorrect());
        }
        return presents.stream().distinct().collect(Collectors.toList());
    }

    public String getPresentConsonantsAsString() {
        return String.join(",", presentConsonants);
    }

    @Override
    public void conclude() {
        testTime = new Date();
        this.pcc = 0;
        this.presentConsonants = getPresentsFromSyllable();
        this.absentConsonants = getAbsentFromPresent();
        this.errors = getErrorsFromSyllable();
    }

    @Override
    public ResultItem toResultItem() {
        return new PronunResultItem(this);
    }
}
