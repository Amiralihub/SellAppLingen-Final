package com.example.sellapplingen;

import static com.example.sellapplingen.SettingParameter.email;
import static com.example.sellapplingen.SettingParameter.telephone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.json.JSONException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SettingFragment extends Fragment {

    private EditText editStoreName, editOwner, editStreet, editHouseNumber, editZip, editTelephone, editEmail;
    private Button saveData;
    private String token;

    private final SettingManager settingManager = new SettingManager();


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
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });

        Button logoutButton = view.findViewById(R.id.logoutButton);

        InputFilter emojiFilter = new EmojiExcludeFilter();
        editStoreName.setFilters(new InputFilter[]{emojiFilter});
        editOwner.setFilters(new InputFilter[]{emojiFilter});
        editStreet.setFilters(new InputFilter[]{emojiFilter});
        editTelephone.setFilters(new InputFilter[]{emojiFilter});
        editZip.setFilters(new InputFilter[]{emojiFilter});
        editEmail.setFilters(new InputFilter[]{emojiFilter});
        editHouseNumber.setFilters(new InputFilter[]{emojiFilter});

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

        new Thread(() -> {
            try {
                Settings settings = SettingManager.getSettings(token);
                if (settings != null) {
                    requireActivity().runOnUiThread(() -> {
                        editStoreName.setText(settings.getStoreName());
                        editOwner.setText(settings.getOwner());
                        editStreet.setText(settings.getAddress().getStreet());
                        editHouseNumber.setText(settings.getAddress().getHouseNumber());
                        editZip.setText(settings.getAddress().getZip());
                        editTelephone.setText(settings.getTelephone());
                        editEmail.setText(settings.getEmail());

                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();



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



    public class EmojiExcludeFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder filtered = new StringBuilder();
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type != Character.SURROGATE && type != Character.OTHER_SYMBOL) {
                    filtered.append(source.charAt(i));
                }
            }
            return (source instanceof Spanned) ? new SpannableString(filtered) : filtered.toString();
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

        if (storeName.trim().isEmpty() ) {
            isValid = false;
            editStoreName.setError("Bitte geben Sie einen gültigen Geschäftsnamen ein");
        }

        if (owner.trim().isEmpty() ) {
            isValid = false;
            editOwner.setError("Bitte geben Sie einen gültigen Eigentümer ein");
        }

        if (street.trim().isEmpty() ) {
            isValid = false;
            editStreet.setError("Bitte geben Sie eine gültige Straße ein");
        }

        if (houseNumber.trim().isEmpty() ) {
            isValid = false;
            editHouseNumber.setError("Bitte geben Sie eine gültige Hausnummer ein");
        }

        if (!isValidZipCode(zip)) {
            isValid = false;
            editZip.setError("Bitte geben Sie eine gültige PLZ ein (49808, 49809, 49811)");
        }

        if (telephone.trim().isEmpty() ) {
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

    public void setAddressToServer() {
        String street = editStreet.getText().toString();
        String houseNumber = editHouseNumber.getText().toString();
        String zip = editZip.getText().toString();

        try {
            boolean success = SettingManager.setAddress(token, street, houseNumber, zip);
            if (!success) {
                showErrorPopup();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            showErrorPopup();
        }
    }

    public void setSettingsToServer() {
        String storeName = editStoreName.getText().toString();
        String owner = editOwner.getText().toString();
        String telephone = editTelephone.getText().toString();
        String email = editEmail.getText().toString();

        try {
            boolean success = SettingManager.setSettings(token, storeName, owner, telephone, email);
            if (!success) {
                showErrorPopup();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            showErrorPopup();
        }
    }


    public void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Bestätigung");
        builder.setMessage("Möchten Sie die Änderungen speichern?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Extract data from EditText fields
                String storeName = editStoreName.getText().toString();
                String owner = editOwner.getText().toString();
                String street = editStreet.getText().toString();
                String houseNumber = editHouseNumber.getText().toString();
                String zip = editZip.getText().toString();
                String telephone = editTelephone.getText().toString();
                String email = editEmail.getText().toString();

                try {
                    boolean setAddressSuccess = SettingManager.setAddress(token, street, houseNumber, zip);
                    boolean setSettingSuccess = SettingManager.setSettings(token, storeName, owner, telephone, email);

                    if (setAddressSuccess && setSettingSuccess) {
                        showSuccessPopup();
                    } else {
                        showErrorPopup();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    showErrorPopup();
                }
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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