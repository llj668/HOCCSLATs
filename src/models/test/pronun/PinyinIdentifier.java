package models.test.pronun;

import models.services.util.MapSortUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinyinIdentifier {

    public static List<Collection<String>> parsePhonemeList(String string) {
        String[] syllables = string.split(",");
        List<Collection<String>> phonemeList = new LinkedList<>();
        for (String syllable : syllables) {
            phonemeList.add(getPhoneme(syllable));
        }
        return phonemeList;
    }

    private static Collection<String> getPhoneme(String syllable) {
        Map<Integer, String> phonemes = new HashMap<>();
        for (List<String> phonemeList : PronunItems.phonemes) {
            for (String phoneme : phonemeList) {
                Pattern pattern = Pattern.compile(phoneme);
                Matcher matcher = pattern.matcher(syllable);
                if (matcher.find()) {
                    phonemes.put(matcher.start(), phoneme);
                    syllable = matcher.replaceAll("#");
                }
                if (Pattern.matches("#+", syllable))
                    return MapSortUtil.sortByKeyAsc(phonemes).values();
            }
        }
        return MapSortUtil.sortByKeyAsc(phonemes).values();
    }

    public static Map.Entry<List<String>, Double> calculatePcc(List<Collection<String>> responsePhonemes, List<Collection<String>> targetPhonemes) {
        Iterator<Collection<String>> responseIterator = responsePhonemes.iterator();
        Iterator<Collection<String>> targetIterator = targetPhonemes.iterator();
        DecimalFormat df = new DecimalFormat("#.00");
        List<String> phonemesCorrect = new LinkedList<>();
        double totalPhonemes = 0;
        double correctPhonemes = 0;
        while (responseIterator.hasNext() && targetIterator.hasNext()) {
            Collection<String> resCol = responseIterator.next();
            Collection<String> tarCol = targetIterator.next();
            for (String phoneme : tarCol) {
                if (resCol.contains(phoneme)) {
                    phonemesCorrect.add(phoneme);
                    correctPhonemes++;
                }
                totalPhonemes++;
            }
        }

        double pcc = Double.parseDouble(df.format(correctPhonemes / totalPhonemes));
        return new AbstractMap.SimpleEntry<>(phonemesCorrect, pcc);
    }
}
