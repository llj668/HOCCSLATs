package models.test.grammar;

import application.PropertyManager;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import models.test.Question;

import java.io.IOException;
import java.util.*;

public class UtteranceMarker {

    public static void main(String[] arg) {
        new PropertyManager();
        String word2vec = PropertyManager.getResourceProperty("word2vec_path");
        try {
            WordVectorModel wordVectorModel = new WordVectorModel(word2vec);
            DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
            double sim = docVectorModel.similarity("妈妈出门买菜", "妈妈在家里做饭");
            System.out.println(sim);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> mark(Question question, Utterance utterance, DocVectorModel docVectorModel) {
        Map<String, Integer> marked = new LinkedHashMap<>();
        if (docVectorModel == null) {
            for (String target : question.getTargets().keySet())
                marked.put(target, -1);
            return marked;
        }

        double sim_question = docVectorModel.similarity(utterance.getUtterance(), question.getText());
        System.out.println("similarity question: " + sim_question);

        boolean isCorrectMeaning = false;
        boolean isUnknown = false;
        boolean isUnmarkable = false;
        if (sim_question >= 0.7)
            isCorrectMeaning = true;
        else if (sim_question == -1.0)
            isUnmarkable = true;

        // 查看是否为“不知道”
        double sim_unknown = docVectorModel.similarity(utterance.getUtterance(), "不知道");
        System.out.println("similarity unknown: " + sim_unknown);
        if (sim_unknown > 0.5 && !isCorrectMeaning)
            isUnknown = true;
        if (utterance.getUtterance().equals(""))
            isUnknown = true;

        for (String target : question.getTargets().keySet()) {
            if (isUnmarkable)
                marked.put(target, -1);
            else if (isUnknown)
                marked.put(target, 0);
            else {
                boolean isCorrectSyntax = isCorrectStructure(utterance, target);
                if (isCorrectMeaning) {
                    if (isCorrectSyntax)
                        marked.put(target, 5);
                    else
                        marked.put(target, 4);
                } else {
                    if (isCorrectSyntax)
                        marked.put(target, 3);
                    else
                        marked.put(target, 1);
                }
            }
        }
        return marked;
    }

    private static boolean isCorrectStructure(Utterance utterance, String target) {
        List<String> syntaxList = GrammarItems.structureList.get(target);
        List<String> clauseList = getStructureList(utterance.getAnalyzedUtterance());
        List<String> phraseList = getStructureList(utterance.getAnalyzedPhrase());
        List<String> wordList = getStructureList(utterance.getAnalyzedWord());
        boolean isClauseLevel = false;
        boolean isPhraseLevel = false;
        boolean isWordLevel = false;
        for (String syntax : syntaxList) {
            boolean isAny = false;
            boolean isFound = false;
            List<String> equivalent = new LinkedList<>();
            if (syntax.equalsIgnoreCase("x"))
                isAny = true;
            if (syntax.equalsIgnoreCase("v"))
                equivalent = Collections.singletonList("p");

            // 从句子层面匹配
            for (String clauseStruct : clauseList) {
                if (clauseStruct.equalsIgnoreCase(syntax) || equivalent.contains(clauseStruct) || isAny) {
                    clauseList.remove(clauseStruct);
                    isFound = true;
                    isClauseLevel = true;
                    break;
                }
            }
            // 从词组层面匹配
            if (!isFound) {
                for (String phraseStruct : phraseList) {
                    if (phraseStruct.equalsIgnoreCase(syntax)) {
                        phraseList.remove(phraseStruct);
                        isFound = true;
                        isPhraseLevel = true;
                        break;
                    }
                }
            }
            // 从词前后缀层面匹配
            if (!isFound) {
                for (String wordStruct : wordList) {
                    if (wordStruct.equalsIgnoreCase(syntax)) {
                        phraseList.remove(wordStruct);
                        isFound = true;
                        isWordLevel = true;
                        break;
                    }
                }
            }
            if (!isFound)
                return false;
        }

        if (isClauseLevel)
            utterance.addPresentUtteranceStructure(target);
        else if (isPhraseLevel)
            utterance.addPresentPhraseStructure(target);
        else if (isWordLevel)
            utterance.addPresentWordStructure(target);
        return true;
    }

    private static List<String> getStructureList(List<Map.Entry<String, String>> list) {
        List<String> structureList = new LinkedList<>();
        if (list != null) {
            for (Map.Entry<String, String> entry : list) {
                structureList.add(entry.getValue().toLowerCase());
            }
        }
        return structureList;
    }
}
