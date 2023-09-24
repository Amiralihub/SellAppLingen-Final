package sellapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sellapp.models.NetworkManager;
import sellapp.models.Order;

import com.example.sellapplingen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class DeliveryDetailsFragment extends Fragment
    {
    private Order order;
    public String orderId;
    private String token;

    private boolean isEditingAddress = false;

    public DeliveryDetailsFragment()
        {

        }


        /**
         * Create a new instance of DeliveryDetailsFragment with the given order data.
         *
         * @param order The order to be displayed in this fragment.
         * @return A new instance of DeliveryDetailsFragment with the provided order data.
         */
    public static DeliveryDetailsFragment newInstance(Order order)
        {
        DeliveryDetailsFragment fragment = new DeliveryDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        fragment.setArguments(args);
        return fragment;
        }

    private Order createTestOrder()
        {
        Order testOrder = new Order();
        testOrder.setOrderID("Test-Token");
        testOrder.setTimestamp("2023-08-03 12:34:56");
        return testOrder;
        }

        /**
         * Sends the order data to the server and handles the server's response.
         * This method performs an asynchronous POST request to the server to submit the order data
         * and processes the server's response, displaying the order ID if successful or showing
         * an error message in case of communication or response processing issues.
         */
    private void sendOrderDataToServer()
        {

        CompletableFuture<String> sendOrderFuture = NetworkManager.sendPostRequest(
                NetworkManager.APIEndpoints.ORDER.getUrl(), order);
        String response = sendOrderFuture.join();

        try
            {
            if (response != null)
                {
                JSONObject responseJson = new JSONObject(response);
                orderId = responseJson.getString("orderID");
                showOrderIdPopup(orderId);
                }
            else
                {
                showErrorPopup("Fehler bei der Kommunikation mit dem Server");
                }
            } catch (JSONException e)
            {
            showErrorPopup("Fehler bei der Verarbeitung der Antwort");
            e.printStackTrace();
            }
        }

        /**
         * Displays a popup dialog showing the order ID and an option to navigate back to the ScannerFragment.
         *
         * @param orderId The order ID to display in the popup.
         */
    private void showOrderIdPopup(String orderId)
        {
        requireActivity().runOnUiThread(new Runnable()
            {
            @Override
            public void run()
                {
                View popupView = getLayoutInflater().inflate(
                        R.layout.popup_success_deliverydetail, null);

                TextView successMessageTextView = popupView.findViewById(
                        R.id.successMessageTextView);
                TextView orderIdTextView = popupView.findViewById(R.id.orderIdTextView);
                Button backToScannerButton = popupView.findViewById(R.id.backToScannerButton);

                orderIdTextView.setText("Bestellungs-ID: " + orderId);

                AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setView(
                        popupView).create();

                backToScannerButton.setOnClickListener(new View.OnClickListener()
                    {
                    @Override
                    public void onClick(View v)
                        {
                        alertDialog.dismiss();
                        // Hier kannst du zum ScannerFragment wechseln
                        FragmentManager fragmentManager
                                = requireActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.frame_layout, ScannerFragment.newInstance(order));
                        transaction.commit();
                        }
                    });

                alertDialog.show();
                }
            });
        }

        /**
         * Called to create and return the view hierarchy associated with the fragment.
         *
         * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
         * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
         * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
         * @return The root view of the fragment's layout.
         */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
                            )
        {
        View view = inflater.inflate(R.layout.fragment_delivery_details, container, false);

        Button confirmButton = view.findViewById(R.id.confirmButton);

        Button editButton = view.findViewById(R.id.editAddressButton);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Schritt 1: Setzen des Statusflags und Navigation zu ManualInputFragment
                    isEditingAddress = true;
                    System.out.println(isEditingAddress);
                    navigateToManualInputFragment();
                }
            });

            /**
             * Click event handler for the "Confirm" button. It generates a timestamp, sets it in the order object,
             * and sends the order data to the server.
             *
             * @param v The View that was clicked (the "Confirm" button).
             */
        confirmButton.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View v)
                {

                String myFormat = "dd-MM-yyyy:HH-mm-ss.SSS";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                String getTime = dateFormat.format(Calendar.getInstance().getTime());

                if (order != null)
                    {
                    order.setTimestamp(getTime);
                    }

                sendOrderDataToServer();
                }
            });

            /**
             * Inflates the layout for the DeliveryDetailsFragment and initializes UI components
             * with order information if available.
             *
             * @param inflater           The LayoutInflater object that can be used to inflate
             *                           any views in the fragment.
             * @param container          If non-null, this is the parent view that the fragment's
             *                           UI should be attached to. The fragment should not add the view itself,
             *                           but this can be used to generate the LayoutParams of the view.
             * @param savedInstanceState A saved instance state if available, null otherwise.
             * @return The root View for the fragment's UI or null.
             */
        Bundle args = getArguments();
        if (args != null && args.containsKey("order"))
            {
            order = (Order) args.getSerializable("order");
            }

        if (order != null)
            {
            createTestOrder();


            TextView employeeIdValue = view.findViewById(R.id.employeeIdValue);
            employeeIdValue.setText(order.getEmployeeName());

            TextView firstNameValue = view.findViewById(R.id.firstNameValue);
            firstNameValue.setText(order.getRecipient().getFirstName() + " ");

            TextView lastNameValue = view.findViewById(R.id.lastNameValue);
            lastNameValue.setText(order.getRecipient().getLastName());

            TextView packageSizeValue = view.findViewById(R.id.packageSizeValue);
            packageSizeValue.setText(order.getPackageSize());

            TextView actionInfoValue = view.findViewById(R.id.actionInfoValue);
            actionInfoValue.setText(order.getHandlingInfo());

            TextView customDropOffValue = view.findViewById(R.id.customDropOffValue);
            customDropOffValue.setText(order.getCustomDropOffPlace());

            TextView deliveryDateValue = view.findViewById(R.id.deliveryDateValue);
            deliveryDateValue.setText(order.getDeliveryDate());

            TextView streetNameValue = view.findViewById(R.id.streetNameValue);
            streetNameValue.setText(order.getRecipient().getAddress().getStreet() + " ");

            TextView housenNumberValue = view.findViewById(R.id.houseNumberValue);
            housenNumberValue.setText(order.getRecipient().getAddress().getHouseNumber());

            TextView cityLabel = view.findViewById(R.id.cityValue);
            cityLabel.setText(order.getRecipient().getAddress().getCity());

            TextView zipLabel = view.findViewById(R.id.zipValue);
            zipLabel.setText(order.getRecipient().getAddress().getZip() + " ");

            }

        return view;
        }

        /**
         * Displays an error message in the form of a Toast on the UI thread.
         *
         * @param msg The error message to display.
         */
    private void showErrorPopup(String msg)
        {
        requireActivity().runOnUiThread(new Runnable()
            {
            @Override
            public void run()
                {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * Navigates to the ManualInputFragment, allowing the user to manually input address details.
         * Sets the necessary arguments for the ManualInputFragment and adds it to the back stack for navigation history.
         */
        private void navigateToManualInputFragment() {
            FragmentManager fragmentManager = requireFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Create a new instance of ManualInputFragment
            ManualInputFragment manualInputFragment = new ManualInputFragment();

            // Set the arguments for the ManualInputFragment, including the order and editing status
            Bundle args = new Bundle();
            args.putSerializable("order", order);
            args.putBoolean("isEditingAddress", isEditingAddress);
            manualInputFragment.setArguments(args);

            // Replace the current fragment with the ManualInputFragment
            customerapp.models.customerapp.FragmentManagerHelper.replace(
                    requireFragmentManager(), R.id.frame_layout, manualInputFragment);

            // Add the transaction to the back stack for navigation history
            transaction.addToBackStack(null);
            transaction.commit();
        }




    }