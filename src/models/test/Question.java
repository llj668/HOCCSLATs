package models.test;

import org.apache.commons.lang3.StringUtils;

public class Question {
    public String path;
    public String name;
    public String target;
    public int stage;

    public Question(String path, String name, int stage) {
        this.path = StringUtils.substring(path,5);
        this.name = name;
        this.stage = stage;
    }

    public Question(String path, String name, String target) {
        this.path = StringUtils.substring(path,5);
        this.name = name;
        this.target = target;
    }

}
