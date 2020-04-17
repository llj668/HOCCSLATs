package models.test.results;

import models.profiles.Age;
import views.items.ResultItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseResult implements Comparable<BaseResult> {
    public Date testTime;
    public Age testAge;

    public BaseResult(Age testAge) {
        this.testAge = testAge;
    }

    public abstract void conclude();
    public abstract ResultItem toResultItem();

    public String getTestTime() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(testTime);
    }

    @Override
    public int compareTo(BaseResult o) {
        if (o.testTime.getTime() > this.testTime.getTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}
