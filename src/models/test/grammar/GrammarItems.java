package models.test.grammar;

import java.util.*;

public class GrammarItems {
    public static Map<String, List<String>> structureList = new HashMap<String, List<String>>(){{
        put("SV", Arrays.asList("s", "v"));
        put("aux", Collections.singletonList("aux"));
        put("pron p", Collections.singletonList("pron p"));
        put("ing-v", Collections.singletonList("ing-v"));
    }};
}
