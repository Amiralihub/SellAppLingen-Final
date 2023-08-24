package com.example.sellapplingen;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SettingFragment extends Fragment {

    private EditText editStoreName, editOwner, editStreet, editHouseNumber, editZip, editTelephone, editEmail;
    private Button saveData;
    private String token;

    private boolean isEditMode = false; // Neue Variable für den Bearbeitungsmodus

    private DataEditWatcher dataEditWatcher;


    @SuppressLint("MissingInflatedId")
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
        saveData = view.findViewById(R.id.saveData);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        dataEditWatcher = new DataEditWatcher();
        dataEditWatcher.watch(editStoreName);
        dataEditWatcher.watch(editOwner);
        dataEditWatcher.watch(editStreet);
        dataEditWatcher.watch(editHouseNumber);
        dataEditWatcher.watch(editZip);
        dataEditWatcher.watch(editTelephone);
        dataEditWatcher.watch(editEmail);

        // Lese den Token aus den SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        new Thread(this::getSettings).start();

        Button editDataButton = view.findViewById(R.id.editDataButton);
        editDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = true; // Aktivieren Sie den Bearbeitungsmodus
                enableEditMode(true); // Aktivieren Sie die Bearbeitung der Daten
            }
        });

        saveData.setOnClickListener(v -> showConfirmationDialog());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }

        });

        setupTextWatchers();

        return view;
    }



    private class DataEditWatcher implements TextWatcher {

        private final Set<EditText> watchedFields = new HashSet<>();

        public void watch(EditText editText) {
            watchedFields.add(editText);
            editText.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableSaveButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {}

        public boolean anyFieldEdited() {
            for (EditText editText : watchedFields) {
                if (!editText.getText().toString().equals(editText.getTag())) {
                    return true;
                }
            }
            return false;
        }
    }


    private void setupTextWatchers() {
        editStoreName.addTextChangedListener(createTextWatcher());
        editOwner.addTextChangedListener(createTextWatcher());
        editStreet.addTextChangedListener(createTextWatcher());
        editHouseNumber.addTextChangedListener(createTextWatcher());
        editZip.addTextChangedListener(createTextWatcher());
        editTelephone.addTextChangedListener(createTextWatcher());
        editEmail.addTextChangedListener(createTextWatcher());
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableSaveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }


    private void enableEditMode(boolean enable) {
        editStoreName.setEnabled(enable);
        editOwner.setEnabled(enable);
        editStreet.setEnabled(enable);
        editHouseNumber.setEnabled(enable);
        editZip.setEnabled(enable);
        editTelephone.setEnabled(enable);
        editEmail.setEnabled(enable);
    }


    private void enableSaveButton() {
        if (isEditMode) {
            saveData.setEnabled(dataEditWatcher.anyFieldEdited());
        } else {
            saveData.setEnabled(false);

        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Abmelden");
        builder.setMessage("Sind Sie sicher, dass Sie sich abmelden möchten?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the log out method here
                performLogout();
            }
        });

        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performLogout() {
        // Clear session data (token) from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();

        // Start LoginActivity using an Intent
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }



    public enum ZipCode {
        ZIP_40808("49808"),
        ZIP_49809("49809"),
        ZIP_49811("49811");

        private final String value;

        ZipCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(\\w+\\.)(com|de)$";
        return email.matches(emailRegex);
    }

    private boolean isInputValid(String storeName, String owner, String street, String houseNumber, String zip, String telephone, String email) {
        boolean isValid = true;

        if (storeName.trim().isEmpty() || containsEmojis(storeName)) {
            isValid = false;
            editStoreName.setError("Bitte geben Sie einen gültigen Geschäftsnamen ein");
        }

        if (owner.trim().isEmpty() || containsEmojis(owner)) {
            isValid = false;
            editOwner.setError("Bitte geben Sie einen gültigen Eigentümer ein");
        }

        if (street.trim().isEmpty() || containsEmojis(street)) {
            isValid = false;
            editStreet.setError("Bitte geben Sie eine gültige Straße ein");
        }

        if (houseNumber.trim().isEmpty() || containsEmojis(houseNumber)) {
            isValid = false;
            editHouseNumber.setError("Bitte geben Sie eine gültige Hausnummer ein");
        }

        if (!isValidZipCode(zip)) {
            isValid = false;
            editZip.setError("Bitte geben Sie eine gültige PLZ ein (49808,49809,49811)");
        }

        if (telephone.trim().isEmpty() || containsEmojis(telephone)) {
            isValid = false;
            editTelephone.setError("Bitte geben Sie eine gültige Telefonnummer ein");
        }

        if (!isValidEmail(email)) {
            isValid = false;
            editEmail.setError("Bitte geben Sie eine gültige E-Mail-Adresse ein");
        }

        return isValid;
    }

    private boolean isValidZipCode(String zip) {
        for (ZipCode validZip : ZipCode.values()) {
            if (validZip.getValue().equals(zip)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsEmojis(String text) {
        String regex = "[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}\\x{1F680}-\\x{1F6FF}\\x{1F700}-\\x{1F77F}\\x{1F780}-\\x{1F7FF}\\x{1F800}-\\x{1F8FF}\\x{1F900}-\\x{1F9FF}\\x{1FA00}-\\x{1FA6F}\\x{1FA70}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{2300}\\x{2B50}\\x{1F004}\\x{2B05}\\x{2744}\\x{26A1}\\x{1F004}\\x{1F0CF}\\x{1F18E}\\x{1F201}\\x{1F202}\\x{1F21A}\\x{1F22F}\\x{1F232}\\x{1F233}\\x{1F234}\\x{1F235}\\x{1F236}\\x{1F237}\\x{1F238}\\x{1F239}\\x{1F23A}\\x{1F250}\\x{1F251}\\x{1F300}\\x{1F301}\\x{1F302}\\x{1F303}\\x{1F304}\\x{1F305}\\x{1F306}\\x{1F307}\\x{1F308}\\x{1F309}\\x{1F30A}\\x{1F30B}\\x{1F30C}\\x{1F30D}\\x{1F30E}\\x{1F30F}\\x{1F310}\\x{1F311}\\x{1F312}\\x{1F313}\\x{1F314}\\x{1F315}\\x{1F316}\\x{1F317}\\x{1F318}\\x{1F319}\\x{1F31A}\\x{1F31B}\\x{1F31C}\\x{1F31D}\\x{1F31E}\\x{1F31F}\\x{1F320}\\x{1F321}\\x{1F322}\\x{1F323}\\x{1F324}\\x{1F325}\\x{1F326}\\x{1F327}\\x{1F328}\\x{1F329}\\x{1F32A}\\x{1F32B}\\x{1F32C}\\x{1F32D}\\x{1F32E}\\x{1F32F}\\x{1F330}\\x{1F331}\\x{1F332}\\x{1F333}\\x{1F334}\\x{1F335}\\x{1F336}\\x{1F337}\\x{1F338}\\x{1F339}\\x{1F33A}\\x{1F33B}\\x{1F33C}\\x{1F33D}\\x{1F33E}\\x{1F33F}\\x{1F340}\\x{1F341}\\x{1F342}\\x{1F343}\\x{1F344}\\x{1F345}\\x{1F346}\\x{1F347}\\x{1F348}\\x{1F349}\\x{1F34A}\\x{1F34B}\\x{1F34C}\\x{1F34D}\\x{1F34E}\\x{1F34F}\\x{1F350}\\x{1F351}\\x{1F352}\\x{1F353}\\x{1F354}\\x{1F355}\\x{1F356}\\x{1F357}\\x{1F358}\\x{1F359}\\x{1F35A}\\x{1F35B}\\x{1F35C}\\x{1F35D}\\x{1F35E}\\x{1F35F}\\x{1F360}\\x{1F361}\\x{1F362}\\x{1F363}\\x{1F364}\\x{1F365}\\x{1F366}\\x{1F367}\\x{1F368}\\x{1F369}\\x{1F36A}\\x{1F36B}\\x{1F36C}\\x{1F36D}\\x{1F36E}\\x{1F36F}\\x{1F370}\\x{1F371}\\x{1F372}\\x{1F373}\\x{1F374}\\x{1F375}\\x{1F376}\\x{1F377}\\x{1F378}\\x{1F379}\\x{1F37A}\\x{1F37B}\\x{1F37C}\\x{1F37D}\\x{1F37E}\\x{1F37F}\\x{1F380}\\x{1F381}\\x{1F382}\\x{1F383}\\x{1F384}\\x{1F385}\\x{1F386}\\x{1F387}\\x{1F388}\\x{1F389}\\x{1F38A}\\x{1F38B}\\x{1F38C}]";

        return text.matches(regex);
    }

    private void showConfirmationDialog() {
        if (isInputValid(
                editStoreName.getText().toString(),
                editOwner.getText().toString(),
                editStreet.getText().toString(),
                editHouseNumber.getText().toString(),
                editZip.getText().toString(),
                editTelephone.getText().toString(),
                editEmail.getText().toString()
        )) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Daten speichern");
            builder.setMessage("Sind Sie sicher, dass Sie die Daten speichern möchten?");

            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<Pair<SettingParameter, String>> updatedSettings = new ArrayList<>();
                            if (!editStoreName.getText().toString().isEmpty()) {
                                updatedSettings.add(new Pair<>(SettingParameter.storeName, editStoreName.getText().toString()));
                            }
                            if (!editOwner.getText().toString().isEmpty()) {
                                updatedSettings.add(new Pair<>(SettingParameter.owner, editOwner.getText().toString()));
                            }
                            if (!editTelephone.getText().toString().isEmpty()) {
                                updatedSettings.add(new Pair<>(SettingParameter.telephone, editTelephone.getText().toString()));
                            }
                            if (!editEmail.getText().toString().isEmpty()) {
                                updatedSettings.add(new Pair<>(SettingParameter.email, editEmail.getText().toString()));
                            }

                            String updatedStreet = editStreet.getText().toString();
                            String updatedHouseNumber = editHouseNumber.getText().toString();
                            String updatedZip = editZip.getText().toString();

                            if (updatedSettings.isEmpty() && updatedStreet.isEmpty() && updatedHouseNumber.isEmpty() && updatedZip.isEmpty()) {
                                dialog.dismiss();
                                return;
                            }

                            boolean allUpdatesSuccessful = true;
                            for (Pair<SettingParameter, String> setting : updatedSettings) {
                                boolean settingUpdated;
                                if (!setSettings(setting.second, setting.first)) {
                                    settingUpdated = false;
                                } else {
                                    settingUpdated = true;
                                }
                                if (!settingUpdated) {
                                    allUpdatesSuccessful = false;
                                    break;
                                }
                            }

                            if (allUpdatesSuccessful) {
                                boolean addressUpdated = setAddress(updatedStreet, updatedHouseNumber, updatedZip);
                                if (addressUpdated) {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveData.setEnabled(false);
                                            enableEditMode(false);
                                            showSuccessPopup();
                                        }
                                    });
                                } else {
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showErrorPopup();
                                        }
                                    });
                                }
                            } else {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showErrorPopup();
                                    }
                                });
                            }

                            dialog.dismiss();
                        }
                    }).start();
                }
            }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(requireContext(), "Bitte überprüfen Sie die Eingabe", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSavedToken() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    private void getSettings() {
        if (getSavedToken() == null) {
            Log.d("Settings", "Kein Token");
            return;
        }

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", token);

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

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String storeName = jsonResponse.getString("storeName");
                            String owner = jsonResponse.getString("owner");
                            String street = jsonResponse.getJSONObject("address").getString("street");
                            String houseNumber = jsonResponse.getJSONObject("address").getString("houseNumber");
                            String zip = jsonResponse.getJSONObject("address").getString("zip");
                            String telephone = jsonResponse.getString("telephone");
                            String email = jsonResponse.getString("email");

                            // Update UI components with the retrieved data
                            editStoreName.setText(storeName);
                            editOwner.setText(owner);
                            editStreet.setText(street);
                            editHouseNumber.setText(houseNumber);
                            editZip.setText(zip);
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

    public boolean setSettings(String value, SettingParameter parameter) {
        if (getSavedToken() == null) {
            Log.d("Settings", "Kein Token");
            return false;
        }

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", token);
            jsonParam.put("parameter", parameter.toString());
            jsonParam.put("value", value);

            URL url = new URL("http://131.173.65.77:8080/api/setSettings");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            conn.disconnect();

            return responseCode == 200;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean setAddress(String street, String houseNumber, String zip) {
        if (getSavedToken() == null) {
            Log.d("Settings", "Kein Token");
            return false;
        }

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", token);

            JSONObject addressJson = new JSONObject();
            addressJson.put("street", street);
            addressJson.put("houseNumber", houseNumber);
            addressJson.put("zip", zip);

            jsonParam.put("address", addressJson);

            URL url = new URL("http://131.173.65.77:8080/api/setAddress");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            conn.disconnect();

            return responseCode == 200;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showSuccessPopup() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(requireContext(), "Daten erfolgreich an den Server gesendet.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showErrorPopup() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireContext(), "Keine Verbindung zum Server.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}