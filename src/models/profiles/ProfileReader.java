package models.profiles;

import java.io.*;
import java.lang.reflect.Array;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import models.test.grammar.Utterance;
import models.test.pronun.Syllable;
import models.test.results.GrammarResult;
import models.test.results.GrammarStage;
import models.test.results.GrammarStructure;
import models.test.results.PronunResult;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProfileReader {
	public final static String PROFILE_PATH = "./src/resources/profiles/";

	public static Profile readProfileFromXML(File xml) {
		HashMap<String, String> info = new HashMap<>();
		List<String> testAges = new LinkedList<>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(xml);
			Element root = document.getRootElement();
			// get attribute info
			for(Attribute attribute : root.attributes()){
				info.put(attribute.getName(), attribute.getValue());
			}
			info.put("profileName", xml.getName().split("\\.")[0]);

			// read test ages
			Iterator rootElements = root.elementIterator();
			while (rootElements.hasNext()) {
				Element rootElement = (Element) rootElements.next();
				Iterator testElements = rootElement.elementIterator();
				while (testElements.hasNext()) {
					Element test = (Element) testElements.next();
					String age = test.attribute("age").getValue();
					if (!testAges.contains(age))
						testAges.add(age);
				}
			}
			info.put("ages", String.join(",", testAges));

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return new Profile(info);
	}

	public static ArrayList<GrammarResult> readGrammarResultsFromXML(String filename) {
		ArrayList<GrammarResult> results = new ArrayList<>();
		SAXReader reader = new SAXReader();
		try {
			File xml = new File(PROFILE_PATH + filename + ".xml");

			Document document = reader.read(xml);
			Element root = document.getRootElement();

			Iterator rootElements = root.elementIterator();
			while (rootElements.hasNext()) {
				Element rootElement = (Element) rootElements.next();

				if (rootElement.getName().equalsIgnoreCase("grammar")) {

					Iterator testElements = rootElement.elementIterator();
					while (testElements.hasNext()) {
						Element test = (Element) testElements.next();

						List<GrammarStage> stageResults = new LinkedList<>();
						SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String testTime = test.attribute("time").getValue();
						String testAge = test.attribute("age").getValue();
						String testScore = test.attribute("score").getValue();

						Iterator stageElements = test.elementIterator();
						while (stageElements.hasNext()) {
							Element stage = (Element) stageElements.next();

							String stageNo = stage.attribute("stage_no").getValue();
							String stageScore = stage.attribute("stage_score").getValue();
							GrammarStage grammarStage = new GrammarStage(Integer.parseInt(stageNo));
							grammarStage.setStageScore(Double.parseDouble(stageScore));

							Iterator questionElements = stage.elementIterator();
							while (questionElements.hasNext()) {
								Element question = (Element) questionElements.next();

								String target = question.attribute("name").getValue();
								String score = question.attribute("score").getValue();

								Iterator responseElements = question.elementIterator();
								while (responseElements.hasNext()) {
									Element response = (Element) responseElements.next();

									String utterance = response.attribute("utterance").getValue();
									List<Map.Entry<String, String>> analyzed = new LinkedList<>();

									Iterator structureElements = response.elementIterator();
									while (structureElements.hasNext()) {
										Element structure = (Element) structureElements.next();
										analyzed.add(new AbstractMap.SimpleEntry<>(structure.getStringValue(), structure.attribute("value").getValue()));
									}

									grammarStage.addRecord(new GrammarStructure(target, Integer.parseInt(score)), new Utterance(utterance, analyzed));
								}
							}
							stageResults.add(grammarStage);
						}
						results.add(new GrammarResult(stageResults, f.parse(testTime, new ParsePosition(0)), testAge, testScore));
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return results;
	}

	public static ArrayList<PronunResult> readPronunResultsFromXML(String filename) {
		ArrayList<PronunResult> results = new ArrayList<>();
		SAXReader reader = new SAXReader();
		try {
			File xml = new File(PROFILE_PATH + filename + ".xml");

			Document document = reader.read(xml);
			Element root = document.getRootElement();

			Iterator rootElements = root.elementIterator();
			while (rootElements.hasNext()) {
				Element rootElement = (Element) rootElements.next();

				if (rootElement.getName().equalsIgnoreCase("pronun")) {

					Iterator testElements = rootElement.elementIterator();
					while (testElements.hasNext()) {
						Element test = (Element) testElements.next();

						List<Syllable> syllables = new LinkedList<>();
						List<String> presentConsonants = new LinkedList<>();
						SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String testTime = test.attribute("time").getValue();
						String testAge = test.attribute("age").getValue();
						String testScore = test.attribute("pcc").getValue();

						Iterator resultElements = test.elementIterator();
						while (resultElements.hasNext()) {
							Element result = (Element) resultElements.next();

							if (result.getName().equalsIgnoreCase("syllables")) {

								Iterator syllableElements = result.elementIterator();
								while (syllableElements.hasNext()) {
									Element syllable = (Element) syllableElements.next();

									HashMap<String, String> syllableData = new HashMap<>();

									Iterator dataElements = syllable.elementIterator();
									while (dataElements.hasNext()) {
										Element data = (Element) dataElements.next();
										syllableData.put(data.getName(), data.getStringValue());
									}
									syllables.add(new Syllable(syllableData));
								}
							} else if (result.getName().equalsIgnoreCase("pronounced_consonants")) {
								presentConsonants = Arrays.asList(result.getStringValue().split(","));
							}
						}
						results.add(new PronunResult(testAge, f.parse(testTime, new ParsePosition(0)), testScore, syllables, presentConsonants));
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return results;
	}

}
