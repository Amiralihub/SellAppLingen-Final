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


    public static Settings getSettings(String token) {

        CompletableFuture<String> getSettingsFuture = NetworkManager.sendGetRequest(NetworkManager.APIEndpoints.SETTINGS.getUrl());
        String jsonResponse = getSettingsFuture.join();
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