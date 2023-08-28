package com.example.sellapplingen;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;


public class SettingManager {

    private static final String API_URL = "http://131.173.65.77:8080/api/settings";

    private static final Gson gson = new Gson();

    public class Parameter {
        public static final String STORE_NAME = "storeName";
        public static final String OWNER = "owner";
        public static final String TELEPHONE = "telephone";
        public static final String EMAIL = "email";
        public static final String LOGO = "logo";
        public static final String BACKGROUND_IMAGE = "backgroundImage";
        public static final String PASSWORD = "password";
    }


    public static Settings getSettings(String token) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;

        try {
            URL url = new URL(API_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonResponse = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error appropriately
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Settings settings = gson.fromJson(jsonResponse, Settings.class);
            return settings;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean setSettings(String parameter, String value) {

        boolean success = false;
        String token = null;

        SetSettings settingsObject = new SetSettings(parameter, value);
        Gson gson = new Gson();
        CompletableFuture<String> setSettingFuture = NetworkManager.sendPostRequest(NetworkManager.APIEndpoints.SETTINGS.getUrl(), settingsObject);
        String response = setSettingFuture.join();
        JSONObject responseJson = null;
        try {
            responseJson = new JSONObject(response);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            token = responseJson.getString("token");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        if (token != null) {
            LoginManager.saveToken(token);
            success = true;
        }

        return success;

    }


    public static boolean setAddress(SetAddress address) {

        boolean success = false;

        CompletableFuture<String> setSettingFuture = NetworkManager.sendPostRequest(NetworkManager.APIEndpoints.SET_ADDRESS.getUrl(), address);
        String response = setSettingFuture.join();

        System.out.println("Server response" + response);

        return success;

    }
}