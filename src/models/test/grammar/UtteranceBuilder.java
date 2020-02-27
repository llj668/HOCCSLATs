package models.test.grammar;

import java.util.*;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import models.services.util.MapSortUtil;
import org.apache.commons.lang3.StringUtils;

public class UtteranceBuilder {
    final static String[] questionWords = {"谁", "呢", "哪", "什么", "吗"};
    final static String[] negWords = {"不", "没"};
    final static String[] auxWords = {"了", "吧", "的"};

    public static void main(String[] arg) {
        List<Map.Entry<String, String>> analyzed = analyzeClause("乌龟叫去森林", new NeuralNetworkDependencyParser());
        System.out.println();
        System.out.println("结果：");
//        for (Map.Entry<String, String> entry : analyzed) {
//            System.out.println(entry.getKey() + "\t\t成分：" + entry.getValue());
//        }
        for (Map.Entry<String, String> entry : analyzed) {
            System.out.print(entry.getKey() + "（" + entry.getValue() + "）");
        }
        System.out.println();
    }

    public static List<Map.Entry<String, String>> analyzeClause(String input, NeuralNetworkDependencyParser parser){
        CoNLLSentence sentence = parser.parse(input);

        // 找核心词
        CoNLLWord root = new CoNLLWord(1,"a","b");
        for (CoNLLWord word : sentence) {
            if (word.DEPREL.equals("核心关系")) {
                root = word;
                System.out.println("ROOT " + word.LEMMA);
            }
        }

        // 核心词
        Map.Entry<String, String> coreWord = buildCoreWord(root, sentence);

        // 获取第一层语法结构
        // 词列表 <<词@index, 结构名称>, 在句中的索引位置>
        Map<Map.Entry<String, String>, Integer> wordList = getDirectStructures(root, sentence, coreWord);

        // 加入核心词
        String coreWordString = coreWord.getKey();
        String tag = coreWord.getValue();
        // 核心词位置
        int offset = indexOfSentence(sentence, root);
        wordList.put(new AbstractMap.SimpleEntry<>(coreWordString+"@"+offset, tag), offset);

        // 排序
        Collection<Map.Entry<String, String>> entries = MapSortUtil.sortByValueAsc(wordList).keySet();
        return formatOutput(entries);
    }

    // 获取HEAD为root的第一层结构
    private static Map<Map.Entry<String, String>, Integer> getDirectStructures(CoNLLWord root, CoNLLSentence sentence, Map.Entry<String, String> coreString) {
        // 词列表 <<词, 结构名称>, 在句中的索引位置>
        Map<Map.Entry<String, String>, Integer> wordList = new HashMap<>();
        // 辨认其他句子层级结构
        boolean isFoundSubj = false;
        for (CoNLLWord word : sentence) {
            if (word.HEAD == root) {
                String segment = getSubTree(word, sentence);
                String wordTag = "";
                int segOffset = indexOfSentence(sentence, word);
                boolean isIgnore = false;
                boolean isLongSegment = false;
                // 判断是否分段过长
                if (segment.length() >= 7)
                    isLongSegment = true;
                // 判断是否已存在于核心词
                if (word.ID == -1)
                    continue;
                // 判断是否为疑问词
                if (isTypeWord(segment, questionWords, "contain")) {
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, "Q"), segOffset);
                    continue;
                }
                // 判断是否为助词
                if (isTypeWord(word.LEMMA, auxWords, "match")) {
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, "助词"), segOffset);
                    continue;
                }

                switch (word.DEPREL) {
                    case "主谓关系":
                        wordTag = "S";
                        if (!coreString.getValue().equals("V") && !coreString.getValue().equals("neg V")) {
                            if (coreString.getValue().contains("neg"))
                                coreString.setValue("neg P");
                            else
                                coreString.setValue("P");
                        }
                        isFoundSubj = true;
                        break;
                    case "动宾关系":
                    case "介宾关系":
                    case "直接宾语":
                    case "间宾关系":
                        wordTag = "O";
                        break;
                    case "并列关系":
                        isIgnore = true;
                        // 并列核心词
                        Map.Entry<String, String> newCoreWord = buildCoreWord(word, sentence);
                        System.out.println("并列核心："+newCoreWord.getKey());
                        // 并列第一层语法结构
                        Map<Map.Entry<String, String>, Integer> subWordList = getDirectStructures(word, sentence, newCoreWord);

                        wordList.put(new AbstractMap.SimpleEntry<>(newCoreWord.getKey()+"@"+segOffset, newCoreWord.getValue()), segOffset);
                        wordList.putAll(subWordList);
                        break;
                    case "状中结构":
                        wordTag = "A";
                        break;
                    case "定中关系":
                        wordTag = "Attr";
                        break;
                    case "动补结构":
                        wordTag = "Cv";
                        break;
                    case "前置宾语":
                        if (isFoundSubj)
                            wordTag = "O";
                        else
                            wordTag = "S";
                        break;
                    case "标点符号":
                        wordTag = "标点";
                        break;
                    default:
                        break;
                }
                // 如果分段过长且不为并列关系，增看下一层结构
                if (isLongSegment && !isIgnore) {
                    isIgnore = true;
                    // 二层核心词
                    Map.Entry<String, String> newCoreWord = buildCoreWord(word, sentence);
                    System.out.println("二层核心："+newCoreWord.getKey());
                    // 第二层语法结构
                    Map<Map.Entry<String, String>, Integer> subWordList = getDirectStructures(word, sentence, newCoreWord);

                    wordList.put(new AbstractMap.SimpleEntry<>(newCoreWord.getKey()+"@"+segOffset, newCoreWord.getValue()), segOffset);
                    wordList.putAll(subWordList);
                }

                // 判断是否为否定
                if (isTypeWord(segment, negWords, "contain"))
                    wordTag = "neg " + wordTag;
                // 加入wordList
                if (!isIgnore)
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, wordTag), segOffset);
            }
        }

        return wordList;
    }

    private static Map.Entry<String, String> buildCoreWord(CoNLLWord core, CoNLLSentence sentence) {
        // 判断核心词类型
        String tag = "核心词";
        switch (core.CPOSTAG) {
            case "v":
                tag = "V";
                break;
            case "a":
                tag = "adj";
                break;
            default:
                break;
        }
        // 构建核心词（将左附加与右附加关系对应的词加入核心词）
        Map<String, Integer> coreBuilder = new HashMap<>();
        coreBuilder.put(core.LEMMA, indexOfSentence(sentence, core));
        int offset = indexOfSentence(sentence, core);
        for (CoNLLWord word : sentence) {
            if (word.HEAD == core && !isTypeWord(word.LEMMA, questionWords, "contain")) {
                int wordOffset = indexOfSentence(sentence, word);
                if (wordOffset + word.LEMMA.length() == offset || wordOffset - word.LEMMA.length() == offset) {
                    switch (word.DEPREL) {
                        case "右附加关系":
                        case "左附加关系":
                            coreBuilder.put(word.LEMMA, wordOffset);
                            // 标记为在核心词内
                            word.ID = -1;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        // 排序，转为String
        Collection<String> words = MapSortUtil.sortByValueAsc(coreBuilder).keySet();
        StringBuilder coreString = new StringBuilder();
        for (String str : words)
            coreString.append(str);
        // 判断是否为否定
        if (isTypeWord(coreString.toString(), negWords, "contain"))
            tag = "neg " + tag;
        System.out.println("core tag: " + tag);
        return new AbstractMap.SimpleEntry<>(coreString.toString(), tag);
    }

    // 遍历子树，返回整个子树字符串
    private static String getSubTree(CoNLLWord core, CoNLLSentence sentence) {
        Map<String, Integer> subTree = new HashMap<>();
        subTree.put(core.LEMMA, indexOfSentence(sentence, core));
        // 第一层子树
        List<CoNLLWord> subTreeNodes = new LinkedList<>();
        for (CoNLLWord word : sentence) {
            if (word.HEAD == core) {
                subTreeNodes.add(word);
            }
        }

        // 遍历其他子树
        for (int i = 0; i < subTreeNodes.size(); i++) {
            CoNLLWord node = subTreeNodes.get(i);
            // 添加节点信息
            subTree.put(node.LEMMA, indexOfSentence(sentence, node));
            // 添加子树
            for (CoNLLWord word : sentence) {
                if (word.HEAD == node)
                    subTreeNodes.add(word);
            }
        }
        // 排序，转为String
        Collection<String> words = MapSortUtil.sortByValueAsc(subTree).keySet();
        StringBuilder builder = new StringBuilder();
        for (String str : words)
            builder.append(str);
        return builder.toString();
    }

    private static boolean isTypeWord(String word, String[] wordList, String type) {
        for (String string : wordList) {
            if (type.equals("contain") && word.contains(string))
                return true;
            if (type.equals("match") && word.matches(string))
                return true;
        }
        return false;
    }

    private static int indexOfSentence(CoNLLSentence sentence, CoNLLWord word) {
        int index = 0;
        for (CoNLLWord w : sentence.getWordArray()) {
            if (w == word)
                break;
            index++;
        }
        return index;
    }

    private static List<Map.Entry<String, String>> formatOutput(Collection<Map.Entry<String, String>> collection) {
        List<Map.Entry<String, String>> list = new LinkedList<>();
        for (Map.Entry<String, String> entry : collection)
            list.add(new AbstractMap.SimpleEntry<>(StringUtils.substringBefore(entry.getKey(), "@"), entry.getValue()));
        return list;
    }
}


