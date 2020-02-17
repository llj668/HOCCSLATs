package models.test.pronun;

import java.util.*;

public class PronunItems {
    public static String[] targets = {
            "鼻子", "耳朵", "嘴"
    };

    public static List<String> priority_phoneme = Collections.singletonList(
            "er"
    );
    public static List<String> consonants = Arrays.asList(
            "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t",
            "x", "z"
    );
    public static List<String> double_consonants = Arrays.asList(
            "zh", "ch", "ng", "sh"
    );
    public static List<String> vowels = Arrays.asList(
            "i", "u", "v", "o", "e", "a", "y", "w"
    );
    public static List<String> double_vowels = Arrays.asList(
            "ao", "au", "ai", "ua", "uo", "ia", "iu", "ui", "ei", "ou"
    );
    public static List<String> triple_vowels = Arrays.asList(
            "yue", "iao", "uai"
    );
    public static List<List<String>> phonemes = Arrays.asList(
            priority_phoneme, double_consonants, consonants, triple_vowels, double_vowels, vowels
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
