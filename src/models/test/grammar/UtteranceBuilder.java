package models.test.grammar;

import java.util.*;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.IDependencyParser;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.hankcs.hanlp.dependency.perceptron.parser.KBeamArcEagerDependencyParser;
import models.services.util.MapSortUtil;
import org.apache.commons.lang3.StringUtils;

public class UtteranceBuilder {
    final static String[] questionWords = {"呢", "吗", "怎么"};
    final static String[] whQuestionWords = {"谁", "哪", "什么"};
    final static String[] negWords = {"不", "没"};
    final static String[] auxWords = {"了", "吧", "的"};
    final static String[] pronP = {"我", "你", "他", "她", "它", "我们", "你们", "他们", "她们", "它们"};
    final static String[] auxVerbs = {"能", "想", "在"};
    final static String[] specialWords = {"把", "被", "给"};

    public static void main(String[] arg) {
        List<Map.Entry<String, String>> analyzed = analyzePhrase("乌龟叫兔子去河边的森林参加比赛", new NeuralNetworkDependencyParser());
        System.out.println();
        System.out.println("结果：");
        for (Map.Entry<String, String> entry : analyzed) {
            System.out.print(entry.getKey() + "（" + entry.getValue() + "）");
        }
        System.out.println();
    }

    public static List<Map.Entry<String, String>> analyzeClause(String input, IDependencyParser parser){
        if (input == null || parser == null || input.equals(""))
            return new LinkedList<>();
        CoNLLSentence sentence = parser.parse(input);
        System.out.println(sentence);

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
        // Map<Map.Entry<String, String>, Integer> wordList = getSecondaryStructures(root, sentence, coreWord);

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

    public static List<Map.Entry<String, String>> analyzePhrase(String input, IDependencyParser parser){
        if (input == null || parser == null || input.equals(""))
            return new LinkedList<>();
        CoNLLSentence sentence = parser.parse(input);

        // 获取所有词组结构的列表
        // 词列表 <<词@index, 结构名称>, 在句中的索引位置>
        Map<Map.Entry<String, String>, Integer> wordList = new HashMap<>();

        // 偏正结构
        for (CoNLLWord word : sentence) {
            if ((word.DEPREL.equals("定中关系") || word.DEPREL.equals("状中结构")) && word.HEAD.CPOSTAG.equals("n")) {
                wordList.put(new AbstractMap.SimpleEntry<>(getSubTree(word.HEAD, sentence, new String[]{"定中关系", "状中结构"}), "Endo"), indexOfSentence(sentence, word));
            }
        }

        // 复字
        String prevChar = null;
        for(int i=0; i<input.length(); i++){
            if (prevChar != null) {
                String curChar = input.substring(i, i + 1);
                if (prevChar.equals(curChar))
                    wordList.put(new AbstractMap.SimpleEntry<>(prevChar + curChar, "N-dup"), input.indexOf(prevChar + curChar));
            }
            prevChar = input.substring(i, i + 1);
        }

        // 代词名词，人称代词
        List<CoNLLWord> pronWords = new LinkedList<>();
        for (CoNLLWord word : sentence) {
            if (word.CPOSTAG.equals("r"))
                pronWords.add(word);
        }
        for (CoNLLWord word : pronWords) {
            if (pronWords.contains(word.HEAD))
                continue;
            String wordString = getSubTree(word, sentence);
            if (isTypeWord(wordString, pronP, "match"))
                wordList.put(new AbstractMap.SimpleEntry<>(wordString, "pron p"), indexOfSentence(sentence, word));
            else
                wordList.put(new AbstractMap.SimpleEntry<>(wordString, "pron n"), indexOfSentence(sentence, word));
        }

        // 情态动词
        for (CoNLLWord word : sentence) {
            if (isTypeWord(word.LEMMA, auxVerbs, "contain"))
                wordList.put(new AbstractMap.SimpleEntry<>(word.LEMMA, "aux"), indexOfSentence(sentence, word));
        }

        // x-组合

        // 连谓
        CoNLLWord prevWord = null;
        for (CoNLLWord word : sentence) {
            if (prevWord != null) {
                if (word.CPOSTAG.equals("v") && prevWord.CPOSTAG.equals("v") && !word.DEPREL.equals("动补结构"))
                    wordList.put(new AbstractMap.SimpleEntry<>(prevWord.LEMMA+word.LEMMA, "vv"), indexOfSentence(sentence, prevWord));
            }
            prevWord = word;
        }

        // N(x-de) x-的构名词
        for (CoNLLWord word : sentence) {
            if (word.LEMMA.equals("的") && word.DEPREL.equals("右附加关系")) {
                if (word.HEAD.CPOSTAG.equals("r") || word.HEAD.CPOSTAG.equals("n"))
                    wordList.put(new AbstractMap.SimpleEntry<>(word.HEAD.LEMMA+word.LEMMA, "N(x-de)"), indexOfSentence(sentence, word.HEAD));
            }
        }

        // 代-数-量词组
        for (CoNLLWord word : sentence) {
            if (word.CPOSTAG.equals("n")) {
                boolean hasPron = false;
                boolean hasNum = false;
                for (CoNLLWord child : sentence) {
                    if (child.HEAD == word) {
                        if (child.CPOSTAG.equals("r"))
                            hasPron = true;
                        if (child.CPOSTAG.equals("m"))
                            hasNum = true;
                    }
                }
                if (hasNum && hasPron)
                    wordList.put(new AbstractMap.SimpleEntry<>(getSubTree(word, sentence), "pron-nu-m"), indexOfSentence(sentence, word));
            }
        }

        // 副词形容词组
        prevWord = null;
        for (CoNLLWord word : sentence) {
            if (prevWord != null) {
                if (prevWord.CPOSTAG.equals("d") && word.CPOSTAG.equals("a"))
                    wordList.put(new AbstractMap.SimpleEntry<>(prevWord.LEMMA+word.LEMMA, "adv.adj."), indexOfSentence(sentence, prevWord));
            }
            prevWord = word;
        }

        // 副词形容词名词组

        // N(xy-de) xy-的构名词
        CoNLLWord nounWord = null;
        CoNLLWord yWord = null;
        for (CoNLLWord word : sentence) {
            if (word.LEMMA.equals("的") && word.DEPREL.equals("右附加关系")) {
                if (word.HEAD.HEAD.CPOSTAG.equals("n")) {
                    nounWord = word.HEAD.HEAD;
                    yWord = word.HEAD;
                }
            }
        }
        for (CoNLLWord word : sentence) {
            if (word.HEAD == nounWord || word.HEAD == yWord) {
                if (indexOfSentence(sentence, word) + 1 == indexOfSentence(sentence, yWord)) {
                    assert nounWord != null;
                    wordList.put(new AbstractMap.SimpleEntry<>(getSubTree(nounWord, sentence), "N(xy-de)"), indexOfSentence(sentence, word));
                }
            }
        }

        // 动否动补
        for (CoNLLWord word : sentence) {
            if (word.DEPREL.equals("动补结构") && isTypeWord(word.LEMMA, negWords, "contain"))
                wordList.put(new AbstractMap.SimpleEntry<>(word.HEAD.LEMMA+word.LEMMA, "v neg cv"), indexOfSentence(sentence, word.HEAD));
        }

        // 介疑问词
        for (CoNLLWord word : sentence) {
            if (word.DEPREL.equals("介宾关系") && isTypeWord(word.LEMMA, whQuestionWords, "contain")) {
                if (indexOfSentence(sentence, word) - indexOfSentence(sentence, word.HEAD) != 1)
                    wordList.put(new AbstractMap.SimpleEntry<>(word.HEAD.LEMMA+"..."+word.LEMMA, "prep Q"), indexOfSentence(sentence, word.HEAD));
                else
                    wordList.put(new AbstractMap.SimpleEntry<>(word.HEAD.LEMMA+word.LEMMA, "prep Q"), indexOfSentence(sentence, word.HEAD));
            }
        }

        // 情态动词组合

        // 否 动词
        for (CoNLLWord word : sentence) {
            if (word.DEPREL.equals("状中结构") && isTypeWord(word.LEMMA, negWords, "contain") && word.HEAD.CPOSTAG.equals("v"))
                wordList.put(new AbstractMap.SimpleEntry<>(word.LEMMA+word.HEAD.LEMMA, "neg v"), indexOfSentence(sentence, word));
            else if (word.CPOSTAG.equals("v") && isTypeWord(word.LEMMA, negWords, "contain"))
                wordList.put(new AbstractMap.SimpleEntry<>(word.LEMMA, "neg v"), indexOfSentence(sentence, word));
        }

        // 补语词组合

        // 总括词-量-名词

        // 同位词
        for (CoNLLWord word : sentence) {
            if (word.DEPREL.equals("并列关系")) {
                String tag = "";
                if (word.CPOSTAG.equals("r") && word.HEAD.CPOSTAG.equals("r")) {
                    tag = "Apposit (pron-pron)";
                } else if ((word.CPOSTAG.equals("r") && word.HEAD.CPOSTAG.equals("n")) || (word.CPOSTAG.equals("n") && word.HEAD.CPOSTAG.equals("r"))) {
                    tag = "Apposit (n-pron)";
                } else {
                    continue;
                }

                if (indexOfSentence(sentence, word) > indexOfSentence(sentence, word.HEAD))
                    wordList.put(new AbstractMap.SimpleEntry<>(word.HEAD.LEMMA + "..." + word.LEMMA, tag), indexOfSentence(sentence, word.HEAD));
                else
                    wordList.put(new AbstractMap.SimpleEntry<>(word.LEMMA + "..." + word.HEAD.LEMMA, tag), indexOfSentence(sentence, word));
            }
        }

        // 动-否-动词

        // 数-量词-名词组合

        // 否-补语组合

        // 名词短语加名词短语

        // 排序
        Collection<Map.Entry<String, String>> entries = MapSortUtil.sortByValueAsc(wordList).keySet();
        return formatOutput(entries);
    }

    public static List<Map.Entry<String, String>> analyzeWord(String input, IDependencyParser parser){
        if (input == null || parser == null || input.equals(""))
            return new LinkedList<>();
        CoNLLSentence sentence = parser.parse(input);

        // 获取所有词前后缀结构的列表
        // 词列表 <<词@index, 结构名称>, 在句中的索引位置>
        Map<Map.Entry<String, String>, Integer> wordList = new HashMap<>();

        // suffix-zi
        for (CoNLLWord word : sentence) {
            String str = word.LEMMA;
            if (str.length() > 1 && str.substring(str.length()).equals("子"))
                wordList.put(new AbstractMap.SimpleEntry<>(str, "suffix-zi"), indexOfSentence(sentence, word));
        }

        // de-n
        for (CoNLLWord word : sentence) {

        }

        // de-poss

        // de-emp

        // de-attr

        // de-con

        // de-ly

        // de-Cv
        for (CoNLLWord word : sentence) {

        }

        // pl

        // tm(ed)
        CoNLLWord preWord = null;
        for (CoNLLWord word : sentence) {
            if (preWord == null) {
                preWord = word;
                continue;
            }
            if (preWord.CPOSTAG.equals("v") && word.LEMMA.equals("了"))
                wordList.put(new AbstractMap.SimpleEntry<>(preWord.LEMMA+word.LEMMA, "ed"), indexOfSentence(sentence, word));
            preWord = word;
        }

        // tm(ing-v)
        preWord = null;
        for (CoNLLWord word : sentence) {
            if (preWord == null) {
                preWord = word;
                continue;
            }
            if (word.CPOSTAG.equals("v") && preWord.LEMMA.contains("在"))
                wordList.put(new AbstractMap.SimpleEntry<>(preWord.LEMMA+word.LEMMA, "ing-v"), indexOfSentence(sentence, word));
            preWord = word;
        }

        // pre-fix
        for (CoNLLWord word : sentence) {
            if (word.CPOSTAG.equals("m") && word.LEMMA.substring(1).equals("第"))
                wordList.put(new AbstractMap.SimpleEntry<>(word.LEMMA, "pre-fix"), indexOfSentence(sentence, word));
        }

        // pre-fix est
        for (CoNLLWord word : sentence) {
            if (word.LEMMA.substring(1).equals("最"))
                wordList.put(new AbstractMap.SimpleEntry<>(word.LEMMA, "pre-fix-est"), indexOfSentence(sentence, word));
        }

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
                List<String> tag = new LinkedList<>();
                int segOffset = indexOfSentence(sentence, word);
                boolean isIgnore = false;
                boolean isLongSegment = false;
                // 判断是否分段过长
                if (segment.length() >= 7)
                    isLongSegment = true;
                // 判断是否已存在于核心词
                if (word.ID == -1)
                    continue;
                // 判断是否为否定
                if (isTypeWord(segment, negWords, "contain"))
                    tag.add("neg");
                // 判断是否包含 把 被 给
                if (isTypeWord(segment, specialWords, "match")) {
                    switch (segment) {
                        case "把":
                            tag.add("Ba");
                            break;
                        case "被":
                            tag.add("Bei");
                            break;
                        case "给":
                            tag.add("Gei");
                            break;
                    }
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, String.join(" ", tag)), segOffset);
                    continue;
                } else if (isTypeWord(segment, specialWords, "contain")) {
                    if (segment.contains("把"))
                        tag.add("Ba");
                    else if (segment.contains("被"))
                        tag.add("Bei");
                    else if (segment.contains("给"))
                        tag.add("Gei");
                }
                // 判断是否为疑问词
                if (isTypeWord(segment, questionWords, "contain")) {
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, "Q"), segOffset);
                    continue;
                }
                if (isTypeWord(segment, whQuestionWords, "contain")) {
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, "Wh"), segOffset);
                    continue;
                }
                // 判断是否为助词
                if (isTypeWord(word.LEMMA, auxWords, "match")) {
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, "助词"), segOffset);
                    continue;
                }

                switch (word.DEPREL) {
                    case "主谓关系":
                        tag.add("S");
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
                        tag.add("O");
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
                        tag.add("A");
                        break;
                    case "定中关系":
                        tag.add("Attr");
                        break;
                    case "动补结构":
                        tag.add("Cv");
                        break;
                    case "兼语":
                        tag.add("Os");
                        break;
                    case "前置宾语":
                        if (isFoundSubj)
                            tag.add("O");
                        else
                            tag.add("S");
                        break;
                    case "左附加结构":
                    case "右附加结构":
                        tag.add(getTagByPOS(word.CPOSTAG));
                        break;
                    case "标点符号":
                        tag.add("标点");
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

                // 加入wordList
                if (!isIgnore)
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, String.join(" ", tag)), segOffset);
            }
        }

        return wordList;
    }

    // 获取HEAD为root的第一层结构
    private static Map<Map.Entry<String, String>, Integer> getSecondaryStructures(CoNLLWord root, CoNLLSentence sentence, Map.Entry<String, String> coreString) {
        // 词列表 <<词, 结构名称>, 在句中的索引位置>
        Map<Map.Entry<String, String>, Integer> wordList = new HashMap<>();
        // 辨认其他句子层级结构
        boolean isFoundSubj = false;
        for (CoNLLWord word : sentence) {
            if (word.HEAD == root) {
                String segment = word.LEMMA;
                String wordTag = "";
                int segOffset = indexOfSentence(sentence, word);
                boolean isSkip = false;

                // 判断是否已存在于核心词
                if (word.ID == -1)
                    continue;
                // 判断是否为疑问词
                if (isTypeWord(segment, questionWords, "contain")) {
                    wordTag = "Q";
                    isSkip = true;
                }
                // 判断是否为助词
                if (isTypeWord(word.LEMMA, auxWords, "match") && !isSkip) {
                    wordTag = "助词";
                    isSkip = true;
                }

                if (!isSkip) {
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
                            wordTag = coreString.getValue();
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

                    // 判断是否为否定
                    if (isTypeWord(segment, negWords, "contain"))
                        wordTag = "neg " + wordTag;
                }

                // 判断是否为叶
                boolean isLeaf = true;
                for (CoNLLWord child : sentence) {
                    if (child.HEAD == word) {
                        isLeaf = false;
                        break;
                    }
                }
                if (!isLeaf) {
                    // 下一层核心词
                    Map.Entry<String, String> newCoreWord;
                    if (isSkip)
                        newCoreWord = buildCoreWord(word, sentence);
                    else
                        newCoreWord = new AbstractMap.SimpleEntry<>(segment, wordTag);
                    // 下一层语法结构
                    Map<Map.Entry<String, String>, Integer> subWordList = getSecondaryStructures(word, sentence, newCoreWord);

                    wordList.put(new AbstractMap.SimpleEntry<>(newCoreWord.getKey()+"@"+segOffset, newCoreWord.getValue()), segOffset);
                    wordList.putAll(subWordList);
                } else {
                    // 加入wordList
                    wordList.put(new AbstractMap.SimpleEntry<>(segment+"@"+segOffset, wordTag), segOffset);
                }
            }
        }

        return wordList;
    }

    private static Map.Entry<String, String> buildCoreWord(CoNLLWord core, CoNLLSentence sentence) {
        // 判断核心词类型
        String tag = getTagByPOS(core.CPOSTAG);
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

    private static String getSubTree(CoNLLWord core, CoNLLSentence sentence, String[] limit) {
        Map<String, Integer> subTree = new HashMap<>();
        subTree.put(core.LEMMA, indexOfSentence(sentence, core));
        // 第一层子树
        List<CoNLLWord> subTreeNodes = new LinkedList<>();
        for (CoNLLWord word : sentence) {
            if (word.HEAD == core && Arrays.toString(limit).contains(word.DEPREL)) {
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

    private static String getTagByPOS(String pos) {
        String tag;
        switch (pos) {
            case "v":
                tag = "V";
                break;
            case "a":
                tag = "adj";
                break;
            case "n":
            case "nt":
                tag = "noun";
                break;
            case "r":
                tag = "pron";
                break;
            default:
                tag = "";
                break;
        }
        return tag;
    }

    // 判断是哪种词
    private static boolean isTypeWord(String word, String[] wordList, String type) {
        for (String string : wordList) {
            if (type.equals("contain") && word.contains(string))
                return true;
            if (type.equals("match") && word.matches(string))
                return true;
        }
        return false;
    }

    // 返回词在句中位置
    private static int indexOfSentence(CoNLLSentence sentence, CoNLLWord word) {
        int index = 0;
        for (CoNLLWord w : sentence.getWordArray()) {
            if (w == word)
                break;
            index++;
        }
        return index;
    }

    // 整理输出
    private static List<Map.Entry<String, String>> formatOutput(Collection<Map.Entry<String, String>> collection) {
        List<Map.Entry<String, String>> list = new LinkedList<>();
        for (Map.Entry<String, String> entry : collection)
            list.add(new AbstractMap.SimpleEntry<>(StringUtils.substringBefore(entry.getKey(), "@"), entry.getValue()));
        return list;
    }

}


