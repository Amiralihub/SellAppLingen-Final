package com.example.sellapplingen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

import com.example.sellapplingen.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingFragment extends Fragment {

    private EditText editStoreName, editOwner, editStreet, editHouseNumber, editZip, editTelephone, editEmail;
    private Button btnSubmit;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        editStoreName = view.findViewById(R.id.editStoreName);
        editOwner = view.findViewById(R.id.editOwner);
        editStreet = view.findViewById(R.id.editStreet);
        editHouseNumber = view.findViewById(R.id.editHouseNumber);
        editZip = view.findViewById(R.id.editZip);
        editTelephone = view.findViewById(R.id.editTelephone);
        editEmail = view.findViewById(R.id.editEmail);


        // Lese den Token aus den SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSettings();  // Rufe die Methode zum Abrufen der Einstellungen vom Server auf
                    }
                }).start();
            }
        });

        return view;
    }

    private void sendDataToServer() {
        final String storeName = editStoreName.getText().toString();
        final String owner = editOwner.getText().toString();
        final String street = editStreet.getText().toString();
        final String houseNumber = editHouseNumber.getText().toString();
        final String zip = editZip.getText().toString();
        final String telephone = editTelephone.getText().toString();
        final String email = editEmail.getText().toString();

        // Hier kannst du den Code für das Senden der Daten zum Server einfügen
    }

    private String getSavedToken() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }



    private void getSettings() {
        String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdG9yZUlEIjozLCJzdG9yZU5hbWUiOiJUd2l0dGVyIiwib3duZXIiOiJFbG9uX3N1Y2tzIiwibG9nbyI6ImVsb24iLCJ0ZWxlcGhvbmUiOiIwOTg3NjQzMjEiLCJlbWFpbCI6InNkZnNkLnNkZnNkQHR3aXR0ZXIuY29tIiwiaWF0IjoxNjkxNjc1NjA5LCJzdWIiOiJhdXRoX3Rva2VuIn0._c2pnpuPlGCFE6ah5uosnhabaCQdRKCuoH13GSS4fRM";
        if (getSavedToken() == null) {
            Log.d("Settings", "Kein Token");
            return;
        }
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", testToken);

            URL url = new URL("http://131.173.65.77:8080/api/getSettings");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                final JSONObject jsonResponse = new JSONObject(response.toString());

                // Verarbeite die Antwort und setze die Daten in die EditText-Felder im UI-Thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String storeName = jsonResponse.getString("storeName");
                            String owner = jsonResponse.getString("owner");
                            String street = jsonResponse.getString("street");
                            String houseNumber = jsonResponse.getString("houseNumber");
                            //Integer zip = jsonResponse.getInt("zip");
                            String telephone = jsonResponse.getString("telephone");
                            String email = jsonResponse.getString("email");

                            editStoreName.setText(storeName);
                            editOwner.setText(owner);
                            editStreet.setText(street);
                            editHouseNumber.setText(houseNumber);
                            //editZip.setText(zip);
                            editTelephone.setText(telephone);
                            editEmail.setText(email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                Log.d("Settings", "Fehler beim Empfangen der Einstellungen. Statuscode: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSettings() {
        // Hier der Code zum Aktualisieren der Einstellungen auf dem Server
    }
}