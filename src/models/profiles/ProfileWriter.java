package models.profiles;

import models.test.grammar.Utterance;
import models.test.pronun.Syllable;
import models.test.results.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class ProfileWriter {
    final static String PROFILE_PATH = "./src/resources/profiles/";

    public static void writeNewProfileToXML(Profile profile) {
        Document profileXML = DocumentHelper.createDocument();
        Element root = profileXML.addElement("profile");
        root.addAttribute("name", profile.getInfo().get("name"));
        root.addAttribute("gender", profile.getInfo().get("gender"));
        root.addElement("grammar");
        root.addElement("pronun");

        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            File file = new File(PROFILE_PATH + profile.getInfo().get("name") + "_" + LocalDate.now() + ".xml");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(profileXML);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateProfileResultToXML(Profile profile, String updateType) {
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
                if (rootElement.getName().equalsIgnoreCase("grammar") && updateType.equalsIgnoreCase("grammar")) {
                    // check if there is a new result
                    ArrayList<GrammarResult> results = profile.getGrammarResults();
                    GrammarResult newResult = results.get(results.size()-1);
                    if (!isNewResult(rootElement, newResult))
                        break;

                    System.out.println("update grammar result");
                    // add new test result to document
                    Element test = rootElement.addElement("test");
                    // attributes
                    test.addAttribute("time", newResult.getTestTime());
                    test.addAttribute("age", newResult.testAge);
                    test.addAttribute("score", String.valueOf(newResult.score));

                    // stage results
                    Collections.sort(newResult.stageResults);
                    for (GrammarStage grammarStage : newResult.stageResults) {
                        Element stage = test.addElement("stage");
                        // attributes
                        stage.addAttribute("stage_no", String.valueOf(grammarStage.getStageNo()));
                        stage.addAttribute("stage_score", String.valueOf(grammarStage.getStageScore()));

                        for (Map.Entry<GrammarStructure, Utterance> entry : grammarStage.getRecords().entrySet()) {
                            Element question = stage.addElement("question");
                            Element response = question.addElement("response");

                            for (Map.Entry<String, String> segment : entry.getValue().getAnalyzedUtterance()) {
                                Element structure = response.addElement("structure");
                                structure.setText(segment.getKey());
                                structure.addAttribute("value", segment.getValue());
                            }

                            GrammarStructure grammar = entry.getKey();
                            question.addAttribute("name", grammar.name.toString());
                            question.addAttribute("score", String.valueOf(grammar.score));
                            response.addAttribute("utterance", entry.getValue().getUtterance());
                        }
                    }
                } else if (rootElement.getName().equalsIgnoreCase("pronun") && updateType.equalsIgnoreCase("pronun")) {
                    // check if there is a new result
                    ArrayList<PronunResult> results = profile.getPronunResults();
                    PronunResult newResult = results.get(results.size()-1);
                    if (!isNewResult(rootElement, newResult))
                        break;

                    System.out.println("update pronun result");
                    // add new test result to document
                    Element test = rootElement.addElement("test");
                    // attributes
                    test.addAttribute("time", newResult.getTestTime());
                    test.addAttribute("age", newResult.testAge);
                    test.addAttribute("pcc", String.valueOf(newResult.pcc));

                    // syllables
                    Element syllables = test.addElement("syllables");
                    for (Syllable syllable : newResult.syllables) {
                        Element item = syllables.addElement("item");
                        Element target = item.addElement("target");
                        Element response = item.addElement("response");
                        Element presentConsonant = item.addElement("present_consonant");
                        Element errorPattern = item.addElement("error_pattern");
                        target.setText(syllable.getTarget());
                        response.setText(syllable.getResponse());
                        presentConsonant.setText(syllable.getConsonantsCorrectAsString());
                        errorPattern.setText(syllable.getErrorPatternsAsString());
                    }

                    // present consonants
                    Element present = test.addElement("pronounced_consonants");
                    present.setText(newResult.getPresentConsonantsAsString());
                }
            }

            String filename = profile.getInfo().get("name") + "_" + LocalDate.now();
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

    private static boolean isNewResult(@NotNull Element rootElement, BaseResult newResult) {
        Iterator testElements = rootElement.elementIterator();
        while (testElements.hasNext()) {
            Element test = (Element) testElements.next();
            if (test.attribute("time").getValue().equals(newResult.testTime.toString())) {
                return false;
            }
        }
        return true;
    }
}
