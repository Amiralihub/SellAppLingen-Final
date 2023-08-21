package com.example.sellapplingen;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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


public class SettingFragment extends Fragment {

    private EditText editStoreName, editOwner, editStreet, editHouseNumber, editZip, editTelephone, editEmail;
    private Button saveData;
    private String token;

    private boolean isEditMode = false; // Neue Variable für den Bearbeitungsmodus
    private boolean isDataEdited = false; // Neue Variable für bearbeitete Daten

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
        Button logoutButton = view.findViewById(R.id.logoutButton); // Initialize the log out button

        // Lese den Token aus den SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getSettings();
            }
        }).start();

        Button editDataButton = view.findViewById(R.id.editDataButton);
        editDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = true; // Aktivieren Sie den Bearbeitungsmodus
                enableEditMode(); // Aktivieren Sie die Bearbeitung der Daten
            }
        });

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }

        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }

        });

        setupTextWatchers(); // Fügen Sie die TextWatcher für die Textfelder hinzu

        return view;
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

    private void enableEditMode() {
        // Aktivieren Sie die Bearbeitung der Daten
        if (isEditMode) {
            editStoreName.setEnabled(true);
            editOwner.setEnabled(true);
            editStreet.setEnabled(true);
            editHouseNumber.setEnabled(true);
            editZip.setEnabled(true);
            editTelephone.setEnabled(true);
            editEmail.setEnabled(true);
            enableSaveButton(); // Überprüfen Sie den Zustand des Speichern-Buttons
        }
    }

    private void enableSaveButton() {
        // Aktivieren oder deaktivieren Sie den "Daten aktualisieren"-Button basierend auf dem Bearbeitungsstatus
        if (isDataEdited && isEditMode) {
            saveData.setEnabled(true);
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
        requireActivity().finish(); // Optional, um die aktuelle Aktivität zu schließen
    }



    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Daten speichern");
        builder.setMessage("Sind Sie sicher, dass Sie die Daten speichern möchten?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setSettings();
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



    private String getSavedToken() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }


    private void getSettings() {
        String testToken = token;
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


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String storeName = jsonResponse.getString("storeName");
                            String owner = jsonResponse.getString("owner");
                            String street = jsonResponse.getString("street");
                            String houseNumber = jsonResponse.getString("houseNumber");
                            String zip = jsonResponse.getString("zip");
                            String telephone = jsonResponse.getString("telephone");
                            String email = jsonResponse.getString("email");

                            if (!isEditMode) {
                                editStoreName.setEnabled(false);
                                editOwner.setEnabled(false);
                                editStreet.setEnabled(false);
                                editHouseNumber.setEnabled(false);
                                editZip.setEnabled(false);
                                editTelephone.setEnabled(false);
                                editEmail.setEnabled(false);
                            }

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    editStoreName.setText(storeName);
                                    editOwner.setText(owner);
                                    editStreet.setText(street);
                                    editHouseNumber.setText(houseNumber);
                                    editZip.setText(zip);
                                    editTelephone.setText(telephone);
                                    editEmail.setText(email);
                                }
                            });
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
        System.out.println();
        String testToken = token;
        if (getSavedToken() == null) {
            Log.d("Settings", "Kein Token");
            return;
        }

        try {
            String storeName = editStoreName.getText().toString();
            String owner = editOwner.getText().toString();
            String street = editStreet.getText().toString();
            String houseNumber = editHouseNumber.getText().toString();
            String zip = editZip.getText().toString();
            String telephone = editTelephone.getText().toString();
            String email = editEmail.getText().toString();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", testToken);
            jsonParam.put("storeName", storeName);
            jsonParam.put("owner", owner);
            jsonParam.put("street", street);
            jsonParam.put("houseNumber", houseNumber);
            jsonParam.put("zip", zip);
            jsonParam.put("telephone", telephone);
            jsonParam.put("email", email);

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

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());

            System.out.println(conn.getResponseMessage());
            if (conn.getResponseCode() == 403) {

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