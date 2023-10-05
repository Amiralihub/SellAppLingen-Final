package sellapp.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.sellapplingen.R;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionException;

import sellapp.activities.ChangePasswordActivity;
import sellapp.activities.LoginActivity;
import sellapp.models.Address;
import sellapp.models.EmojiExcludeFilter;
import sellapp.models.LogInData;
import sellapp.models.SetAddress;
import sellapp.models.SettingManager;
import sellapp.models.StoreDetails;
import sellapp.models.ValidationManager;

/**
 * This class represents a fragment for managing user settings.
 * It provides a user interface for editing and saving settings such as store name,
 * owner's name, address, zip code, telephone number, and email.
 * The fragment allows users to edit their settings and save changes.
 */
public class SettingFragment extends Fragment
    {
    /**
     * The EditText field for the store name setting.
     */
    private EditText editStoreName;

    /**
     * The EditText field for the owner's name setting.
     */
    private EditText editOwner;

    /**
     * The EditText field for the street address setting.
     */
    private EditText editStreet;

    /**
     * The EditText field for the house number setting.
     */
    private EditText editHouseNumber;

    /**
     * The EditText field for the ZIP code setting.
     */
    private EditText editZip;

    /**
     * The EditText field for the telephone number setting.
     */
    private EditText editTelephone;

    /**
     * The EditText field for the email setting.
     */
    private EditText editEmail;

    /**
     * The button used to save user data.
     */
    private Button saveData;

    /**
     * Stores the user's current settings.
     */
    private StoreDetails settings;

    /**
     * Flag indicating whether the fragment is in edit mode.
     */
    private boolean isEditMode;

    /**
     * Watcher for tracking changes in user data input.
     */
    private DataEditWatcher dataEditWatcher;

    /**
     * Inflates the fragment layout and initializes UI elements.
     *
     * @param inflater           The LayoutInflater to inflate the layout.
     * @param container          The ViewGroup container.
     * @param savedInstanceState The saved instance state.
     * @return The View of the inflated fragment.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
                            )
        {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        if (!isNetworkAvailable())
            {
            showNoConnectionPopup();
            return view;
            }
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

        if (settings != null)
            {

            initializeFieldsWithSettings();
            }
        else
            {
            Toast.makeText(
                    requireContext(),
                    "Keine Daten vom Server empfangen oder keine Internetverbindung.",
                    Toast.LENGTH_LONG
                          ).show();
            }

        Button changePasswordButton = view.findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(v ->
                                                    {
                                                    Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
                                                    startActivity(intent);
                                                    });

        saveData.setOnClickListener(v -> showConfirmationDialog());

        Button logoutButton = view.findViewById(R.id.logoutButton);
        setupInputFilters();
        setupTextWatchers();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(
                LogInData.PREF_NAME, Context.MODE_PRIVATE);

        sharedPreferences.getString("token", null);

        setupEditDataButton(view);

        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        return view;
        }

    private boolean isNetworkAvailable()
        {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
            {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        return false;
        }

    private void showNoConnectionPopup()
        {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Keine Internetverbindung");
        builder.setMessage(
                "Es besteht keine Verbindung zum Internet. Die Daten können nicht abgerufen werden.");

        builder.setPositiveButton("OK", (dialog, which) ->
            {

            });

        AlertDialog dialog = builder.create();
        dialog.show();
        }

    /**
     * Initializes UI fields with user settings if available.
     */
    private void initializeFieldsWithSettings()
        {
        requireActivity().runOnUiThread(() ->
                                            {
                                            editStoreName.setText(settings.getStoreName());
                                            editOwner.setText(settings.getOwner());
                                            editStreet.setText(settings.getAddress().getStreet());
                                            editHouseNumber.setText(
                                                    settings.getAddress().getHouseNumber());
                                            editZip.setText(settings.getAddress().getZip());
                                            editTelephone.setText(settings.getTelephone());
                                            editEmail.setText(settings.getEmail());
                                            new ValidationManager(editStoreName, editOwner,
                                                                  editStreet, editHouseNumber,
                                                                  editZip, editTelephone, editEmail
                                            );
                                            });
        }

    private void setupInputFilters()
        {
        InputFilter emojiFilter = new EmojiExcludeFilter();
        setEditTextFilters(editStoreName, emojiFilter);
        setEditTextFilters(editOwner, emojiFilter);
        setEditTextFilters(editStreet, emojiFilter);
        setEditTextFilters(editTelephone, emojiFilter);
        setEditTextFilters(editZip, emojiFilter);
        setEditTextFilters(editEmail, emojiFilter);
        setEditTextFilters(editHouseNumber, emojiFilter);
        }

    /**
     * Sets an input filter for an EditText field.
     *
     * @param editText The EditText to set the filter for.
     * @param filter   The InputFilter to apply.
     */

    private void setEditTextFilters(EditText editText, InputFilter filter)
        {
        editText.setFilters(new InputFilter[]{filter});
        }


    private void setupEditDataButton(View view)
        {
        Button editDataButton = view.findViewById(R.id.editDataButton);
        editDataButton.setOnClickListener(v ->
                                              {
                                              isEditMode = true;
                                              enableEditMode(true);
                                              });
        }

    private void setupTextWatchers()
        {
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

    private TextWatcher createTextWatcher()
        {
        return new TextWatcher()
            {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                enableSaveButton();
                }

            @Override
            public void afterTextChanged(Editable editable)
                {
                }
            };
        }

    private void enableEditMode(boolean enable)
        {
        editStoreName.setEnabled(enable);
        editOwner.setEnabled(enable);
        editStreet.setEnabled(enable);
        editHouseNumber.setEnabled(enable);
        editZip.setEnabled(enable);
        editTelephone.setEnabled(enable);
        editEmail.setEnabled(enable);
        }

    private void enableSaveButton()
        {
        if (isEditMode)
            {
            saveData.setEnabled(dataEditWatcher.anyFieldEdited());
            }
        else
            {
            saveData.setEnabled(false);
            }
        }

    private void showLogoutConfirmationDialog()
        {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Abmelden");
        builder.setMessage("Sind Sie sicher, dass Sie sich abmelden möchten?");

        builder.setPositiveButton("Ja", (dialog, which) -> performLogout());

        builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        }

    private void performLogout()
        {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(
                LogInData.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean updateAddressIfNeeded(String street, String houseNumber, String zip)
        {
        Address oldAddress = settings.getAddress();
        Address newAddress = new Address(street, houseNumber, zip);

        if (!oldAddress.equals(newAddress))
            {
            SetAddress toSendAddress = new SetAddress(newAddress);
            Gson gson = new Gson();
            String jsonString = gson.toJson(newAddress);
            System.out.println("json to send: " + jsonString);

            if (SettingManager.setAddress(toSendAddress))
                {
                Toast.makeText(
                        requireContext(), "Die Adresse wurde erfolgreich übermittelt!",
                        Toast.LENGTH_SHORT
                              ).show();
                return true;
                }
            else
                {
                Toast.makeText(
                        requireContext(),
                        "Die Adresse existiert nicht in Lingen, Bitte korigieren Sie Ihre eingabe.",
                        Toast.LENGTH_LONG
                              ).show();
                return false;
                }
            }
        return true;
        }

    /**
     * Displays a confirmation dialog to prompt the user for saving data changes.
     * If the user chooses to save the changes, this method validates the input provided
     * by the user, including the store name, owner's name, street address, house number,
     * ZIP code, telephone number, and email address. If all input fields are valid, it
     * proceeds to update the user settings accordingly. It sends the updated settings to
     * the server for synchronization.
     *
     * @implNote This method ensures that the user's input adheres to specific validation
     * criteria using the {@link ValidationManager} class. If any of the input
     * fields fail validation, an error message is displayed to the user.
     * @see ValidationManager
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showConfirmationDialog()
        {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Bestätigung");
        builder.setMessage("Möchten Sie die Änderungen speichern?");

        builder.setPositiveButton("Ja", (dialog, which) ->
            {
            try
                {
                if (ValidationManager.isInputValid(
                        editStoreName.getText().toString().trim(),
                        editOwner.getText().toString().trim(),
                        editStreet.getText().toString().trim(),
                        editHouseNumber.getText().toString().trim(),
                        editZip.getText().toString().trim(),
                        editTelephone.getText().toString().trim(),
                        editEmail.getText().toString().trim()
                                                  ))
                    {
                    String storeName = editStoreName.getText().toString().trim();
                    String owner = editOwner.getText().toString().trim();
                    String street = editStreet.getText().toString().trim();
                    String houseNumber = editHouseNumber.getText().toString().trim();
                    String zip = editZip.getText().toString().trim();
                    String telephone = editTelephone.getText().toString().trim();
                    String email = editEmail.getText().toString().trim();

                    if (settings != null)
                        {
                        if (!storeName.equals(settings.getStoreName()))
                            {
                            sendSettings(SettingManager.Parameter.STORE_NAME, storeName);
                            }

                        if (!owner.equals(settings.getOwner()))
                            {
                            sendSettings(SettingManager.Parameter.OWNER, owner);
                            }

                        if (!telephone.equals(settings.getTelephone()))
                            {
                            sendSettings(SettingManager.Parameter.TELEPHONE, telephone);
                            }

                        if (!email.equals(settings.getEmail()))
                            {
                            sendSettings(SettingManager.Parameter.EMAIL, email);
                            }
                        boolean needsUpdate = updateAddressIfNeeded(street, houseNumber, zip);
                        saveData.setEnabled(false);
                        if (needsUpdate)
                            {
                            enableEditMode(false);
                            }
                        }
                    else
                        {
                        Toast.makeText(
                                     requireContext(), "Keine Internetverbindung", Toast.LENGTH_SHORT)
                             .show();
                        }
                    }
                else
                    {
                    Toast.makeText(
                            requireContext(), "Bitte folgen Sie die Anweisungen.",
                            Toast.LENGTH_SHORT
                                  ).show();
                    }
                } catch (Exception e)
                {
                e.printStackTrace();
                Toast.makeText(
                        requireContext(),
                        "Es ist ein Fehler aufgetreten. Bitte kontaktieren Sie das Entwicklungsteam.",
                        Toast.LENGTH_LONG
                              ).show();
                }
            });

        builder.setNegativeButton("Nein", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        }

    private boolean successPopupShown;


    /**
     * Sends updated user settings to the server for synchronization.
     * This method is responsible for sending specific user settings to the server based on
     * the provided parameter and value. It communicates with the {@link SettingManager} to
     * perform the setting update operation and handles success and error scenarios.
     *
     * @param parameter The parameter representing the setting to be updated (e.g., "STORE_NAME").
     * @param value     The new value to be set for the specified setting parameter.
     * @implNote This method uses the {@link SettingManager} class to update user settings on
     * the server. It checks the result of the operation and displays success or
     * error messages accordingly. In case of a successful setting update, a success
     * message is displayed using the {@link #showSuccessPopup()} method. In case of
     * an error or failure, an error message is displayed using the {@link #showErrorPopup()}
     * method.
     * @see SettingManager
     * @see #showSuccessPopup()
     * @see #showErrorPopup()
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendSettings(String parameter, String value)
        {
        try
            {
            Boolean setSettingSuccess = SettingManager.setSettings(parameter, value);

            if (setSettingSuccess && !successPopupShown)
                {
                showSuccessPopup();
                successPopupShown = true;
                }
            else if (!setSettingSuccess)
                {
                showErrorPopup();
                }
            } catch (CompletionException e)
            {
            e.printStackTrace();
            showErrorPopup();
            }
        }

    private void showSuccessPopup()
        {
        requireActivity().runOnUiThread(() -> Toast
                .makeText(requireContext(), "Daten erfolgreich an den Server gesendet.",
                          Toast.LENGTH_SHORT
                         ).show());
        }

    private void showErrorPopup()
        {
        requireActivity().runOnUiThread(() -> Toast
                .makeText(requireContext(), "Keine Verbindung zum Server.", Toast.LENGTH_SHORT)
                .show());
        }

    private final class DataEditWatcher implements TextWatcher
        {
        private final Set<EditText> watchedFields = new HashSet<>();

        public void watch(EditText editText)
            {
            watchedFields.add(editText);
            editText.addTextChangedListener(this);
            }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            enableSaveButton();
            }

        @Override
        public void afterTextChanged(Editable editable)
            {
            }

        /**
         * Checks if any of the monitored EditText fields have been edited by the user.
         * This method examines a set of EditText fields that have been monitored for changes
         * by the {@link DataEditWatcher} and determines whether any of them have been modified
         * from their initial values.
         *
         * @return {@code true} if any of the monitored EditText fields have been edited by
         * the user; {@code false} otherwise.
         * @implNote This method is used to determine whether there are unsaved changes in the
         * user's input. It compares the current text in each monitored EditText field
         * to its initial value (tag), and if any field has been modified, it returns
         * {@code true} to indicate that unsaved changes are present.
         * @see DataEditWatcher
         */


        public boolean anyFieldEdited()
            {
            for (EditText editText : watchedFields)
                {
                if (!editText.getText().toString().equals(editText.getTag()))
                    {
                    return true;
                    }
                }
            return false;
            }
        }
    }
