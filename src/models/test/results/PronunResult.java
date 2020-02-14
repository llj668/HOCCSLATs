package models.test.results;

import application.PropertyManager;
import models.profiles.Age;
import models.test.pronun.ErrorPattern;
import models.test.pronun.Inventory;
import models.test.pronun.PronunItems;
import models.test.pronun.Syllable;
import models.test.reader.InventoryReader;
import views.items.GrammarResultItem;
import views.items.PronunResultItem;
import views.items.ResultItem;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PronunResult extends BaseResult {
    public double pcc;
    public List<Syllable> syllables;
    public List<String> presentConsonants;
    public List<String> absentConsonants;
    public List<ErrorPattern> errors;
    public Map<String, String> comparedMap75;
    public Map<String, String> comparedMap90;

    public PronunResult(Age testAge) {
        super(testAge);
        syllables = new LinkedList<>();
        presentConsonants = new LinkedList<>();
        absentConsonants = new LinkedList<>();
        errors = new LinkedList<>();
    }

    public PronunResult(Age testAge, Date testTime, String pcc, List<Syllable> syllables, List<String> presentConsonants) {
        super(testAge);
        this.testTime = testTime;
        this.pcc = Double.parseDouble(pcc);
        this.syllables = syllables;
        this.presentConsonants = presentConsonants;
        this.absentConsonants = getAbsentFromPresent();
        this.errors = getErrorsFromSyllable();
        this.comparedMap75 = compareToInventory("75");
        this.comparedMap90 = compareToInventory("90");
    }

    private List<String> getAbsentFromPresent() {
        List<String> absent = new LinkedList<>();
        for (String consonant : PronunItems.consonants) {
            if (!presentConsonants.contains(consonant))
                absent.add(consonant);
        }
        return absent;
    }

    private List<ErrorPattern> getErrorsFromSyllable() {
        List<ErrorPattern> errors = new LinkedList<>();
        for (Syllable syllable : syllables) {
            errors.addAll(syllable.getErrorPatterns());
        }
        return errors.stream().distinct().collect(Collectors.toList());
    }

    private List<String> getPresentsFromSyllable() {
        List<String> presents = new LinkedList<>();
        for (Syllable syllable : syllables) {
            presents.addAll(syllable.getConsonantsCorrect());
        }
        return presents.stream().distinct().collect(Collectors.toList());
    }

    public String getPresentConsonantsAsString() {
        return String.join(",", presentConsonants);
    }

    @Override
    public void conclude() {
        testTime = new Date();
        this.pcc = 0;
        this.presentConsonants = getPresentsFromSyllable();
        this.absentConsonants = getAbsentFromPresent();
        this.errors = getErrorsFromSyllable();
        this.comparedMap75 = compareToInventory("75");
        this.comparedMap90 = compareToInventory("90");
    }

    private Map<String, String> compareToInventory(String type) {
        Map<String, String> comparedMap = new HashMap<>();
        List<Inventory> inventoryList = new LinkedList<>();
        if (type.equalsIgnoreCase("75")) {
            inventoryList = InventoryReader.readInventoryFromXML(PropertyManager.getResourceProperty("inventory_75pc"));
        } else if (type.equalsIgnoreCase("90")) {
            inventoryList = InventoryReader.readInventoryFromXML(PropertyManager.getResourceProperty("inventory_90pc"));
        }

        for (Inventory inventory : inventoryList) {
            if (testAge.isInAgePeriod(inventory.getAgePeriod())) {
                System.out.println("compare with age period: " + inventory.getAgePeriod());
                comparedMap = inventory.compare(presentConsonants, absentConsonants);
            }
        }
        return comparedMap;
    }

    @Override
    public ResultItem toResultItem() {
        return new PronunResultItem(this);
    }
}
