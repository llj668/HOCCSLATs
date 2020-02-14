package models.test.pronun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private String agePeriod;
    private Map<String, Boolean> plosive;
    private Map<String, Boolean> nasal;
    private Map<String, Boolean> affricate;
    private Map<String, Boolean> fricative;
    private Map<String, Boolean> approxi;
    private Map<String, Boolean> l_approxi;

    public Inventory(String agePeriod) {
        this.agePeriod = agePeriod;
    }

    public Map<String, String> compare(List<String> presentConsonants, List<String> absentConsonants) {
        Map<String, String> compared = new HashMap<>();
        for (String consonant : presentConsonants) {
            Map<String, Boolean> category = this.getCategory(PronunItems.consonantType.get(consonant));
            if (category == null)
                continue;
            boolean inventoryBoolean = category.get(consonant);
            if (inventoryBoolean) {
                compared.put(consonant, "normal_present");
            } else {
                compared.put(consonant, "green");
            }
        }
        for (String consonant : absentConsonants) {
            Map<String, Boolean> category = this.getCategory(PronunItems.consonantType.get(consonant));
            if (category == null)
                continue;
            boolean inventoryBoolean = category.get(consonant);
            if (inventoryBoolean) {
                compared.put(consonant, "red");
            } else {
                compared.put(consonant, "normal_absent");
            }
        }
        return compared;
    }

    public void setCategory(Map<String, Boolean> category, String name) {
        switch (name) {
            case "plosive":
                this.plosive = category;
                break;
            case "nasal":
                this.nasal = category;
                break;
            case "affricate":
                this.affricate = category;
                break;
            case "fricative":
                this.fricative = category;
                break;
            case "approxi":
                this.approxi = category;
                break;
            case "l_approxi":
                this.l_approxi = category;
                break;
            default:
                break;
        }
    }

    public Map<String, Boolean> getCategory(String name) {
        if (name == null)
            return null;
        switch (name) {
            case "plosive":
                return this.plosive;
            case "nasal":
                return this.nasal;
            case "affricate":
                return this.affricate;
            case "fricative":
                return this.fricative;
            case "approxi":
                return this.approxi;
            case "l_approxi":
                return this.l_approxi;
            default:
                return null;
        }
    }

    public String getAgePeriod() {
        return this.agePeriod;
    }
}
