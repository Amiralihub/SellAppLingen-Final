package com.example.sellapplingen;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.sellapplingen.databinding.FragmentManualInputBinding;

public class ManualInputFragment extends Fragment {

    private FragmentManualInputBinding binding;
    private Order currentOrder;

    private String selectedZipCode = "";
    private String[] zipCodes = {"49808", "49809", "49811"}; // Array der verfügbaren PLZ-Optionen

    public ManualInputFragment() {
        // Required empty public constructor
    }

    public void setCurrentOrder(Order order) {
        currentOrder = order;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentManualInputBinding.inflate(inflater, container, false);
        setupViews();
        return binding.getRoot();
    }

    private void setupViews() {
        binding.confirmManualInputButton.setOnClickListener(v -> saveManualInput());

        // Initialisiere den Spinner
        ArrayAdapter<String> zipAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, zipCodes);
        binding.zipSpinner.setAdapter(zipAdapter);

        binding.zipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Setze den ausgewählten Wert im Spinner
                selectedZipCode = zipCodes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nichts ausgewählt
            }
        });
    }


    private void saveManualInput() {
        // Überprüfe, ob alle Felder ausgefüllt sind oder Daten eingegeben wurden
        if (isInputValid()) {
            // Speichere die manuell eingegebenen Order-Informationen im currentOrder-Objekt
            Address address = new Address(binding.streetEditText.getText().toString(), binding.houseNumberEditText.getText().toString(), selectedZipCode);
            Recipient recipient = new Recipient(binding.firstNameEditText.getText().toString(), binding.lastNameEditText.getText().toString(), address);
            currentOrder.setRecipient(recipient);

            // Erstelle das Bundle für die Übergabe der Order-Daten
            Bundle args = new Bundle();
            args.putSerializable("order", currentOrder);

            // Erstelle das HandlingInfoFragment und setze die Argumente
            HandlingInfoFragment handlingInfoFragment = new HandlingInfoFragment();
            handlingInfoFragment.setArguments(args);

            // Wechsle zum HandlingInfoFragment
            FragmentManager fragmentManager = requireFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, handlingInfoFragment, "handlingInfoFragment");
            transaction.addToBackStack(null); // Füge das Fragment zur Rückwärtsnavigation hinzu
            transaction.commit();
        } else {
            // Zeige eine Benachrichtigung, wenn nicht alle Felder ausgefüllt sind
            Toast.makeText(requireContext(), "Bitte füllen Sie alle Felder aus.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputValid() {
        String lastName = binding.lastNameEditText.getText().toString();
        String firstName = binding.firstNameEditText.getText().toString();
        String street = binding.streetEditText.getText().toString();
        String houseNumber = binding.houseNumberEditText.getText().toString();

        // Überprüfe, ob die Felder ausgefüllt sind oder Daten eingegeben wurden
        return !lastName.isEmpty() && !firstName.isEmpty() && !street.isEmpty() && !houseNumber.isEmpty() && !selectedZipCode.isEmpty();
    }





}
