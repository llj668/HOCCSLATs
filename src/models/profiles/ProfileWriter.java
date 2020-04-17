package models.profiles;

import models.test.Question;
import models.test.grammar.Utterance;
import models.test.pronun.ErrorPattern;
import models.test.pronun.Syllable;
import models.test.results.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class ProfileWriter {
    public final static String PROFILE_PATH = "./src/resources/profiles/";

    public static void writeNewProfileToXML(Profile profile) {
        Document profileXML = DocumentHelper.createDocument();
        Element root = profileXML.addElement("profile");
        root.addAttribute("name", profile.getName());
        root.addAttribute("gender", profile.getGenderString());
        root.addElement("grammar");
        root.addElement("pronun");

        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            File file = new File(PROFILE_PATH + profile.getName() + "_" + LocalDate.now() + ".xml");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(profileXML);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updatePronunResultToXML(Profile profile) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        SAXReader reader = new SAXReader();
        String fileDate = profile.getProfileName().split("_")[1];
        try {
            File xml = new File(PROFILE_PATH + profile.getProfileName() + ".xml");
            Document document = reader.read(xml);
            Element root = document.getRootElement();

            Iterator rootElements = root.elementIterator();
            while (rootElements.hasNext()) {
                Element rootElement = (Element) rootElements.next();

                if (rootElement.getName().equalsIgnoreCase("pronun")) {
                    // check if there is a new result
                    ArrayList<PronunResult> results = profile.getPronunResults();
                    PronunResult newResult = results.get(results.size()-1);
                    if (isOldResult(rootElement, newResult))
                        break;

                    System.out.println("update pronun result");
                    // add new test result to document
                    Element test = rootElement.addElement("test");
                    // attributes
                    test.addAttribute("time", newResult.getTestTime());
                    test.addAttribute("age", newResult.testAge.toString());
                    test.addAttribute("pcc", String.valueOf(newResult.pcc));

                    // syllables
                    Element syllables = test.addElement("syllables");
                    for (Syllable syllable : newResult.syllables) {
                        Element item = syllables.addElement("item");
                        Element pcc = item.addElement("pcc");
                        Element target = item.addElement("target");
                        Element response = item.addElement("response");
                        Element presentConsonant = item.addElement("present_consonant");
                        Element errorPattern = item.addElement("error_pattern");
                        pcc.setText(String.valueOf(syllable.getPcc()));
                        target.setText(syllable.getTarget());
                        response.setText(syllable.getResponse());
                        presentConsonant.setText(syllable.getConsonantsCorrectAsString());

                        for (Map.Entry<ErrorPattern, Integer> entry : syllable.getErrorPatterns().entrySet()) {
                            Element pattern = errorPattern.addElement(entry.getKey().name());
                            pattern.addAttribute("times", entry.getValue().toString());
                        }
                    }

                    // present consonants
                    Element present = test.addElement("pronounced_consonants");
                    present.setText(newResult.getPresentConsonantsAsString());
                }
            }

            String filename = profile.getName() + "_" + LocalDate.now();
            File file = new File(PROFILE_PATH + filename + ".xml");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(document);
            writer.close();

            if (!LocalDate.now().toString().equals(fileDate)) {
                new File(PROFILE_PATH + profile.getProfileName() + ".xml").delete();
            }

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateGrammarResultToXML(Profile profile, GrammarResult result) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        SAXReader reader = new SAXReader();
        String fileDate = profile.getProfileName().split("_")[1];
        try {
            File xml = new File(PROFILE_PATH + profile.getProfileName() + ".xml");
            Document document = reader.read(xml);
            Element root = document.getRootElement();

            Iterator rootElements = root.elementIterator();
            while (rootElements.hasNext()) {
                Element rootElement = (Element) rootElements.next();

                if (rootElement.getName().equalsIgnoreCase("grammar")) {
                    // check if there is a new result
                    if (isOldResult(rootElement, result)) {
                        updateOldGrammarResult(rootElement, result);
                        break;
                    }

                    System.out.println("update grammar result");
                    // add new test result to document
                    Element test = rootElement.addElement("test");
                    // attributes
                    test.addAttribute("time", result.getTestTime());
                    test.addAttribute("age", result.testAge.toString());

                    // stage results
                    Collections.sort(result.stageResults);
                    for (GrammarStage grammarStage : result.stageResults) {
                        Element stage = test.addElement("stage");
                        // attributes
                        stage.addAttribute("stage_no", String.valueOf(grammarStage.getStageNo()));
                        stage.addAttribute("stage_score", String.valueOf(grammarStage.getStageScore()));

                        for (Map.Entry<GrammarStructure, Utterance> entry : grammarStage.getRecords().entrySet()) {
                            Element question = stage.addElement("question");
                            question.addAttribute("present_clause", String.join("/", entry.getValue().getPresentUtteranceStructures()));
                            question.addAttribute("present_phrase", String.join("/", entry.getValue().getPresentPhraseStructures()));
                            question.addAttribute("present_word", String.join("/", entry.getValue().getPresentWordStructures()));

                            Element response_clause = question.addElement("response_clause");
                            for (Map.Entry<String, String> segment : entry.getValue().getAnalyzedUtterance()) {
                                Element structure = response_clause.addElement("structure");
                                structure.setText(segment.getKey());
                                structure.addAttribute("value", segment.getValue());
                            }

                            Element response_phrase = question.addElement("response_phrase");
                            for (Map.Entry<String, String> segment : entry.getValue().getAnalyzedPhrase()) {
                                Element structure = response_phrase.addElement("structure");
                                structure.setText(segment.getKey());
                                structure.addAttribute("value", segment.getValue());
                            }

                            Element response_word = question.addElement("response_word");
                            for (Map.Entry<String, String> segment : entry.getValue().getAnalyzedWord()) {
                                Element structure = response_word.addElement("structure");
                                structure.setText(segment.getKey());
                                structure.addAttribute("value", segment.getValue());
                            }

                            GrammarStructure grammar = entry.getKey();
                            question.addAttribute("name", grammar.name);
                            question.addAttribute("score", String.valueOf(grammar.score));
                            String questionId = "";
                            for (Question q : result.questions.keySet()) {
                                if (q.getTargets().containsKey(entry.getKey().name))
                                    questionId = q.getId();
                            }
                            question.addAttribute("id", questionId);
                            response_clause.addAttribute("utterance", entry.getValue().getUtterance());
                        }
                    }
                }
            }

            String filename = profile.getName() + "_" + LocalDate.now();
            File file = new File(PROFILE_PATH + filename + ".xml");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(document);
            writer.close();

            if (!LocalDate.now().toString().equals(fileDate)) {
                new File(PROFILE_PATH + profile.getProfileName() + ".xml").delete();
            }

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isOldResult(Element rootElement, BaseResult newResult) {
        Iterator testElements = rootElement.elementIterator();
        while (testElements.hasNext()) {
            Element test = (Element) testElements.next();
            if (test.attribute("time").getValue().equals(newResult.getTestTime())) {
                return true;
            }
        }
        return false;
    }

    private static void updateOldGrammarResult(Element rootElement, GrammarResult newResult) {
        Iterator testElements = rootElement.elementIterator();
        while (testElements.hasNext()) {
            Element test = (Element) testElements.next();

            if (test.attribute("time").getValue().equals(newResult.getTestTime())) {
                // stage results
                Collections.sort(newResult.stageResults);

                Iterator stageElements = test.elementIterator();
                while (stageElements.hasNext()) {
                    Element stage = (Element) stageElements.next();

                    if (!stage.getName().equals("stage"))
                        continue;

                    String stageNo = stage.attribute("stage_no").getValue();
                    for (GrammarStage grammarStage : newResult.stageResults) {
                        if (grammarStage.getStageNo() == Integer.parseInt(stageNo)) {
                            stage.attribute("stage_score").setValue(String.valueOf(grammarStage.getStageScore()));

                            Iterator questionElements = stage.elementIterator();
                            while (questionElements.hasNext()) {
                                Element question = (Element) questionElements.next();

                                String name = question.attribute("name").getValue();
                                for (Map.Entry<GrammarStructure, Utterance> entry : grammarStage.getRecords().entrySet()) {
                                    if (entry.getKey().name.equalsIgnoreCase(name))
                                        question.attribute("score").setValue(String.valueOf(entry.getKey().score));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
