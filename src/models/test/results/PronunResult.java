package models.test.results;

import application.PropertyManager;
import models.profiles.Age;
import models.test.pronun.ErrorPattern;
import models.test.pronun.Inventory;
import models.test.pronun.PronunItems;
import models.test.pronun.Syllable;
import models.test.reader.InventoryReader;
import views.items.PronunResultItem;
import views.items.ResultItem;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PronunResult extends BaseResult {
    final static int threshold = 1;
    public double pcc;
    public List<Syllable> syllables;
    public List<String> presentConsonants;
    public List<String> absentConsonants;
    public Map<ErrorPattern, Integer> errors;
    public Map<String, String> comparedMap75;
    public Map<String, String> comparedMap90;
    public Map<ErrorPattern, String> comparedMapErrorPattern;

    public PronunResult(Age testAge) {
        super(testAge);
        syllables = new LinkedList<>();
        presentConsonants = new LinkedList<>();
        absentConsonants = new LinkedList<>();
        errors = new HashMap<>();
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
        this.comparedMapErrorPattern = compareToErrorPattern();
    }

    private List<String> getAbsentFromPresent() {
        List<String> absent = new LinkedList<>();
        for (String consonant : PronunItems.consonants) {
            if (!presentConsonants.contains(consonant))
                absent.add(consonant);
        }
        for (String consonant : PronunItems.double_consonants) {
            if (!presentConsonants.contains(consonant))
                absent.add(consonant);
        }
        return absent;
    }

    private Map<ErrorPattern, Integer> getErrorsFromSyllable() {
        Map<ErrorPattern, Integer> patterns = new LinkedHashMap<>();
        int index = 0;
        for (Syllable syllable : syllables) {
            if (index == 0) {
                patterns.putAll(syllable.getErrorPatterns());
                index++;
            } else {
                for (Map.Entry<ErrorPattern, Integer> entry : syllable.getErrorPatterns().entrySet()) {
                    int newValue = patterns.get(entry.getKey()) + entry.getValue();
                    patterns.put(entry.getKey(), newValue);
                }
            }
        }
        return patterns;
    }

    private List<String> getPresentsFromSyllable() {
        List<String> phonemes = new LinkedList<>();
        List<String> presents = new LinkedList<>();
        for (Syllable syllable : syllables) {
            phonemes.addAll(syllable.getPhonemesCorrect());
        }
        for (String phoneme: phonemes.stream().distinct().collect(Collectors.toList())) {
            if (PronunItems.double_consonants.contains(phoneme) || PronunItems.consonants.contains(phoneme))
                presents.add(phoneme);
        }
        return presents;
    }

    public String getPresentConsonantsAsString() {
        return String.join(",", presentConsonants);
    }

    @Override
    public void conclude() {
        testTime = new Date();
        this.presentConsonants = getPresentsFromSyllable();
        this.absentConsonants = getAbsentFromPresent();
        this.errors = getErrorsFromSyllable();
        this.comparedMap75 = compareToInventory("75");
        this.comparedMap90 = compareToInventory("90");
        this.comparedMapErrorPattern = compareToErrorPattern();

        DecimalFormat df = new DecimalFormat("#.00");
        this.pcc = 0;
        for (Syllable syllable : syllables) {
            pcc += syllable.getPcc();
        }
        this.pcc = Double.parseDouble(df.format(pcc / syllables.size()));
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

    private Map<ErrorPattern, String> compareToErrorPattern() {
        Map<ErrorPattern, String> comparedMap = new HashMap<>();
        Map<ErrorPattern, Boolean> inventoryMap = InventoryReader.readErrorPatternInventory(PropertyManager.getResourceProperty("inventory_error"), testAge);

        for (Map.Entry<ErrorPattern, Integer> error : errors.entrySet()) {
            for (Map.Entry<ErrorPattern, Boolean> inventory : inventoryMap.entrySet()) {
                if (error.getKey() == inventory.getKey()) {
                    if (error.getValue() < threshold) {
                        if (inventory.getValue()) {
                            comparedMap.put(error.getKey(), "green");
                        } else {
                            comparedMap.put(error.getKey(), "normal");
                        }
                    } else {
                        if (inventory.getValue()) {
                            comparedMap.put(error.getKey(), "normal");
                        } else {
                            comparedMap.put(error.getKey(), "red");
                        }
                    }
                }
            }
        }
        return comparedMap;
    }

    @Override
    public ResultItem toResultItem() {
        return new PronunResultItem(this);
    }
}
