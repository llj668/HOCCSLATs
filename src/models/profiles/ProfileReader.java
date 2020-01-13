package models.profiles;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

import models.test.results.GrammarResult;
import models.test.results.GrammarStructure;
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
						results.add(new GrammarResult(null, null, test.attribute("age").getValue()));
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		System.out.println(results.size());
		return results;
	}

}
