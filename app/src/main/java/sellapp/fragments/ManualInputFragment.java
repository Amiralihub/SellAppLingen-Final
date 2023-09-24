package sellapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sellapp.models.Address;
import sellapp.models.Order;

import com.example.sellapplingen.R;

import sellapp.models.Recipient;

import com.example.sellapplingen.databinding.FragmentManualInputBinding;

public class ManualInputFragment extends Fragment
    {

    private boolean isEditingAddress = false;

    private FragmentManualInputBinding binding;
    private Order currentOrder;
    private String selectedZipCode = "";
    private String[] zipCodes = {"49808", "49809", "49811"}; // Array der verfügbaren PLZ-Optionen

    public ManualInputFragment()
        {
        // Required empty public constructor
        }

    public void setCurrentOrder(Order order)
        {
        currentOrder = order;
        }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
                            )
        {
        binding = FragmentManualInputBinding.inflate(inflater, container, false);
        setupViews();

        return binding.getRoot();


        }


    private void goBackToPreviousFragment()
        {
        // Hier können Sie den Code hinzufügen, um zum vorherigen Fragment zurückzukehren
        // Zum Beispiel, indem Sie die FragmentTransaction verwenden
        FragmentManager fragmentManager = requireFragmentManager();
        fragmentManager.popBackStack(); // Dies entfernt das aktuelle Fragment und kehrt zum vorherigen zurück
        }

    private void setupViews()
        {
        binding.confirmManualInputButton.setOnClickListener(v -> saveManualInput());

        // Initialisiere den Spinner
        ArrayAdapter<String> zipAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, zipCodes);
        binding.zipSpinner.setAdapter(zipAdapter);

        binding.zipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
            @Override
            public void onItemSelected(
                    AdapterView<?> parentView,
                    View selectedItemView,
                    int position,
                    long id
                                      )
                {
                // Setze den ausgewählten Wert im Spinner
                selectedZipCode = zipCodes[position];
                }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
                {
                // Handle nichts ausgewählt
                }
            });

        // Fügen Sie den TextWatcher zum houseNumberEditText hinzu
        binding.houseNumberEditText.addTextChangedListener(new TextWatcher()
            {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                // Nicht benötigt, vor der Textänderung
                }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                // Überprüfen Sie die Länge der eingegebenen Hausnummer
                if (charSequence.length() > 5)
                    {
                    // Schneiden Sie den Text auf 5 Zeichen ab
                    binding.houseNumberEditText.setText(charSequence.subSequence(0, 5));
                    binding.houseNumberEditText.setSelection(5); // Setzen Sie den Cursor am Ende
                    }
                }

            @Override
            public void afterTextChanged(Editable editable)
                {
                // Nicht benötigt, nach der Textänderung
                }
            });
        }
    private void saveManualInput()
        {
        // Schritt 3: Überprüfen des Statusflags isEditingAddress
        if (isEditingAddress)
            {
            // Speichern Sie die bearbeitete Adresse in Ihren Datenstrukturen (z. B. order)
            Address address = new Address(
                    binding.streetEditText.getText().toString(),
                    binding.houseNumberEditText.getText().toString(), selectedZipCode
            );
            Recipient recipient = new Recipient(
                    binding.firstNameEditText.getText().toString(),
                    binding.lastNameEditText.getText().toString(), address
            );
            currentOrder.setRecipient(recipient);

            // Schritt 4: Zurücksetzen des Statusflags
            isEditingAddress = false;

            // Schritt 5: Navigieren Sie zurück zu DeliveryDetailsFragment
            navigateBackToDeliveryDetailsFragment();
            }
        else
            {
            // Normaler Ablauf - speichern Sie die Daten wie zuvor
            // Überprüfen Sie, ob alle Felder ausgefüllt sind
            if (isInputValid())
                {
                // Speichern Sie die manuell eingegebenen Order-Informationen im currentOrder-Objekt
                Address address = new Address(
                        binding.streetEditText.getText().toString(),
                        binding.houseNumberEditText.getText().toString(), selectedZipCode
                );
                Recipient recipient = new Recipient(
                        binding.firstNameEditText.getText().toString(),
                        binding.lastNameEditText.getText().toString(), address
                );
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
                transaction.replace(
                        R.id.frame_layout, handlingInfoFragment, "handlingInfoFragment");
                transaction.addToBackStack(null); // Füge das Fragment zur Rückwärtsnavigation hinzu
                transaction.commit();
                }
            else
                {
                // Zeige eine Benachrichtigung, wenn nicht alle Felder ausgefüllt sind
                Toast.makeText(
                             requireContext(), "Bitte füllen Sie alle Felder aus.", Toast.LENGTH_SHORT)
                     .show();
                }
            }
        }


    private boolean isInputValid()
        {
        String lastName = binding.lastNameEditText.getText().toString();
        String firstName = binding.firstNameEditText.getText().toString();
        String street = binding.streetEditText.getText().toString();
        String houseNumber = binding.houseNumberEditText.getText().toString();

        // Überprüfe, ob die Felder ausgefüllt sind oder Daten eingegeben wurden
        return !lastName.isEmpty() &&
               !firstName.isEmpty() &&
               !street.isEmpty() &&
               !houseNumber.isEmpty() &&
               !selectedZipCode.isEmpty();
        }

    private void navigateBackToDeliveryDetailsFragment()
        {
        // Erstellen Sie das DeliveryDetailsFragment und setzen Sie die Argumente
        DeliveryDetailsFragment deliveryDetailsFragment = DeliveryDetailsFragment.newInstance(
                currentOrder);

        // Verwenden Sie die FragmentManagerHelper-Klasse für den Fragment-Übergang
        customerapp.models.customerapp.FragmentManagerHelper.replace(
                requireFragmentManager(), R.id.frame_layout, deliveryDetailsFragment);
        }

    }