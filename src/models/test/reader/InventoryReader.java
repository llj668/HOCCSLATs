package models.test.reader;

import application.PropertyManager;
import models.profiles.Age;
import models.test.Question;
import models.test.grammar.Utterance;
import models.test.pronun.ErrorPattern;
import models.test.pronun.Inventory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class InventoryReader {

    public static List<Inventory> readInventoryFromXML(String inventoryPath) {
        List<Inventory> inventories = new LinkedList<>();
        SAXReader reader = new SAXReader();
        try {
            File xml = new File(inventoryPath);
            Document document = reader.read(xml);
            Element root = document.getRootElement();

            Iterator rootElements = root.elementIterator();
            while (rootElements.hasNext()) {
                Element rootElement = (Element) rootElements.next();

                Inventory inventory = new Inventory(rootElement.attribute("period").getValue());

                Iterator categoryElements = rootElement.elementIterator();
                while (categoryElements.hasNext()) {
                    Element category = (Element) categoryElements.next();

                    Map<String, Boolean> categoryConsonants = new HashMap<>();
                    String categoryName = category.getName();

                    Iterator paElements = category.elementIterator();
                    while (paElements.hasNext()) {
                        Element pa = (Element) paElements.next();

                        if (pa.getStringValue() == null || pa.getStringValue().equalsIgnoreCase(""))
                            continue;
                        boolean isPresent = pa.getName().equalsIgnoreCase("present");
                        for (String consonant : pa.getStringValue().split(","))
                            categoryConsonants.put(consonant, isPresent);
                    }
                    inventory.setCategory(categoryConsonants, categoryName);
                }
                inventories.add(inventory);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return inventories;
    }

    public static Map<ErrorPattern, Boolean> readErrorPatternInventory(String inventoryPath, Age age) {
        Map<ErrorPattern, Boolean> inventory = new LinkedHashMap<>();
        SAXReader reader = new SAXReader();
        try {
            File xml = new File(inventoryPath);
            Document document = reader.read(xml);
            Element root = document.getRootElement();

            Iterator rootElements = root.elementIterator();
            while (rootElements.hasNext()) {
                Element rootElement = (Element) rootElements.next();

                if (age.isInAgePeriod(rootElement.attribute("period").getValue())) {

                    Iterator errorElements = rootElement.elementIterator();
                    while (errorElements.hasNext()) {
                        Element error = (Element) errorElements.next();

                        inventory.put(ErrorPattern.valueOf(error.getName()), Boolean.valueOf(error.attribute("present").getValue()));
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return inventory;
    }

    public static Question readQuestionFromXML(String filepath, String filename, Age testAge) {
        SAXReader reader = new SAXReader();
        try {
            File xml = new File(PropertyManager.getResourceProperty("grammar_questioninfo") + "question_" + filename + ".xml");
            Document document = reader.read(xml);
            Element root = document.getRootElement();

            String text = root.attribute("text").getValue();
            String stage = root.attribute("stage").getValue();
            String id = root.attribute("id").getValue();
            Map<String, Integer> targets = new LinkedHashMap<>();

            Iterator rootElements = root.elementIterator();
            while (rootElements.hasNext()) {
                Element period = (Element) rootElements.next();

                if (testAge.isInAgePeriod(period.attribute("period").getValue())) {

                    Iterator targetElements = period.elementIterator();
                    while (targetElements.hasNext()) {
                        Element target = (Element) targetElements.next();
                        targets.put(target.getStringValue(), Integer.parseInt(target.attributeValue("stage")));
                    }
                    break;
                }
            }
            return new Question(id, filepath, stage, text, targets);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getSampleAnswersFromXML(Question question) {
        SAXReader reader = new SAXReader();
        try {
            File xml = new File(PropertyManager.getResourceProperty("grammar_sampleanswer") + "answer_" + question.getId() + ".xml");
            Document document = reader.read(xml);
            Element root = document.getRootElement();
            Map<Utterance, Map<String, Integer>> sampleAnswers = new LinkedHashMap<>();

            Iterator rootElements = root.elementIterator();
            while (rootElements.hasNext()) {
                Element answer = (Element) rootElements.next();

                String utterance = "";
                List<Map.Entry<String, String>> analyzedUtterance = new LinkedList<>();
                List<Map.Entry<String, String>> analyzedPhrase = new LinkedList<>();
                List<Map.Entry<String, String>> analyzedWord = new LinkedList<>();
                List<String> presentUtteranceStructures = new LinkedList<>();
                List<String> presentPhraseStructures = new LinkedList<>();
                List<String> presentWordStructures = new LinkedList<>();
                Map<String, Integer> marked = new LinkedHashMap<>();

                Iterator answerElements = answer.elementIterator();
                while (answerElements.hasNext()) {
                    Element item = (Element) answerElements.next();

                    if (item.getName().equals("response_clause")) {
                        utterance = item.attributeValue("utterance");
                        Iterator clauseElements = item.elementIterator();
                        while (clauseElements.hasNext()) {
                            Element structure = (Element) clauseElements.next();

                            analyzedUtterance.add(new AbstractMap.SimpleEntry<>(structure.getStringValue(), structure.attributeValue("value")));
                        }
                    } else if (item.getName().equals("response_phrase")) {
                        Iterator phraseElements = item.elementIterator();
                        while (phraseElements.hasNext()) {
                            Element structure = (Element) phraseElements.next();

                            analyzedPhrase.add(new AbstractMap.SimpleEntry<>(structure.getStringValue(), structure.attributeValue("value")));
                        }
                    } else if (item.getName().equals("response_word")) {
                        Iterator wordElements = item.elementIterator();
                        while (wordElements.hasNext()) {
                            Element structure = (Element) wordElements.next();

                            analyzedWord.add(new AbstractMap.SimpleEntry<>(structure.getStringValue(), structure.attributeValue("value")));
                        }
                    } else if (item.getName().equals("scores")) {
                        Iterator scoreElements = item.elementIterator();
                        while (scoreElements.hasNext()) {
                            Element structure = (Element) scoreElements.next();

                            String target = structure.getStringValue();
                            if (question.getTargets().containsKey(target)) {
                                int score = Integer.parseInt(structure.attributeValue("score"));
                                marked.put(target, score);
                                if (score == 5 || score == 3) {
                                    if (structure.attributeValue("type").equals("clause"))
                                        presentUtteranceStructures.add(target);
                                    if (structure.attributeValue("type").equals("phrase"))
                                        presentPhraseStructures.add(target);
                                    if (structure.attributeValue("type").equals("word"))
                                        presentWordStructures.add(target);
                                }
                            }
                        }
                    }
                }

                Utterance answerUtterance = new Utterance(utterance, analyzedUtterance, analyzedPhrase, analyzedWord,
                        presentUtteranceStructures, presentPhraseStructures, presentWordStructures);
                sampleAnswers.put(answerUtterance, marked);
            }
            question.setSampleAnswers(sampleAnswers);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

}
