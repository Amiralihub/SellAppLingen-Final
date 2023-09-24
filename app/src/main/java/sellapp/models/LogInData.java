package sellapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class LogInData
    {
    public static final String PREF_NAME = "LoginPrefs";
    public String username;
    private String password;
    public Boolean showFailMSG = false;

    public LogInData(String username, String password)
        {
        this.username = username;
        this.password = password;
        }

    private static LogInData instance;
    private static Context context;

    private LogInData(Context context)
        {
        this.context = context;
        }


    public static LogInData getInstance(Context context)
        {
        if (instance == null)
            {
            instance = new LogInData(context.getApplicationContext());
            }
        return instance;
        }


    public void saveLoginDetails(String username, String password)
        {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
        }

    public static void saveToken(String token)
        {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
        String savedToken = sharedPreferences.getString("token", null);
        if (token.equals(savedToken))
            {
            Log.d("LoginManager", "Token was successfully saved: " + savedToken);
            }
        else
            {
            Log.e("LoginManager", "Failed to save the token");
            }
        }

    public static String loadToken()
        {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
        }

    public boolean isLoggedIn()
        {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains("token");
        }



    public boolean sendPost(LogInData loginData)
        {
        showFailMSG = false;
        String token = null;
        boolean success = false;
        CompletableFuture<String> loginFuture = NetworkManager.sendPostRequest(
                NetworkManager.APIEndpoints.LOGIN.getUrl(), loginData);
        String response = loginFuture.join();

        if (response != null)
            {
            try
                {
                JSONObject responseJson = new JSONObject(response);
                token = responseJson.optString("token");
                Log.d("LoginManager", "Received token: " + token);

                if (token != null)
                    {
                    LogInData.saveToken(token);
                    success = true;
                    }
                else
                    {
                    showFailMSG = true;
                    success = false;
                    }
                } catch (JSONException e)
                {
                showFailMSG = true;
                e.printStackTrace();
                }
            }
        else
            {
            showFailMSG = true;
            }

        return success;
        }


    }