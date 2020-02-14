package models.test.pronun;

import java.util.*;

public class PronunItems {
    public static String[] targets = {
            "鼻子", "耳朵", "嘴"
    };
    public static List<String> consonants = Arrays.asList(
            "b", "c", "ch", "d", "f", "g", "h", "j", "k", "l", "m", "n", "ng", "p", "q", "r", "s", "sh", "t", "w",
            "x", "y", "z", "zh"
    );
    public static Map<String, String> consonantType = new HashMap<String, String>(){{
        put("d", "plosive");
        put("t", "plosive");
        put("g", "plosive");
        put("b", "plosive");
        put("p", "plosive");
        put("k", "plosive");
        put("m", "nasal");
        put("n", "nasal");
        put("ng", "nasal");
        put("j", "affricate");
        put("q", "affricate");
        put("z", "affricate");
        put("zh", "affricate");
        put("c", "affricate");
        put("ch", "affricate");
        put("x", "fricative");
        put("h", "fricative");
        put("f", "fricative");
        put("s", "fricative");
        put("sh", "fricative");
        put("r", "approxi");
        put("l", "l_approxi");
    }};
}
