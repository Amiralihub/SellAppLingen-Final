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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


public class SettingFragment extends Fragment {

    private EditText editStoreName, editOwner, editStreet, editHouseNumber, editZip, editTelephone, editEmail;
    private Button saveData;
    private String token;

    private boolean isEditMode = false; // Neue Variable für den Bearbeitungsmodus
    private boolean isDataEdited = false; // Neue Variable für bearbeitete Daten

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
                enableEditMode(); // Aktivieren Sie die Bearbeitung der Daten
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
            isDataEdited = true;
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
                isDataEdited = true;
                enableSaveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }


    public void enableEditMode() {
        // Aktivieren Sie die Bearbeitung der Daten
        if (isEditMode) {
            editStoreName.setEnabled(true);
            editOwner.setEnabled(true);
            editStreet.setEnabled(true);
            editHouseNumber.setEnabled(true);
            editZip.setEnabled(true);
            editTelephone.setEnabled(true);
            editEmail.setEnabled(true);
        }

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

    private boolean isInputValid(String storeName, String owner, String street, String houseNumber, String zip, String telephone, String email) {
        boolean isValid = true;

        if (storeName.isEmpty()) {
            isValid = false;
            editStoreName.setError("Bitte geben Sie einen Geschäftsnamen ein");
        }

        if (owner.isEmpty()) {
            isValid = false;
            editOwner.setError("Bitte geben Sie den Eigentümer ein");
        }

        if (street.isEmpty()) {
            isValid = false;
            editStreet.setError("Bitte geben Sie eine Straße ein");
        }

        if (houseNumber.isEmpty()) {
            isValid = false;
            editHouseNumber.setError("Bitte geben Sie eine Hausnummer ein");
        }
        if (zip.isEmpty()) {
            isValid = false;
            editZip.setError("Bitte geben Sie einen Geschäftsnamen ein");
        }
        if (telephone.isEmpty()) {
            isValid = false;
            editTelephone.setError("Bitte geben Sie einen Geschäftsnamen ein");
        }
        if (email.isEmpty()) {
            isValid = false;
            editEmail.setError("Bitte geben Sie einen Geschäftsnamen ein");
        }

        return isValid;
    }


//TODO

/*


    private boolean isInputValid(String storeName, String owner, String street, String houseNumber, String zip, String telephone, String email) {
        boolean isValid = true;

        if (storeName.isEmpty()) {
            isValid = false;
            editStoreName.setError("Bitte geben Sie einen Geschäftsnamen ein");
        }

        if (owner.isEmpty()) {
            isValid = false;
            editOwner.setError("Bitte geben Sie den Eigentümer ein");
        }

        if (street.isEmpty()) {
            isValid = false;
            editStreet.setError("Bitte geben Sie eine Straße ein");
        }

        if (houseNumber.isEmpty()) {
            isValid = false;
            editHouseNumber.setError("Bitte geben Sie eine Hausnummer ein");
        }

        if (zip.isEmpty()) {
            isValid = false;
            editZip.setError("Bitte geben Sie eine Postleitzahl ein");
        }

        if (telephone.isEmpty()) {
            isValid = false;
            editTelephone.setError("Bitte geben Sie eine Telefonnummer ein");
        }

        if (email.isEmpty()) {
            isValid = false;
            editEmail.setError("Bitte geben Sie eine E-Mail-Adresse ein");
        }

        return isValid;
    }

    // Verwende die Methode in deiner showConfirmationDialog-Methode
    private void showConfirmationDialog() {
        if (!isInputValid(
                editStoreName.getText().toString(),
                editOwner.getText().toString(),
                editStreet.getText().toString(),
                editHouseNumber.getText().toString(),
                editZip.getText().toString(),
                editTelephone.getText().toString(),
                editEmail.getText().toString()
        )) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Daten speichern");
        builder.setMessage("Sind Sie sicher, dass Sie die Daten speichern möchten?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setSettings(SettingParameter.storeName, editStoreName.getText().toString());
                        setSettings(SettingParameter.owner, editOwner.getText().toString());
                        setSettings(SettingParameter.telephone, editTelephone.getText().toString());
                        setSettings(SettingParameter.email, editEmail.getText().toString());
                        setAddress(editStreet.getText().toString(), editHouseNumber.getText().toString(), editZip.getText().toString());

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                saveData.setEnabled(false);
                            }
                        });
                    }
                }).start();
                dialog.dismiss();
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


*/


    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Daten speichern");
        builder.setMessage("Sind Sie sicher, dass Sie die Daten speichern möchten?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String storeName = editStoreName.getText().toString();
                String owner = editOwner.getText().toString();
                String street = editStreet.getText().toString();
                String houseNumber = editHouseNumber.getText().toString();
                String zip = editZip.getText().toString();
                String telephone = editTelephone.getText().toString();
                String email = editEmail.getText().toString();

                // Hier rufst du die Methode auf, um die Validierung durchzuführen
                if (isInputValid(storeName, owner, street, houseNumber, zip, telephone, email)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setSettings(SettingParameter.storeName, storeName);
                            setSettings(SettingParameter.owner, owner);
                            setSettings(SettingParameter.telephone, telephone);
                            setSettings(SettingParameter.email, email);
                            setAddress(street, houseNumber, zip);

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    saveData.setEnabled(false);
                                }
                            });
                        }
                    }).start();
                    dialog.dismiss();
                } else {
                    // Zeige eine Fehlermeldung an, wenn die Validierung fehlschlägt
                    Toast.makeText(requireContext(), "Bitte überprüfen Sie die Eingabe", Toast.LENGTH_SHORT).show();
                }
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

    public void setSettings(SettingParameter parameter, String value) {
        if (getSavedToken() == null) {
            Log.d("Settings", "Kein Token");
            return;
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

            if (responseCode == 200) {

                showSuccessPopup();
                enableEditMode();

            } else {
                showErrorPopup();
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            showErrorPopup();
        }
    }




    public void setAddress(String street, String houseNumber, String zip) {
        if (getSavedToken() == null) {
            Log.d("Settings", "Kein Token");
            return;
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

            if (responseCode == 200) {

                showSuccessPopup();
            } else {

                showErrorPopup();

            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            showErrorPopup();
        }
    }


    public void showSuccessPopup() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Show the success message using a Toast on the main UI thread
                Toast.makeText(requireContext(), "Daten erfolgreich an den Server gesendet.", Toast.LENGTH_SHORT).show();
                enableEditMode();
            }
        });
    }

    private void showErrorPopup() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Show the error message using a Toast on the main UI thread
                Toast.makeText(requireContext(), "Keine Verbindung zum Server.", Toast.LENGTH_SHORT).show();
            }
        });
    }




}