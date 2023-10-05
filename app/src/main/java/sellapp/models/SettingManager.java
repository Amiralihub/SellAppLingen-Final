package sellapp.models;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class SettingManager
    {

    private static final Gson gson = new Gson();

    /**
     * This class contains constants for parameters that can be used in various contexts.
     */
    public class Parameter
        {

        /**
         * The name of the store.
         */
        public static final String STORE_NAME = "storeName";

        /**
         * The name of the store owner.
         */
        public static final String OWNER = "owner";

        /**
         * The telephone number of the store.
         */
        public static final String TELEPHONE = "telephone";

        /**
         * The email address of the store.
         */
        public static final String EMAIL = "email";
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static StoreDetails getSettings()
        {
        CompletableFuture<String> getSettingsFuture = NetworkManager.sendGetRequest(
                NetworkManager.APIEndpoints.SETTINGS.getUrl());
        String jsonResponse = getSettingsFuture.join();

        if (jsonResponse != null)
            {
            try
                {
                return gson.fromJson(jsonResponse, StoreDetails.class);
                } catch (JsonSyntaxException e)
                {
                e.printStackTrace();
                return null;
                }
            }
        else
            {

            return null;
            }
        }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Boolean setSettings(String parameter, String value)
        {
        try
            {
            String token;

            SetSetting settingsObject = new SetSetting(parameter, value);
            CompletableFuture<String> setSettingFuture = NetworkManager.sendPostRequest(
                    NetworkManager.APIEndpoints.SETTINGS.getUrl(), settingsObject);
            String response = setSettingFuture.join();
            JSONObject responseJson = new JSONObject(response);
            token = responseJson.getString("token");

            LogInData.saveToken(token);
            return true;
            } catch (JSONException e)
            {
            e.printStackTrace();
            return false;
            }
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean setAddress(SetAddress address)
        {
        try
            {
            CompletableFuture<String> setSettingFuture = NetworkManager.sendPostRequest(
                    NetworkManager.APIEndpoints.SET_ADDRESS.getUrl(), address);
            String response = setSettingFuture.join();

            System.out.println("Server response: " + response);

            return response != null && response.equals("Address updated");
            } catch (Exception e)
            {
            e.printStackTrace();
            return false;
            }
        }
    }