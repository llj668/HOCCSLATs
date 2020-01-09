package models.profiles;

import java.io.*;
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

	public Profile readProfileFromXML(File xml) {
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
			info.put("ages", testAges.toString());

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return new Profile(info);
	}

	public Profile readResultsFromXML(String path) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(path);
			Element root = document.getRootElement();
			Iterator rootElements = root.elementIterator();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public Profile readProfileFromFile(String fileName) {
//		HashMap<String, String> info = new HashMap<String, String>();
//		Set<GrammarResult> grammarResults = new HashSet<GrammarResult>();
//	    BufferedReader reader = null;
//	    tempString = null;
//        try {
//            reader = new BufferedReader(new FileReader(profilePath + fileName));
//            tempString = reader.readLine();
//            while (tempString != null) {
//        	    switch (tempString) {
//				case "@info":
//					info = readInfo(reader);
//					break;
//
//				default:
//					tempString = reader.readLine();
//					break;
//				}
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		return new Profile(info, fileName);
//	}
//
//	private HashMap<String, String> readInfo(BufferedReader reader) {
//		HashMap<String, String> info = new HashMap<String, String>();
//		try {
//			while ((tempString = reader.readLine()) != null) {
//				if (tempString.contains("@"))
//					break;
//	    	    String[] infoLine = tempString.split(",");
//	    	    info.put(infoLine[0], infoLine[1]);
//	        }
//		} catch (IOException e) {
//            e.printStackTrace();
//        }
//		return info;
//	}

}
