package models.test.pronun;

import models.test.Response;

import java.util.List;

public class Syllable implements Response {
    private String response;
    private List<String> consonantsCorrect;
    private List<ErrorPattern> errorPatterns;

    public Syllable(String response) {
        this.response = response;
    }

    public void getConsonantsCorrect(String target) {

    }

    public void getErrorPatterns(String target) {

    }
}
