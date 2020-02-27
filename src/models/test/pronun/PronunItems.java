package models.test.pronun;

import java.util.*;

public class PronunItems {
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

    // Error pattern
    public static List<String> double_vowels_for_pattern = Arrays.asList(
            "ao", "au", "ai", "ua", "uo", "ia", "iu", "ei", "ou", "er"
    );
    public static List<String> triple_vowels_for_pattern = Arrays.asList(
            "yue", "iao", "uai", "ui"
    );
    public static List<String> aspiration = Arrays.asList("p","t","k","c","q","ch");
    public static List<String> deaspiration = Arrays.asList("b","d","g","z","j","zh");
    public static Map<ErrorPattern, String> patternName = new HashMap<ErrorPattern, String>(){{
        put(ErrorPattern.CONSONANT_ASSIMILATION, "Consonant assimilation");
        put(ErrorPattern.SYLLABLE_INITIAL_DELETION, "Syllable initial deletion");
        put(ErrorPattern.FRONTING_1, "Fronting: /sh/ → [s]");
        put(ErrorPattern.FRONTING_2, "Fronting: /x/ → [sh]");
        put(ErrorPattern.FRONTING_3, "Fronting: /g/ → [d]");
        put(ErrorPattern.BACKING, "Backing: /s/ → [sh]");
        put(ErrorPattern.X_VELARISATION, "X velarisation");
        put(ErrorPattern.STOPPING_1, "Stopping: /z/ → [d]");
        put(ErrorPattern.STOPPING_2, "Stopping: /sh/ → [d]");
        put(ErrorPattern.STOPPING_3, "Stopping: /h/ → [g]");
        put(ErrorPattern.AFFRICATION, "Affrication");
        put(ErrorPattern.DEASPIRATION, "Deaspiration");
        put(ErrorPattern.ASPIRATION, "Aspiration");
        put(ErrorPattern.GLIDING, "Gliding");
        put(ErrorPattern.FINAL_N_DELETION, "Final /n/ deletion");
        put(ErrorPattern.BACKING_N, "Backing: /n/ → [ng]");
        put(ErrorPattern.FINAL_NG_DELETION, "Final /ng/ deletion");
        put(ErrorPattern.TRIPHTHONG_REDUCTION, "Triphthong reduction");
        put(ErrorPattern.DIPHTHONG_REDUCTION, "Diphthong reduction");
    }};
}
