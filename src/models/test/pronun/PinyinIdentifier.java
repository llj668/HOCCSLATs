package models.test.pronun;

import models.services.util.MapSortUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinyinIdentifier {
    private List<Collection<String>> responsePhonemes;
    private List<Collection<String>> targetPhonemes;

    public PinyinIdentifier(String response, String target) {
        this.responsePhonemes = parsePhonemeList(response);
        this.targetPhonemes = parsePhonemeList(target);
    }

    // 把一个词的拼音拆分成拼音集合，Collection为字的拼音，List是字的集合
    private List<Collection<String>> parsePhonemeList(String string) {
        String[] syllables = string.split(",");
        List<Collection<String>> phonemeList = new LinkedList<>();
        for (String syllable : syllables) {
            phonemeList.add(getPhoneme(syllable));
        }
        return phonemeList;
    }

    private Collection<String> getPhoneme(String syllable) {
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

    public Map.Entry<List<String>, Double> calculatePcc() {
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

    // 将response的拼音与target的拼音对比得出error pattern
    public Map<ErrorPattern, Integer> compareErrorPatterns() {
        Map<ErrorPattern, Integer> patterns = new LinkedHashMap<>();
        Map<Collection<String>, Collection<String>> compareList = new HashMap<>();
        Iterator<Collection<String>> responseIterator = responsePhonemes.iterator();
        Iterator<Collection<String>> targetIterator = targetPhonemes.iterator();
        while (responseIterator.hasNext() && targetIterator.hasNext()) {
            compareList.put(responseIterator.next(), targetIterator.next());
        }

        patterns.put(ErrorPattern.CONSONANT_ASSIMILATION, isConsonantAssimilation(compareList));
        patterns.put(ErrorPattern.SYLLABLE_INITIAL_DELETION, isSyllableInitialDeletion(compareList));
        patterns.put(ErrorPattern.FRONTING_1, isFronting_1(compareList));
        patterns.put(ErrorPattern.FRONTING_2, isFronting_2(compareList));
        patterns.put(ErrorPattern.FRONTING_3, isFronting_3(compareList));
        patterns.put(ErrorPattern.BACKING, isBacking(compareList));
        patterns.put(ErrorPattern.X_VELARISATION, isX_Velarisation(compareList));
        patterns.put(ErrorPattern.STOPPING_1, isStopping_1(compareList));
        patterns.put(ErrorPattern.STOPPING_2, isStopping_2(compareList));
        patterns.put(ErrorPattern.STOPPING_3, isStopping_3(compareList));
        patterns.put(ErrorPattern.AFFRICATION, isAffrication(compareList));
        Map<ErrorPattern, Integer> rest = identifyTheRest();
        patterns.putAll(rest);

        return patterns;
    }

    private int isConsonantAssimilation(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getKey().size() != pair.getValue().size())
                continue;
            List<String> key = new LinkedList<>(pair.getKey());
            List<String> value = new LinkedList<>(pair.getValue());
            for (int i = 0; i < key.size(); i++) {
                if (PronunItems.consonants.contains(key.get(i)) || PronunItems.double_consonants.contains(key.get(i))) {
                    if (PronunItems.consonants.contains(value.get(i)) || PronunItems.double_consonants.contains(value.get(i))) {
                        if (!key.get(i).equals(value.get(i)) && !key.get(i).equals("n") && !key.get(i).equals("ng"))
                            times++;
                    }
                }
            }
        }
        return times;
    }

    private int isSyllableInitialDeletion(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            List<String> key = new LinkedList<>(pair.getKey());
            List<String> value = new LinkedList<>(pair.getValue());
            value.remove(0);
            if (value.toString().equals(key.toString()))
                times++;
        }

        return times;
    }

    private int isFronting_1(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getValue().contains("zh") && pair.getKey().contains("z"))
                times++;
            else if (pair.getValue().contains("ch") && pair.getKey().contains("c"))
                times++;
            else if (pair.getValue().contains("sh") && pair.getKey().contains("s"))
                times++;
        }
        return times;
    }

    private int isFronting_2(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getValue().contains("x") && pair.getKey().contains("sh"))
                times++;
        }
        return times;
    }

    private int isFronting_3(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getValue().contains("g") && pair.getKey().contains("d"))
                times++;
        }
        return times;
    }

    private int isBacking(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getValue().contains("z") && pair.getKey().contains("zh"))
                times++;
            else if (pair.getValue().contains("c") && pair.getKey().contains("ch"))
                times++;
            else if (pair.getValue().contains("s") && pair.getKey().contains("sh"))
                times++;
        }
        return times;
    }

    private int isX_Velarisation(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            for (String phoneme : pair.getValue()) {
                String type = PronunItems.consonantType.get(phoneme);
                if (type == null)
                    continue;
                if (type.equalsIgnoreCase("fricative") || type.equalsIgnoreCase("affricate")) {
                    if ((!phoneme.equals("h")) && pair.getKey().contains("h"))
                        times++;
                }
            }
        }
        return times;
    }

    private int isStopping_1(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getValue().contains("z") && pair.getKey().contains("d"))
                times++;
        }
        return times;
    }

    private int isStopping_2(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getValue().contains("sh") && pair.getKey().contains("d"))
                times++;
        }
        return times;
    }

    private int isStopping_3(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            if (pair.getValue().contains("h") && pair.getKey().contains("g"))
                times++;
        }
        return times;
    }

    private int isAffrication(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;
        boolean isStop = false;
        for (Map.Entry<Collection<String>, Collection<String>> pair : compareList.entrySet()) {
            for (String phoneme : pair.getValue()) {
                if (PronunItems.stops.contains(phoneme)) {
                    isStop = true;
                    break;
                }
            }
            for (String phoneme : pair.getKey()) {
                String type = PronunItems.consonantType.get(phoneme);
                if (type == null)
                    continue;
                if (type.equalsIgnoreCase("fricative") || type.equalsIgnoreCase("affricate")) {
                    if (isStop)
                        times++;
                }
            }
        }
        return times;
    }

    private int isDeaspiration(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private int isAspiration(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private int isGliding(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private int isFinal_N_Deletion(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private int isBacking_N(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private int isFinal_NG_Deletion(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private int isTriphthongReduction(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private int isDiphthongReduction(Map<Collection<String>, Collection<String>> compareList) {
        int times = 0;

        return times;
    }

    private Map<ErrorPattern, Integer> identifyTheRest() {
        String response = restorePhonemeList(responsePhonemes);
        String target = restorePhonemeList(targetPhonemes);

        Map<ErrorPattern, Integer> result = new LinkedHashMap<>();
        result.put(ErrorPattern.DEASPIRATION,0);
        result.put(ErrorPattern.ASPIRATION, 0);
        result.put(ErrorPattern.GLIDING, 0);
        result.put(ErrorPattern.FINAL_N_DELETION, 0);
        result.put(ErrorPattern.BACKING_N, 0);
        result.put(ErrorPattern.FINAL_NG_DELETION, 0);
        result.put(ErrorPattern.DIPHTHONG_REDUCTION, 0);
        result.put(ErrorPattern.TRIPHTHONG_REDUCTION, 0);
        String[] targetWords = target.split(",");
        String[] responseWords = response.split(",");
        String trans1 = "";
        String trans2 = "";

        for(int i=0;i<targetWords.length;i++) {
            int judge = 0;
            int judge1 = 0;
            //CASE1 targetWords长度小于responseWords长度时
            if(targetWords[i].length() < responseWords[i].length()) {
                trans1 = responseWords[i].substring(0,targetWords[i].length());

                //匹配BACKING_N ErrorPattern
                if (trans1.equalsIgnoreCase(targetWords[i]) && targetWords[i].endsWith("n") && responseWords[i].endsWith("ng") ) {
                    result.put(ErrorPattern.BACKING_N,result.get(ErrorPattern.BACKING_N)+1 );
                }
            }

            //CASE2 targetWords长度大于responseWords长度时
            if(targetWords[i].length() > responseWords[i].length()) {
                //在targetWords[i]中取和responseWord[i]长度一样的子字符串
                trans2 = targetWords[i].substring(0,responseWords[i].length());

                //匹配FINAL_NG_DELETION ErrorPattern
                if(trans2.equalsIgnoreCase(responseWords[i]) && targetWords[i].endsWith("ng")){
                    if( !responseWords[i].endsWith("n") ) {
                        result.put(ErrorPattern.FINAL_NG_DELETION,result.get(ErrorPattern.FINAL_NG_DELETION)+1 );
                    }

                }
                //匹配FINAL_N_DELETION ErrorPattern
                if(trans2.equalsIgnoreCase(responseWords[i]) && targetWords[i].endsWith("n")) {
                    result.put(ErrorPattern.FINAL_N_DELETION,result.get(ErrorPattern.FINAL_NG_DELETION)+1 );
                }

                if(targetWords[i].substring(0, 1).equalsIgnoreCase(responseWords[i].substring(0,1))) {

                    for(int q = 0; q <PronunItems.triple_vowels_for_pattern.size(); q++) {
                        Pattern p3 = Pattern.compile(PronunItems.triple_vowels_for_pattern.get(q));
                        Matcher m3 = p3.matcher(targetWords[i]);
                        if(m3.find()) {
                            judge++;
                            //System.out.println(m3.find());
                            //判断是不是双元音的情况
                            String targetSuffix1 = PronunItems.triple_vowels_for_pattern.get(q).substring(0,2);
                            String targetSuffix2 = PronunItems.triple_vowels_for_pattern.get(q).substring(1,3);
                            String targetSuffix3 = PronunItems.triple_vowels_for_pattern.get(q).substring(0,1) + PronunItems.triple_vowels.get(q).substring(2);
                            List<String> doubleTest = new ArrayList<>();
                            doubleTest.add(targetSuffix1);
                            doubleTest.add(targetSuffix2);
                            doubleTest.add(targetSuffix3);
                            int deter = 0;

                            for(int d = 0; d < doubleTest.size();d++) {
                                for(int r = 0; r < PronunItems.double_vowels_for_pattern.size(); r++) {
                                    Pattern pa = Pattern.compile(PronunItems.double_vowels_for_pattern.get(d));
                                    Matcher ma = pa.matcher(doubleTest.get(d));
                                    if(ma.matches()) {
                                        deter++;
                                    }
                                }
                                if(deter == 0) {
                                    doubleTest.remove(d);
                                }
                            }

                            for (String s : doubleTest) {
                                if (responseWords[i].contains(s)) {
                                    result.put(ErrorPattern.TRIPHTHONG_REDUCTION, result.get(ErrorPattern.TRIPHTHONG_REDUCTION) + 1);
                                    break;
                                }
                            }

                            if(result.get(ErrorPattern.TRIPHTHONG_REDUCTION)== 0) {
                                //如果不是减少至双元音，则匹配是否减少至单元音
                                if(responseWords[i].contains(PronunItems.triple_vowels_for_pattern.get(q).substring(0,1))) {
                                    result.put(ErrorPattern.TRIPHTHONG_REDUCTION,result.get(ErrorPattern.TRIPHTHONG_REDUCTION)+1 );
                                }
                                else if(responseWords[i].contains(PronunItems.triple_vowels_for_pattern.get(q).substring(1,2))) {
                                    result.put(ErrorPattern.TRIPHTHONG_REDUCTION,result.get(ErrorPattern.TRIPHTHONG_REDUCTION)+1 );
                                }
                                else if(responseWords[i].contains(PronunItems.triple_vowels_for_pattern.get(q).substring(2,3))) {
                                    result.put(ErrorPattern.TRIPHTHONG_REDUCTION,result.get(ErrorPattern.TRIPHTHONG_REDUCTION)+1 );
                                }
                            }
                        }
                    }
                    if(judge == 0) {
                        for(int m = 0; m < PronunItems.double_vowels_for_pattern.size(); m++) {
                            Pattern p4 = Pattern.compile(PronunItems.double_vowels_for_pattern.get(m));
                            Matcher m4 = p4.matcher(targetWords[i]);

                            if(m4.find()) {
                                if (responseWords[i].contains(PronunItems.double_vowels_for_pattern.get(m)))
                                    break;
                                if (responseWords[i].contains(PronunItems.double_vowels_for_pattern.get(m).substring(0,1))) {
                                    result.put(ErrorPattern.DIPHTHONG_REDUCTION,result.get(ErrorPattern.DIPHTHONG_REDUCTION)+1 );
                                }
                                else if(responseWords[i].contains(PronunItems.double_vowels_for_pattern.get(m).substring(1,2))) {
                                    result.put(ErrorPattern.DIPHTHONG_REDUCTION,result.get(ErrorPattern.DIPHTHONG_REDUCTION)+1 );
                                }
                            }
                        }
                    }
                }
            }
            //CASE3
            if(targetWords[i].length() == responseWords[i].length()) {
                //判断GLIDING ErrorPAttern
                if(targetWords[i].substring(1).equalsIgnoreCase(responseWords[i].substring(1))) {
                    if(targetWords[i].substring(0,1).equalsIgnoreCase("r") && responseWords[i].substring(0,1).equalsIgnoreCase("y")) {
                        result.put(ErrorPattern.GLIDING,result.get(ErrorPattern.GLIDING)+1 );
                    }
                }
                //dspiration/deaspiration有六种情况
                if(targetWords[i].substring(1).equalsIgnoreCase(responseWords[i].substring(1))) {
                    String targetPrefix = targetWords[i].substring(0,1);
                    String responsePrefix = responseWords[i].substring(0,1);
                    for(int w=0; w<PronunItems.aspiration.size();w++) {
                        if(targetPrefix.equalsIgnoreCase(PronunItems.aspiration.get(w)) && responsePrefix.equalsIgnoreCase(PronunItems.deaspiration.get(w))) {
                            result.put(ErrorPattern.DEASPIRATION,result.get(ErrorPattern.DEASPIRATION)+1 );
                            judge1++;
                        }
                        else if(targetPrefix.equalsIgnoreCase(PronunItems.deaspiration.get(w)) && responsePrefix.equalsIgnoreCase(PronunItems.aspiration.get(w))) {
                            result.put(ErrorPattern.ASPIRATION,result.get(ErrorPattern.ASPIRATION)+1 );
                            judge1++;
                        }
                    }
                }

                //判断zh和ch的
                if(judge1 == 0) {
                    if(targetWords[i].substring(2).equalsIgnoreCase(responseWords[i].substring(2))) {
                        String targetPrefix1 = targetWords[i].substring(0,2);
                        String responsePrefix1 = responseWords[i].substring(0,2);
                        if(targetPrefix1.equalsIgnoreCase("ch") && responsePrefix1.equalsIgnoreCase("zh") ) {
                            result.put(ErrorPattern.DEASPIRATION,result.get(ErrorPattern.DEASPIRATION)+1 );
                        }
                        else if(targetPrefix1.equalsIgnoreCase("zh") && responsePrefix1.equalsIgnoreCase("ch")) {
                            result.put(ErrorPattern.ASPIRATION,result.get(ErrorPattern.ASPIRATION)+1 );
                        }
                    }
                }
            }
        }
        return result;
    }
    private static String restorePhonemeList(List<Collection<String>> result) {
        String finalResult = "";
        for (Collection<String> strings : result) {
            Iterator iter = strings.iterator();
            while (iter.hasNext()) {
                finalResult += (String) iter.next();

            }
            finalResult += ",";
        }
        finalResult = finalResult.substring(0,finalResult.length()-1);
        return finalResult;
    }
}
