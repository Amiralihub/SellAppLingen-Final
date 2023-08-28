package com.example.sellapplingen;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
public class NetworkManager {

    private static final Gson gson = new Gson();

    public enum APIEndpoints {
        LOGIN("http://131.173.65.77:8080/auth/login"),
        ORDER("http://131.173.65.77:8080/api/order"),
        PLACED_ORDERS("http://131.173.65.77:8080/api/allOrders"),
        SETTINGS("http://131.173.65.77:8080/api/settings"),
        SET_ADDRESS("http://131.173.65.77:8080/api/setAddress"),
        SET_PASSWORD("http://131.173.65.77:8080/auth/updatePassword");

        private final String url;

        APIEndpoints(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public static CompletableFuture<String> sendPostRequest(String apiUrl, Object dataObject) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + LoginManager.loadToken());
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                String jsonString = gson.toJson(dataObject);
                System.out.println("json to send: "+jsonString);
                byte[] postData = jsonString.getBytes(StandardCharsets.UTF_8);

                try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                    os.write(postData);
                    os.flush();
                }

                StringBuilder responseBuilder = new StringBuilder();
                try (InputStream inputStream = conn.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }

                conn.disconnect();

                return responseBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}
