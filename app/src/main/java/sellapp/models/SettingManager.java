package sellapp.models;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.CompletableFuture;

public class SettingManager {

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

    public static StoreDetails getSettings() {
        CompletableFuture<String> getSettingsFuture = NetworkManager.sendGetRequest(NetworkManager.APIEndpoints.SETTINGS.getUrl());
        String jsonResponse = getSettingsFuture.join();
        try {
            return gson.fromJson(jsonResponse, StoreDetails.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean setSettings(String parameter, String value) {
        try {
            String token;

            SetSetting settingsObject = new SetSetting(parameter, value);
            CompletableFuture<String> setSettingFuture = NetworkManager.sendPostRequest(NetworkManager.APIEndpoints.SETTINGS.getUrl(), settingsObject);
            String response = setSettingFuture.join();
            JSONObject responseJson = new JSONObject(response);
            token = responseJson.getString("token");

            LogInData.saveToken(token);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setAddress(SetAddress address) {
        try {
            CompletableFuture<String> setSettingFuture = NetworkManager.sendPostRequest(NetworkManager.APIEndpoints.SET_ADDRESS.getUrl(), address);
            String response = setSettingFuture.join();

            System.out.println("Server response: " + response);

            return response != null && response.equals("Address updated");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
