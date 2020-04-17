package application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * Localization
 */
public class LocalStrings {
    final static String root = "src/" ;
    final static String cnFile = "local_cn.properties";
    static Properties localString;

    public static String GS_comboBoxQuestion = "";
    public static String GS_comboBoxS1 = "";
    public static String GS_comboBoxS2 = "";
    public static String GS_comboBoxS3 = "";
    public static String GS_comboBoxS4 = "";
    public static String GS_comboBoxUnscored = "";
    public static String GS_tableQuestion_id = "";
    public static String GS_tableQuestion_stage = "";
    public static String GS_tableQuestion_response = "";
    public static String GS_tableStage_structure = "";
    public static String GS_tableStage_score = "";
    public static String GS_tableStage_eva = "";
    public static String GS_scoreTexts = "";

    public LocalStrings() {
        localString = new Properties();
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/local_cn.properties");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            localString.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadStrings();
    }

    private void loadStrings() {
        GS_comboBoxQuestion = localString.getProperty("GS_comboBoxQuestion");
        GS_comboBoxS1 = localString.getProperty("GS_comboBoxS1");
        GS_comboBoxS2 = localString.getProperty("GS_comboBoxS2");
        GS_comboBoxS3 = localString.getProperty("GS_comboBoxS3");
        GS_comboBoxS4 = localString.getProperty("GS_comboBoxS4");
        GS_comboBoxUnscored = localString.getProperty("GS_comboBoxUnscored");
        GS_tableQuestion_id = localString.getProperty("GS_tableQuestion_id");
        GS_tableQuestion_stage = localString.getProperty("GS_tableQuestion_stage");
        GS_tableQuestion_response = localString.getProperty("GS_tableQuestion_response");
        GS_tableStage_structure = localString.getProperty("GS_tableStage_structure");
        GS_tableStage_score = localString.getProperty("GS_tableStage_score");
        GS_tableStage_eva = localString.getProperty("GS_tableStage_eva");
        GS_scoreTexts = localString.getProperty("GS_scoreTexts");
    }
}
