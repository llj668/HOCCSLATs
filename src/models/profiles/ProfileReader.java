package models.profiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import models.test.results.GrammarResult;
import models.test.results.GrammarStructure;

public class ProfileReader {
	private String profilePath;
	private String tempString;
	
	public ProfileReader(String path) {
		profilePath = path;
	}

	public Profile readProfileFromFile(String fileName) {
		HashMap<String, String> info = new HashMap<String, String>();
		Set<GrammarResult> grammarResults = new HashSet<GrammarResult>();
	    BufferedReader reader = null;
	    tempString = null;
        try {
            reader = new BufferedReader(new FileReader(profilePath + fileName));
            tempString = reader.readLine();
            while (tempString != null) {
        	    switch (tempString) {
				case "@info":
					info = readInfo(reader);
					break;

				default:
					tempString = reader.readLine();
					break;
				}
            }	    
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		return new Profile(info, fileName);
	}
	
	private HashMap<String, String> readInfo(BufferedReader reader) {
		HashMap<String, String> info = new HashMap<String, String>();
		try {
			while ((tempString = reader.readLine()) != null) {
				if (tempString.contains("@"))
					break;
	    	    String[] infoLine = tempString.split(",");
	    	    info.put(infoLine[0], infoLine[1]);
	        }
		} catch (IOException e) {
            e.printStackTrace();
        } 
		return info;
	}

}
