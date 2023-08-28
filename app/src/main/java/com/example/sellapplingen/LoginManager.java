package com.example.sellapplingen;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class LoginManager {
    public static final String PREF_NAME = "LoginPrefs";
    public String username;
    public String password;

    public Boolean showFailMSG = false;

    public LoginManager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private static LoginManager instance;
    private static Context context;

    // Privater Konstruktor, um die Instanz nur einmal zu erstellen
    private LoginManager(Context context) {
        this.context = context;
    }

    // Statische Methode zum Erhalten der einzigen Instanz des LoginManagers
    public static LoginManager getInstance(Context context) {
        if (instance == null) {
            instance = new LoginManager(context.getApplicationContext());
        }
        return instance;
    }



    //jsonwebtoken muss immer mit geschickt werden, z.b bei settings usw

    public void saveLoginDetails(String username, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    public static void saveToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
        String savedToken = sharedPreferences.getString("token", null);
        if (token.equals(savedToken)) {
            Log.d("LoginManager", "Token was successfully saved: " + savedToken);
        } else {
            Log.e("LoginManager", "Failed to save the token");
        }
    }

    public static String loadToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains("token");
    }



    // Beispiel-Implementierung für die Überprüfung der Login-Daten mit dem Server


    public boolean sendPost(LogInData loginData) {

        showFailMSG = false;
        String token = null;
        boolean success = false;

        CompletableFuture<String> loginFuture = NetworkManager.sendPostRequest(NetworkManager.APIEndpoints.LOGIN.getUrl(),loginData);

        String response = loginFuture.join();
        JSONObject responseJson = null;
        try {
            responseJson = new JSONObject(response);
        } catch (JSONException e) {
            showFailMSG = true;
            throw new RuntimeException(e);
        }
        try {
            token = responseJson.getString("token");
            Log.d("LoginManager", "Received token: " + token);

            if (token != null) {
                LoginManager.saveToken(token);
                success = true;
            }else {
                showFailMSG = true;
                success = false;
            }

        } catch (JSONException e) {
            showFailMSG = true;
            throw new RuntimeException(e);
        }
        if(showFailMSG){
            Toast.makeText(context, "Falsche Login Daten", Toast.LENGTH_SHORT).show();
        }

        return success;
    }

}