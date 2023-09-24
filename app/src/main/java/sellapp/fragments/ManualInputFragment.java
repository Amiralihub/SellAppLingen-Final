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

    private boolean isEditingAddress;

    private FragmentManualInputBinding binding;
    private Order currentOrder;
    private String selectedZipCode = "";
    private String[] zipCodes = {"49808", "49809", "49811"};

    public ManualInputFragment()
        {
        }

    public void setCurrentOrder(Order order)
        {
        currentOrder = order;
        }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
                            )
        {
        binding = FragmentManualInputBinding.inflate(inflater, container, false);
        setupViews();

        Bundle args = getArguments();
        if (args != null)
            {
            currentOrder = (Order) args.getSerializable("order");
            isEditingAddress = args.getBoolean("isEditingAddress", false);

            if (currentOrder != null && currentOrder.getRecipient() != null)
                {
                Recipient recipient = currentOrder.getRecipient();
                binding.firstNameEditText.setText(recipient.getFirstName());
                binding.lastNameEditText.setText(recipient.getLastName());

                if (recipient.getAddress() != null)
                    {
                    binding.streetEditText.setText(recipient.getAddress().getStreet());
                    binding.houseNumberEditText.setText(recipient.getAddress().getHouseNumber());
                    }
                }
            }

        return binding.getRoot();


        }

    /**
     * Initializes and sets up the views for the ManualInputFragment.
     * This method sets up the confirmation button click listener, initializes a Spinner with
     * three selectable zip codes, and handles the selection of a zip code.
     */
    private void setupViews()
        {
        binding.confirmManualInputButton.setOnClickListener(v -> saveManualInput());
        ArrayAdapter<String> zipAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, zipCodes);
        binding.zipSpinner.setAdapter(zipAdapter);

        binding.zipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
            @Override
            public void onItemSelected(
                    AdapterView<?> parentView, View selectedItemView, int position, long id
                                      )
                {
                selectedZipCode = zipCodes[position];
                }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
                {
                }
            });

        /**
         * TextWatcher to limit the maximum length of the input in the houseNumberEditText field.
         * This TextWatcher ensures that the user cannot enter more than 5 characters in the house number field.
         */
        binding.houseNumberEditText.addTextChangedListener(new TextWatcher()
            {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                if (charSequence.length() > 5)
                    {
                    binding.houseNumberEditText.setText(charSequence.subSequence(0, 5));
                    binding.houseNumberEditText.setSelection(5);
                    }
                }

            @Override
            public void afterTextChanged(Editable editable)
                {
                }
            });
        }

    /**
     * Save the manually entered order information.
     * Depending on the status flag 'isEditingAddress', this method either saves the edited address or
     * navigates back to the 'DeliveryDetailsFragment' after saving the order details.
     */
    private void saveManualInput()
        {
        if (isEditingAddress)
            {
            Address address = new Address(binding.streetEditText.getText().toString(),
                                          binding.houseNumberEditText.getText().toString(),
                                          selectedZipCode
            );
            Recipient recipient = new Recipient(binding.firstNameEditText.getText().toString(),
                                                binding.lastNameEditText.getText().toString(),
                                                address
            );
            currentOrder.setRecipient(recipient);
            isEditingAddress = false;
            navigateBackToDeliveryDetailsFragment();
            }
        else
            {

            if (isInputValid())
                {
                Address address = new Address(binding.streetEditText.getText().toString(),
                                              binding.houseNumberEditText.getText().toString(),
                                              selectedZipCode
                );
                Recipient recipient = new Recipient(binding.firstNameEditText.getText().toString(),
                                                    binding.lastNameEditText.getText().toString(),
                                                    address
                );
                currentOrder.setRecipient(recipient);
                Bundle args = new Bundle();
                args.putSerializable("order", currentOrder);

                HandlingInfoFragment handlingInfoFragment = new HandlingInfoFragment();
                handlingInfoFragment.setArguments(args);

                FragmentManager fragmentManager = requireFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(
                        R.id.frame_layout, handlingInfoFragment, "handlingInfoFragment");
                transaction.addToBackStack(null);
                transaction.commit();
                }
            else
                {
                Toast.makeText(
                             requireContext(), "Bitte füllen Sie alle Felder aus.", Toast.LENGTH_SHORT)
                     .show();
                }
            }
        }

    /**
     * Check if the input provided by the user is valid.
     *
     * @return True if all required fields are filled, otherwise false.
     */
    private boolean isInputValid()
        {
        String lastName = binding.lastNameEditText.getText().toString();
        String firstName = binding.firstNameEditText.getText().toString();
        String street = binding.streetEditText.getText().toString();
        String houseNumber = binding.houseNumberEditText.getText().toString();

        boolean isValid = true;


        if (firstName.trim().isEmpty() || !isNumeric(firstName.trim()))
            {
            isValid = false;
            binding.firstNameEditText.setError("Bitte geben Sie einen gültigen Vorname ein");
            }

        if (lastName.trim().isEmpty() || !isNumeric(lastName.trim()))
            {
            isValid = false;
            binding.lastNameEditText.setError("Bitte geben Sie einen gültigen Nachname ein");
            }

        if (street.trim().isEmpty() || !isNumeric(street.trim()))
            {
            isValid = false;
            binding.streetEditText.setError("Bitte geben Sie eine gültige Straße ein");
            }

        if (houseNumber.trim().isEmpty())
            {
            isValid = false;
            binding.houseNumberEditText.setError("Bitte geben Sie eine gültige Hausnummer ein");
            }

        return isValid &&
               !lastName.isEmpty() &&
               !firstName.isEmpty() &&
               !street.isEmpty() &&
               !houseNumber.isEmpty() &&
               !selectedZipCode.isEmpty();
        }

    private static boolean isNumeric(String str)
        {
        return str.matches("^[\\p{L} \\p{Zs}]+$");
        }


    /**
     * Navigate back to the 'DeliveryDetailsFragment'.
     * This method creates the 'DeliveryDetailsFragment' and sets the arguments for the fragment transition.
     */
    private void navigateBackToDeliveryDetailsFragment()
        {
        DeliveryDetailsFragment deliveryDetailsFragment = DeliveryDetailsFragment.newInstance(
                currentOrder);

        customerapp.models.customerapp.FragmentManagerHelper.replace(
                requireFragmentManager(), R.id.frame_layout, deliveryDetailsFragment);
        }

    }