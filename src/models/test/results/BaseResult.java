package models.test.results;

import org.jetbrains.annotations.NotNull;
import views.items.ResultItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseResult implements Comparable<BaseResult> {
    public Date testTime;
    public String testAge;

    public BaseResult(String testAge) {
        this.testAge = testAge;
    }

    public abstract void conclude();
    public abstract ResultItem toResultItem();

    public String getTestTime() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(testTime);
    }

    @Override
    public int compareTo(@NotNull BaseResult o) {
        if (o.testTime.getTime() > this.testTime.getTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}
