package models.profiles;

import models.test.Response;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
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

    public static void updateProfileResultToXML(Profile profile) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        SAXReader reader = new SAXReader();
        try {
            File xml = new File(PROFILE_PATH + profile.getProfileName() + ".xml");
            Document document = reader.read(xml);
            Element root = document.getRootElement();

            Iterator rootElements = root.elementIterator();
            while (rootElements.hasNext()) {

                Element rootElement = (Element) rootElements.next();
                if (rootElement.getName().equalsIgnoreCase("grammar")) {
                    // check if there is a new result
                    ArrayList<GrammarResult> results = profile.getGrammarResults();
                    GrammarResult newResult = results.get(results.size()-1);

                    Iterator grammarElements = rootElement.elementIterator();
                    boolean hasNew = true;
                    while (grammarElements.hasNext()) {
                        Element test = (Element) grammarElements.next();
                        if (test.attribute("time").getValue().equals(newResult.testTime.toString())) {
                            hasNew = false;
                            break;
                        }
                    }

                    // add new test result to document
                    if (hasNew) {
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

                            for (Map.Entry<Response, GrammarStructure> entry : grammarStage.getRecords().entrySet()) {
                                Element question = stage.addElement("question");
                                Element response = question.addElement("response");
                                response.setText(entry.getKey().getResponse());

                                GrammarStructure grammar = entry.getValue();
                                question.addAttribute("name", grammar.name.toString());
                                question.addAttribute("score", String.valueOf(grammar.score));
                            }
                        }
                    }
                } else if (rootElement.getName().equalsIgnoreCase("pronun")) {
                    // todo write pronun result
                }
            }

            String filename = profile.getInfo().get("name").split("_")[0] + "_" + LocalDate.now();
            File file = new File(PROFILE_PATH + filename + ".xml");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(document);
            writer.close();

            // delete old profile
            new File(PROFILE_PATH + profile.getInfo().get("name")).delete();

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
