package models.test.grammar;

import application.PropertyManager;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import models.test.Question;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public static int mark(Question question, Utterance utterance, DocVectorModel docVectorModel) {
        // 语义判断
        String target = question.getTarget();
        String targetString = GrammarItems.similarityMap.get(target);
        double sim_question = docVectorModel.similarity(utterance.getUtterance(), targetString);
        System.out.println("similarity question: " + sim_question);
        boolean isCorrectSyntax = isCorrectStructure(utterance, target);
        boolean isCorrectMeaning = false;
        if (sim_question >= 0.7)
            isCorrectMeaning = true;
        else if (sim_question == -1.0)
            return -1;

        // 查看是否为“不知道”
        double sim_unknown = docVectorModel.similarity(utterance.getUtterance(), "不知道");
        System.out.println("similarity unknown: " + sim_unknown);
        if (sim_unknown > 0.5 && !isCorrectMeaning)
            return 0;

        System.out.println("语义正确：" + isCorrectMeaning);
        System.out.println("结构正确：" + isCorrectSyntax);
        if (isCorrectMeaning) {
            if (isCorrectSyntax)
                return 5;
            else
                return 4;
        } else {
            if (isCorrectSyntax)
                return 3;
            else
                return 1;
        }
    }

    private static boolean isCorrectStructure(Utterance utterance, String target) {
        List<String> syntaxList = GrammarItems.structureList.get(target);
        List<String> clauseList = getStructureList(utterance.getAnalyzedUtterance());
        List<String> phraseList = getStructureList(utterance.getAnalyzedPhrase());
        boolean isAllFound = false;
        for (String syntax : syntaxList) {
            boolean isAny = false;
            boolean isFound = false;
            List<String> equivalent = new LinkedList<>();
            if (syntax.equalsIgnoreCase("x"))
                isAny = true;
            if (syntax.equalsIgnoreCase("v"))
                equivalent = Collections.singletonList("p");

            // 从句法层面匹配
            for (String clauseStruct : clauseList) {
                if (clauseStruct.equalsIgnoreCase(syntax) || equivalent.contains(clauseStruct) || isAny) {
                    clauseList.remove(clauseStruct);
                    isFound = true;
                    break;
                }
            }
            // 从词法层面匹配
            if (!isFound) {
                for (String phraseStruct : phraseList) {
                    if (phraseStruct.equalsIgnoreCase(syntax)) {
                        phraseList.remove(phraseStruct);
                        isFound = true;
                        break;
                    }
                }
            }
            isAllFound = isFound;
        }

        return isAllFound;
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
