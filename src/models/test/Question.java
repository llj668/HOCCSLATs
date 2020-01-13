package models.test;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Question implements Comparable<Question> {
    private String path;
    private String name;
    private String target;
    private int stage;

    public Question(String path, String stage, String target) {
        this.path = StringUtils.substring(path,5);
        this.stage = Integer.parseInt(stage);
        this.target = target;
    }

    @Override
    public int compareTo(@NotNull Question o) {
        return this.stage - o.stage;
    }

    public String getPath() {
        return path;
    }

    public String getTarget() {
        return target;
    }

    public String getStage() {
        return String.valueOf(stage);
    }
}
