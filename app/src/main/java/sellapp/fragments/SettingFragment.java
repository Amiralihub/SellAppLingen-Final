package sellapp.fragments;


import sellapp.models.ValidationManager;

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

import sellapp.models.Address;
import sellapp.models.LogInData;
import sellapp.activities.LoginActivity;
import com.example.sellapplingen.R;
import sellapp.models.SetAddress;
import sellapp.models.SettingManager;
import sellapp.models.StoreDetails;
import sellapp.models.ValidationManager;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionException;

public class SettingFragment extends Fragment {
    private EditText editStoreName;
    private EditText editOwner;
    private EditText editStreet;
    private EditText editHouseNumber;
    private EditText editZip;
    private EditText editTelephone;
    private EditText editEmail;
    private Button saveData;
    private String token;

    private StoreDetails settings;
    private ValidationManager validationManager;



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

        settings = SettingManager.getSettings();

        if (settings != null) {
            requireActivity().runOnUiThread(() -> {
                editStoreName.setText(settings.getStoreName());
                editOwner.setText(settings.getOwner());
                editStreet.setText(settings.getAddress().getStreet());
                editHouseNumber.setText(settings.getAddress().getHouseNumber());
                editZip.setText(settings.getAddress().getZip());
                editTelephone.setText(settings.getTelephone());
                editEmail.setText(settings.getEmail());

                validationManager = new ValidationManager(editStoreName, editOwner, editStreet, editHouseNumber, editZip, editTelephone, editEmail);
            });
        }




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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LogInData.PREF_NAME, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);


        new Thread(() ->
        {
            settings = SettingManager.getSettings();

            if (settings != null) {
                requireActivity().runOnUiThread(() ->
                {
                    editStoreName.setText(settings.getStoreName());
                    editOwner.setText(settings.getOwner());
                    editStreet.setText(settings.getAddress().getStreet());
                    editHouseNumber.setText(settings.getAddress().getHouseNumber());
                    editZip.setText(settings.getAddress().getZip());
                    editTelephone.setText(settings.getTelephone());
                    editEmail.setText(settings.getEmail());

                });
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
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableSaveButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

        public boolean anyFieldEdited() {
            for (EditText editText : watchedFields) {
                if (!editText.getText().toString().equals(editText.getTag())) {
                    return true;
                }
            }
            return false;
        }
    }


    public static class EmojiExcludeFilter implements InputFilter {
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableSaveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LogInData.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();

        // Start LoginActivity using an Intent
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    public void showConfirmationDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Bestätigung");
            builder.setMessage("Möchten Sie die Änderungen speichern?");

            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {

                        // Validate user input
                        if (ValidationManager.isInputValid(editStoreName.getText().toString().trim(), editOwner.getText().toString().trim(), editStreet.getText().toString().trim(), editHouseNumber.getText().toString().trim(), editZip.getText().toString().trim(), editTelephone.getText().toString().trim(), editEmail.getText().toString().trim())) {
                            String storeName = editStoreName.getText().toString().trim();
                            String owner = editOwner.getText().toString().trim();
                            String street = editStreet.getText().toString().trim();
                            String houseNumber = editHouseNumber.getText().toString().trim();
                            String zip = editZip.getText().toString().trim();
                            String telephone = editTelephone.getText().toString().trim();
                            String email = editEmail.getText().toString().trim();

                            if (settings != null) {
                                // Compare and update settings
                                if (!storeName.equals(settings.getStoreName())) {
                                    sendSettings(SettingManager.Parameter.STORE_NAME, storeName);
                                }

                                if (!owner.equals(settings.getOwner())) {
                                    sendSettings(SettingManager.Parameter.OWNER, owner);
                                }

                                Address oldAddress = settings.getAddress();
                                if (!street.equals(oldAddress.getStreet()) ||
                                        !houseNumber.equals(oldAddress.getHouseNumber()) ||
                                        !zip.equals(oldAddress.getZip())) {

                                    Address address = new Address(street, houseNumber, zip);
                                    SetAddress toSendAddress = new SetAddress(address);
                                    Gson gson = new Gson();
                                    String jsonString = gson.toJson(address);
                                    System.out.println("json to send: " + jsonString);

                                    if (SettingManager.setAddress(toSendAddress)) {
                                        showSuccessPopup();
                                    } else {
                                        Toast.makeText(requireContext(), "waaas", Toast.LENGTH_SHORT).show();                                    }
                                }

                                if (!telephone.equals(settings.getTelephone())) {
                                    sendSettings(SettingManager.Parameter.TELEPHONE, telephone);
                                }

                                if (!email.equals(settings.getEmail())) {
                                    sendSettings(SettingManager.Parameter.EMAIL, email);
                                }
                            } else {
                                // Handle the case when settings are null (no internet connection, for example)
                                Toast.makeText(requireContext(), "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle invalid input
                            Toast.makeText(requireContext(), "Bitte überprüfen Sie Ihre Eingabe", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle any exceptions here
                        Toast.makeText(requireContext(), "Ein Fehler ist aufgetreten", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ein Fehler ist aufgetreten, Bitte Kontaktieren Sie Die Entwickler Gruppe", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSettings(String parameter, String value) {
        try {
            Boolean setSettingSuccess = SettingManager.setSettings(parameter, value);

            if (setSettingSuccess) {
                showSuccessPopup();
            } else {
                showErrorPopup();
            }
        } catch (CompletionException e) {
            e.printStackTrace();
            showErrorPopup();
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