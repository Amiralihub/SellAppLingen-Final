package sellapp.fragments;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

    private boolean isEditingAddress = false;
    public String orderId;
    private String token;

    public DeliveryDetailsFragment()
    {
        // Required empty public constructor
    }

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

    private void navigateBackToPreviousFragment() {
        // Verwende die FragmentManagerHelper-Klasse, um zum vorherigen Fragment zurückzukehren
        customerapp.models.customerapp.FragmentManagerHelper.goBackToPreviousFragment(getFragmentManager());
    }


    private void sendOrderDataToServer()
    {

        CompletableFuture<String> sendOrderFuture = NetworkManager.sendPostRequest(NetworkManager.APIEndpoints.ORDER.getUrl(), order);
        String response = sendOrderFuture.join();

        try
        {
            if (response != null)
            {
                JSONObject responseJson = new JSONObject(response);
                orderId = responseJson.getString("orderID");
                showOrderIdPopup(orderId);
            } else
            {
                showErrorPopup("Fehler bei der Kommunikation mit dem Server");
            }
        } catch (JSONException e)
        {
            showErrorPopup("Fehler bei der Verarbeitung der Antwort");
            e.printStackTrace();
        }
    }

    private void showSuccessMessage()
    {
        requireActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Show the success message using a Toast on the main UI thread
                Toast.makeText(requireContext(), "Daten erfolgreich an den Server gesendet.", Toast.LENGTH_SHORT).show();

                // Here you switch to the ScannerFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layout, ScannerFragment.newInstance(order));
                transaction.commit();
            }
        });
    }

    // Füge diese Methode zum Anzeigen des Popups hinzu
    private void showOrderIdPopup(String orderId)
    {
        requireActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                View popupView = getLayoutInflater().inflate(R.layout.popup_success_deliverydetail, null);

                TextView successMessageTextView = popupView.findViewById(R.id.successMessageTextView);
                TextView orderIdTextView = popupView.findViewById(R.id.orderIdTextView);
                Button backToScannerButton = popupView.findViewById(R.id.backToScannerButton);

                orderIdTextView.setText("Bestellungs-ID: " + orderId);

                AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                        .setView(popupView)
                        .create();

                backToScannerButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        // Hier kannst du zum ScannerFragment wechseln
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.frame_layout, ScannerFragment.newInstance(order));
                        transaction.commit();
                    }
                });

                alertDialog.show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_delivery_details, container, false);

        Button confirmButton = view.findViewById(R.id.confirmButton);

        Button editAddressButton = view.findViewById(R.id.editAddressButton); // Button zum Bearbeiten der Adresse

        // Wenn der "Adresse bearbeiten" Button geklickt wird
        editAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Schritt 2: Setzen des Statusflags und Navigation zu ManualInputFragment
                isEditingAddress = true;
                navigateToManualInputFragment();
            }
        });



        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Generiere den Timestamp
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

        // Hole das Order-Objekt aus den Fragment-Argumenten
        Bundle args = getArguments();
        if (args != null && args.containsKey("order"))
        {
            order = (Order) args.getSerializable("order");
        }

        // Zeige die Order-Informationen in den entsprechenden TextViews an
        if (order != null)
        {
            createTestOrder();


            TextView employeeIdValue = view.findViewById(R.id.employeeIdValue);
            employeeIdValue.setText(order.getEmployeeName());

            TextView deliveryDateValue = view.findViewById(R.id.deliveryDateValue);
            deliveryDateValue.setText(order.getDeliveryDate());

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


            TextView streetNameValue = view.findViewById(R.id.streetNameValue);
            streetNameValue.setText(order.getRecipient().getAddress().getStreet() + " ");

            TextView housenNumberValue = view.findViewById(R.id.houseNumberValue);
            housenNumberValue.setText(order.getRecipient().getAddress().getHouseNumber());

            TextView cityLabel = view.findViewById(R.id.cityValue);
            cityLabel.setText(order.getRecipient().getAddress().getCity());

            TextView zipLabel = view.findViewById(R.id.zipValue);
            zipLabel.setText(order.getRecipient().getAddress().getZip() + " ");



            // Weitere TextViews für andere Order-Informationen hinzufügen
            // Hier kannst du weitere TextViews hinzufügen, um andere Order-Informationen anzuzeigen
            // Beispiel:
            // TextView additionalInfoValue = view.findViewById(R.id.additionalInfoValue);
            // additionalInfoValue.setText(order.getAdditionalInfo());
        }


        return view;
    }

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

    private void navigateToManualInputFragment() {
        ManualInputFragment manualInputFragment = new ManualInputFragment();
        manualInputFragment.setCurrentOrder(order);

        // Verwenden Sie die FragmentManagerHelper-Klasse für den Fragment-Übergang
        customerapp.models.customerapp.FragmentManagerHelper.replace(requireFragmentManager(), R.id.frame_layout, manualInputFragment);
    }





}