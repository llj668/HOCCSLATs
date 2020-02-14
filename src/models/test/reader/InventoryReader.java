package models.test.reader;

import application.PropertyManager;
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

}
