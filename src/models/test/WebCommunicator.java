package models.test;

import application.PropertyManager;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import models.services.WebSpeechToText;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class WebCommunicator {
    private static final String hostUrl = "https://iat-api.xfyun.cn/v2/iat"; //中英文，http url 不支持解析 ws/wss schema
    private static final String apiKey = "52a68a9b3af53d7e6a6421f6c744e78b"; //在控制台-我的应用-语音听写（流式版）获取
    private static final String apiSecret = "4a804ae8812745e99645f02f877d6395"; //在控制台-我的应用-语音听写（流式版）获取
    private static final String tempSoundFile = PropertyManager.getResourceProperty("temp_sound_path");
    private static final String tempFilePath = PropertyManager.getResourceProperty("temp_file_path");

    public static void speechToText() {
        try {
            // 构建鉴权url
            String authUrl = WebSpeechToText.getAuthUrl(hostUrl, apiKey, apiSecret);
            OkHttpClient client = new OkHttpClient.Builder().build();
            //将url中的 schema http://和https://分别替换为ws:// 和 wss://
            String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
            //System.out.println(url);
            Request request = new Request.Builder().url(url).build();
            // System.out.println(client.newCall(request).execute());
            //System.out.println("url===>" + url);
            WebSocket webSocket = client.newWebSocket(request, new WebSpeechToText(tempSoundFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
