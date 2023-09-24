package sellapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sellapp.models.Order;
import com.example.sellapplingen.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HandlingInfoFragment extends Fragment
{
    private CheckBox chkOption1;
    private CheckBox chkOption2;
    private CheckBox chkOption3;
    private CheckBox chkOption4;
    private CheckBox chkOption5;
    private CheckBox S;
    private CheckBox M;
    private CheckBox L;
    private CheckBox XL;
    private Button confirmButton;
    private Button backToScannerFragmentButton;
    final Calendar myCalendar = Calendar.getInstance();
    private EditText reciptname;
    private final StringBuilder selectedInfo = new StringBuilder();
    private final StringBuilder packageSizeInfo = new StringBuilder();

    private EditText customDropOffEditText;
    private Order order;
    private Button date;
    private Button time;
    private String setDate = "";
    private String getTime = "";
    private Order clientInfo;

    /**
     * Creates a CompoundButton.OnCheckedChangeListener that handles the selection of a specific package size.
     *
     * @param size             The package size associated with this listener.
     * @param packageSizeInfo  The StringBuilder used to track selected package sizes.
     * @param otherCheckBoxes  Other CheckBoxes to be unchecked when this one is checked.
     * @return A CompoundButton.OnCheckedChangeListener instance for package size selection.
     */
    private CompoundButton.OnCheckedChangeListener createCheckedChangeListener(String size, StringBuilder packageSizeInfo, CheckBox... otherCheckBoxes)
    {
        return (buttonView, isChecked) ->
        {
            if (isChecked)
            {
                if (packageSizeInfo.indexOf(size) == -1)
                {
                    packageSizeInfo.append(size);
                }
                for (CheckBox checkBox : otherCheckBoxes)
                {
                    checkBox.setChecked(false);
                }
            } else
            {
                int index = packageSizeInfo.indexOf(size);
                if (index != -1)
                {
                    packageSizeInfo.delete(index, index + size.length());
                }
            }
        };
    }

    /**
     * Creates a CompoundButton.OnCheckedChangeListener for handling the selection of a specific option text.
     *
     * @param optionText The text associated with the option to be selected.
     * @return A CompoundButton.OnCheckedChangeListener instance for option selection.
     */
    private CompoundButton.OnCheckedChangeListener createCheckedChangeListener(String optionText)
    {
        return (buttonView, isChecked) ->
        {
            if (isChecked)
            {
                if (selectedInfo.indexOf(optionText) == -1)
                {
                    selectedInfo.append(optionText).append("&");
                }
                if (chkOption4.isChecked())
                {
                    chkOption4.setChecked(false);
                }
            } else
            {
                int startIndex = selectedInfo.indexOf(optionText);
                if (startIndex != -1)
                {
                    int endIndex = startIndex + optionText.length();
                    selectedInfo.replace(startIndex, endIndex + 1, "");
                }
            }
        };
    }



    public HandlingInfoFragment()
    {
    }

    /**
     * Called when the fragment's UI is being created. Inflates the layout for this fragment and initializes UI elements.
     * Set up listeners for package size checkboxes.
     * Find and initialize checkboxes.
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The saved state of the fragment (not used in this case).
     * @return A View representing the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_handling_info, container, false);

        reciptname = view.findViewById(R.id.recipientNameEditText);
        EditText customDropOffEditText = view.findViewById(R.id.customDropOffEditText);

        date = view.findViewById(R.id.calendarView);

        S = view.findViewById(R.id.small);
        M = view.findViewById(R.id.medium);
        L = view.findViewById(R.id.large);
        XL = view.findViewById(R.id.xlarge);

        // Set up listeners for package size checkboxes

        S.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                if (packageSizeInfo.indexOf("S") == -1)
                {
                    packageSizeInfo.append("S");
                }
                M.setChecked(false);
                L.setChecked(false);
                XL.setChecked(false);
            } else
            {
                int index = packageSizeInfo.indexOf("S");
                if (index != -1)
                {
                    packageSizeInfo.delete(index, index + 1);
                }
            }
        });

        M.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                if (packageSizeInfo.indexOf("M") == -1)
                {
                    packageSizeInfo.append("M");
                }
                S.setChecked(false);
                L.setChecked(false);
                XL.setChecked(false);
            } else
            {
                int index = packageSizeInfo.indexOf("M");
                if (index != -1)
                {
                    packageSizeInfo.delete(index, index + 1);
                }
            }
        });

        L.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                if (packageSizeInfo.indexOf("L") == -1)
                {
                    packageSizeInfo.append("L");
                }
                S.setChecked(false);
                M.setChecked(false);
                XL.setChecked(false);
            } else
            {
                int index = packageSizeInfo.indexOf("L");
                if (index != -1)
                {
                    packageSizeInfo.delete(index, index + 1);
                }
            }
        });

        XL.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                if (packageSizeInfo.indexOf("XL") == -1)
                {
                    packageSizeInfo.append("XL");
                }
                S.setChecked(false);
                M.setChecked(false);
                L.setChecked(false);
            } else
            {
                int index = packageSizeInfo.indexOf("XL");
                if (index != -1)
                {
                    packageSizeInfo.delete(index, index + 2);
                }
            }
        });

        chkOption1 = view.findViewById(R.id.fluentOption);
        chkOption2 = view.findViewById(R.id.fragileOption);
        chkOption3 = view.findViewById(R.id.glasOption);
        chkOption4 = view.findViewById(R.id.noOption); // Assuming you have this checkbox in your layout
        chkOption5 = view.findViewById(R.id.heavy);
        confirmButton = view.findViewById(R.id.confirmButton);

        /**
         * OnClickListener for the date selection button.
         *
         * This method displays a DatePickerDialog allowing the user to select a date for delivery.
         * It also handles validation to ensure that the selected date is not in the past and not on the same day after 13:00.
         * The selected date is then formatted and displayed in the date TextView.
         *
         * @param v The View that was clicked (the date selection button).
         */
        date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day)
                    {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, day);
                        Calendar todayCalendar = Calendar.getInstance();

                        if (selectedCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
                                && selectedCalendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH)
                                && selectedCalendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH)
                                && todayCalendar.get(Calendar.HOUR_OF_DAY) >= 13)
                        {
                            Toast.makeText(requireContext(), "Der aktuelle Tag ist nach 13:00 Uhr nicht mehr auswählbar.", Toast.LENGTH_SHORT).show();
                        } else if (selectedCalendar.before(todayCalendar))
                        {
                            Toast.makeText(requireContext(), "Bitte wählen Sie ein zukünftiges Datum aus.", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, month);
                            myCalendar.set(Calendar.DAY_OF_MONTH, day);

                            String myFormat = "yyyy-MM-dd"; // Ändere das Format zu "dd-MM-yyyy"
                            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                            setDate = dateFormat.format(myCalendar.getTime());
                            date.setText(setDate);
                        }
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                datePickerDialog.show();
            }

        });

        S.setOnCheckedChangeListener(createCheckedChangeListener("S", packageSizeInfo, M, L, XL));

        M.setOnCheckedChangeListener(createCheckedChangeListener("M", packageSizeInfo, S, L, XL));

        L.setOnCheckedChangeListener(createCheckedChangeListener("L", packageSizeInfo, S, M, XL));

        XL.setOnCheckedChangeListener(createCheckedChangeListener("XL", packageSizeInfo, S, M, L));

        chkOption1.setOnCheckedChangeListener(createCheckedChangeListener("Flüssig"));
        chkOption2.setOnCheckedChangeListener(createCheckedChangeListener("Zerbrechlich"));
        chkOption3.setOnCheckedChangeListener(createCheckedChangeListener("Glas"));
        chkOption4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                selectedInfo.setLength(0); // Entferne alle anderen ausgewählten Handlungsinformationen
                selectedInfo.append("Keine besondere Eigenschaft").append("&");

                chkOption1.setChecked(false);
                chkOption2.setChecked(false);
                chkOption3.setChecked(false);
                chkOption5.setChecked(false);
            } else
            {
                selectedInfo.replace(selectedInfo.indexOf("Keine besondere Eigenschaft"), selectedInfo.indexOf("Keine besondere Eigenschaft") + "Keine besondere Eigenschaft".length() + 1, "");
            }
            boolean keineBesondereEigenschaftSelected = selectedInfo.indexOf("Keine besondere Eigenschaft") != -1;
            chkOption1.setEnabled(!keineBesondereEigenschaftSelected);
            chkOption2.setEnabled(!keineBesondereEigenschaftSelected);
            chkOption3.setEnabled(!keineBesondereEigenschaftSelected);
            chkOption5.setEnabled(!keineBesondereEigenschaftSelected);
        });
        chkOption5.setOnCheckedChangeListener(createCheckedChangeListener("Schwer"));

        /**
         * Sets an OnClickListener for the confirmation button. When the button is clicked, this method performs several checks
         * and updates the client information with selected data. If all required fields are filled, it navigates to the
         * DeliveryDetailsFragment and passes the client information.
         */
        confirmButton.setOnClickListener(v ->
        {
            String customDropOffPlace = customDropOffEditText.getText().toString();
            if (packageSizeInfo.toString().isEmpty() || selectedInfo.toString().isEmpty() || setDate.isEmpty())
            {
                Toast.makeText(requireContext(), "Bitte füllen Sie alle erforderlichen Felder aus.", Toast.LENGTH_SHORT).show();
            } else
            {

                clientInfo.setEmployeeName(reciptname.getText().toString());

                clientInfo.setPackageSize(packageSizeInfo.toString());
                if (selectedInfo.length() > 0)
                {
                    selectedInfo.setLength(selectedInfo.length() - 1);
                    clientInfo.setHandlingInfo(selectedInfo.toString());
                } else
                {
                    clientInfo.setHandlingInfo("");
                }
                String myFormat = "hh:mm";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                String getTime = dateFormat.format(Calendar.getInstance().getTime());

                clientInfo.setHandlingInfo(selectedInfo.toString());
                clientInfo.setCustomDropOffPlace(customDropOffPlace);
                clientInfo.setDeliveryDate(setDate);
                clientInfo.setTimestamp(getTime);
                String info = selectedInfo.toString();
                System.out.println(customDropOffPlace);
                Log.i("tariq", "onCreateView: " + info + "\n" + packageSizeInfo + "\n" + setDate);
                FragmentManager fragmentManager = requireFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();


                DeliveryDetailsFragment deliveryDetailsFragment = new DeliveryDetailsFragment();
                Bundle args = new Bundle();
                args.putSerializable("order", clientInfo);
                deliveryDetailsFragment.setArguments(args);
                transaction.replace(R.id.frame_layout, deliveryDetailsFragment, "deliveryDetailsFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        if (getArguments() != null)
        {
            clientInfo = (Order) getArguments().getSerializable("order");
        }
        Log.i("tariq", "onCreateView: check " + clientInfo);

        return view;
    }


}