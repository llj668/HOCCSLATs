package models.test.grammar;

import java.util.*;

public class GrammarItems {
    public static Map<String, String> similarityMap = new HashMap<String, String>(){{
        put("ENDO", "高高的树");
        put("SVO", "妈妈去姥姥家");
        put("WhX", "哪里是森林");
        put("VO", "他踢足球");
        put("VCv", "兔子跑远了");
        put("VV", "妈妈出门买菜");
    }};

    public static Map<String, List<String>> structureList = new HashMap<String, List<String>>(){{
        put("ENDO", Collections.singletonList("endo"));
        put("SVO", Arrays.asList("s", "v", "o"));
        put("WhX", Arrays.asList("wh", "x"));
        put("VO", Arrays.asList("v", "o"));
        put("VCv", Arrays.asList("v", "cv"));
        put("VV", Collections.singletonList("vv"));
    }};
}
