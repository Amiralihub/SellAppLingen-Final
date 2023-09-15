package sellapp.fragments;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.sellapplingen.R;
import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionException;
import sellapp.activities.LoginActivity;
import sellapp.models.Address;
import sellapp.models.EmojiExcludeFilter;
import sellapp.models.LogInData;
import sellapp.models.SetAddress;
import sellapp.models.SettingManager;
import sellapp.models.StoreDetails;
import sellapp.models.ValidationManager;

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


    private boolean isEditMode = false;
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


        editStoreName.setEnabled(false);
        editOwner.setEnabled(false);
        editStreet.setEnabled(false);
        editHouseNumber.setEnabled(false);
        editZip.setEnabled(false);
        editTelephone.setEnabled(false);
        editEmail.setEnabled(false);
        saveData.setEnabled(false);

        settings = SettingManager.getSettings();

        if (settings != null) {
            initializeFieldsWithSettings();
        }

        Button changePasswordButton = view.findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);

                startActivity(intent);
            }
        });




        saveData.setOnClickListener(v -> showConfirmationDialog());

        Button logoutButton = view.findViewById(R.id.logoutButton);
        setupInputFilters();
        setupTextWatchers();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LogInData.PREF_NAME, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        setupEditDataButton(view);

        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        return view;

    }



    private void initializeFieldsWithSettings() {
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

    private void setupInputFilters() {
        InputFilter emojiFilter = new EmojiExcludeFilter();
        setEditTextFilters(editStoreName, emojiFilter);
        setEditTextFilters(editOwner, emojiFilter);
        setEditTextFilters(editStreet, emojiFilter);
        setEditTextFilters(editTelephone, emojiFilter);
        setEditTextFilters(editZip, emojiFilter);
        setEditTextFilters(editEmail, emojiFilter);
        setEditTextFilters(editHouseNumber, emojiFilter);
    }

    private void setEditTextFilters(EditText editText, InputFilter filter) {
        editText.setFilters(new InputFilter[]{filter});
    }


    private void setupEditDataButton(View view) {
        Button editDataButton = view.findViewById(R.id.editDataButton);
        editDataButton.setOnClickListener(v -> {
            isEditMode = true;
            enableEditMode(true);
        });
    }

    private void setupTextWatchers() {
        dataEditWatcher = new DataEditWatcher();
        dataEditWatcher.watch(editStoreName);
        dataEditWatcher.watch(editOwner);
        dataEditWatcher.watch(editStreet);
        dataEditWatcher.watch(editHouseNumber);
        dataEditWatcher.watch(editZip);
        dataEditWatcher.watch(editTelephone);
        dataEditWatcher.watch(editEmail);

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

        builder.setPositiveButton("Ja", (dialog, which) -> performLogout());

        builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performLogout() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LogInData.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void updateAddressIfNeeded(String street, String houseNumber, String zip) {
        Address oldAddress = settings.getAddress();
        Address newAddress = new Address(street, houseNumber, zip);

        if (!oldAddress.equals(newAddress)) {
            SetAddress toSendAddress = new SetAddress(newAddress);
            Gson gson = new Gson();
            String jsonString = gson.toJson(newAddress);
            System.out.println("json to send: " + jsonString);

            if (SettingManager.setAddress(toSendAddress)) {
                Toast.makeText(requireContext(), "Die Adresse wurde erfolgreich übermittelt!", Toast.LENGTH_SHORT).show();  //GET Adress vom server vllt Sinnig?
            } else {
                Toast.makeText(requireContext(), "Die Adresse existiert nicht in Lingen oder es ist ein Fehler aufgetreten. Bitte kontaktieren Sie das Entwicklungsteam.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Bestätigung");
        builder.setMessage("Möchten Sie die Änderungen speichern?");

        builder.setPositiveButton("Ja", (dialog, which) -> {
            try {
                if (ValidationManager.isInputValid(editStoreName.getText().toString().trim(), editOwner.getText().toString().trim(), editStreet.getText().toString().trim(), editHouseNumber.getText().toString().trim(), editZip.getText().toString().trim(), editTelephone.getText().toString().trim(), editEmail.getText().toString().trim())) {
                    String storeName = editStoreName.getText().toString().trim();
                    String owner = editOwner.getText().toString().trim();
                    String street = editStreet.getText().toString().trim();
                    String houseNumber = editHouseNumber.getText().toString().trim();
                    String zip = editZip.getText().toString().trim();
                    String telephone = editTelephone.getText().toString().trim();
                    String email = editEmail.getText().toString().trim();

                    if (settings != null) {
                        if (!storeName.equals(settings.getStoreName())) {
                            sendSettings(SettingManager.Parameter.STORE_NAME, storeName);
                        }

                        if (!owner.equals(settings.getOwner())) {
                            sendSettings(SettingManager.Parameter.OWNER, owner);
                        }

                        if (!telephone.equals(settings.getTelephone())) {
                            sendSettings(SettingManager.Parameter.TELEPHONE, telephone);
                        }

                        if (!email.equals(settings.getEmail())) {
                            sendSettings(SettingManager.Parameter.EMAIL, email);
                        }
                        updateAddressIfNeeded(street, houseNumber, zip);

                        saveData.setEnabled(false);
                        enableEditMode(false);
                    } else {
                        Toast.makeText(requireContext(), "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Bitte folgen Sie die Anweisungen.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Es ist ein Fehler aufgetreten. Bitte kontaktieren Sie das Entwicklungsteam.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Nein", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean successPopupShown = false;


    private void sendSettings(String parameter, String value) {
        try {
            Boolean setSettingSuccess = SettingManager.setSettings(parameter, value);

            if (setSettingSuccess && !successPopupShown) {
                showSuccessPopup();
                successPopupShown = true;
            } else if (!setSettingSuccess) {
                showErrorPopup();
            }
        } catch (CompletionException e) {
            e.printStackTrace();
            showErrorPopup();
        }
    }

    private void showSuccessPopup() {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Daten erfolgreich an den Server gesendet.", Toast.LENGTH_SHORT).show());
    }

    private void showErrorPopup() {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Keine Verbindung zum Server.", Toast.LENGTH_SHORT).show());
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
}
