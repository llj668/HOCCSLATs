package models.test.results;

import java.util.Date;

public class BaseResult {
    public Date testTime;
    public String testAge;

    public BaseResult(String testAge) {
        this.testAge = testAge;
    }

}
