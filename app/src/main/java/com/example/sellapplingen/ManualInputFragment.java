package com.example.sellapplingen;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.sellapplingen.databinding.FragmentManualInputBinding;

public class ManualInputFragment extends Fragment {

    private FragmentManualInputBinding binding;
    private Order currentOrder;

    private String selectedZipCode = "";


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
        binding.zipButton.setOnClickListener(v -> showZipCodeOptions());
    }


    private void saveManualInput() {
        // Überprüfe, ob alle Felder ausgefüllt sind oder Daten eingegeben wurden
        if (isInputValid()) {
            // Speichere die manuell eingegebenen Order-Informationen im currentOrder-Objekt
            currentOrder.setLastName(binding.lastNameEditText.getText().toString());
            currentOrder.setFirstName(binding.firstNameEditText.getText().toString());
            currentOrder.setStreet(binding.streetEditText.getText().toString());
            currentOrder.setHouseNumber(binding.houseNumberEditText.getText().toString());
            currentOrder.setZip(selectedZipCode);
            currentOrder.setCity(binding.cityEditText.getText().toString());

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
        String city = binding.cityEditText.getText().toString();

        // Überprüfe, ob die Felder ausgefüllt sind oder Daten eingegeben wurden
        return !lastName.isEmpty() && !firstName.isEmpty() && !street.isEmpty() && !houseNumber.isEmpty() && !selectedZipCode.isEmpty() && !city.isEmpty();
    }


    private void showZipCodeOptions() {
        // Erstelle ein Array mit den verfügbaren PLZ-Optionen
        String[] zipCodes = {"49808", "49809", "49811"};

        // Erstelle einen AlertDialog, um die PLZ-Optionen anzuzeigen
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Postleitzahl auswählen")
                .setItems(zipCodes, (dialog, which) -> {
                    // Wenn eine Option ausgewählt wurde, setze den ausgewählten Wert im Button-Text
                    String selectedZip = zipCodes[which];
                    binding.zipButton.setText(selectedZip);
                    selectedZipCode = selectedZip; // Aktualisiere die ausgewählte Postleitzahl
                });

        // Zeige den AlertDialog
        builder.create().show();
    }


}
