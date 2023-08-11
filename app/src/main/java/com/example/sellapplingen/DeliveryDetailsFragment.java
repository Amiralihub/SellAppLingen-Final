package com.example.sellapplingen;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeliveryDetailsFragment extends Fragment {




    private Order order;

    public DeliveryDetailsFragment() {
        // Required empty public constructor
    }

    public static DeliveryDetailsFragment newInstance(Order order) {
        DeliveryDetailsFragment fragment = new DeliveryDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        fragment.setArguments(args);
        return fragment;
    }
    private Order createTestOrder() {
        Order testOrder = new Order();
        testOrder.setToken("Test-Token");
        testOrder.setTimestamp("2023-08-03 12:34:56");
        return testOrder;
    }

    private void sendOrderDataToServer() {
        // Erstelle die Zeichenkette mit den Order-Daten im gewünschten Format
        String orderDataString = order.getToken() + "&" +
                order.getTimestamp() + "&" +
                order.getEmployeeName() + "&" +
                order.getFirstName() + "&" +
                order.getLastName() + "&" +
                order.getStreet() + "&" +
                order.getHouseNumber() + "&" +
                order.getZip() + "&" +
                order.getCity() + "&" +
                order.getPackageSize() + "&" +
                order.getHandlingInfo() + "&" +
                order.getDeliveryDate();

        // Erstelle JSON-Web-Token (hardcodiert)
        String jsonWebToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdG9yZUlEIjo1LCJzdG9yZU5hbWUiOiJUYWtlMiIsIm93bmVyIjoiU2FkaWsiLCJsb2dvIjoiODljZDI4M2EtOGFmZC00NGUwLTkwYmYtZDAxNzJhNzU5Y2EwIiwidGVsZXBob25lIjoiMDE3NjMyMjU0MTM2IiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsImlhdCI6MTY5MTc0OTg5MSwic3ViIjoiYXV0aF90b2tlbiJ9.SDmBJs2yV6g5hOk0ZgziCf4Vli5V-cigNKVMclFvTNw";

        // Kombiniere Order-Daten und JSON-Web-Token
        final String combinedData = orderDataString + "&" + jsonWebToken;

        // Verwende die kombinierten Daten innerhalb der inneren Klasse
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://131.173.65.77:8080/api/order");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Verwende die kombinierten Daten innerhalb der inneren Klasse
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(combinedData);
                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        // Erfolgsmeldung oder weitere Aktionen hier
                        showSuccessMessage();
                    } else {
                        // Zeige eine Fehlermeldung, wenn die Verbindung nicht erfolgreich war
                        showErrorMessage();
                    }

                    conn.disconnect();
                } catch (IOException e) {
                    // Zeige eine Fehlermeldung, wenn eine IOException auftritt
                    e.printStackTrace();
                    showErrorMessage();
                }
            }
        });

        thread.start();
    }

    private void showSuccessMessage() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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

    private void showErrorMessage() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Show the error message using a Toast on the main UI thread
                Toast.makeText(requireContext(), "Keine Verbindung zum Server.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_details, container, false);

        Button confirmButton = view.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrderDataToServer();
            }
        });



        // Hole das Order-Objekt aus den Fragment-Argumenten
        Bundle args = getArguments();
        if (args != null && args.containsKey("order")) {
            order = (Order) args.getSerializable("order");
        }

        // Zeige die Order-Informationen in den entsprechenden TextViews an
        if (order != null) {
            createTestOrder();
            TextView tokenValue = view.findViewById(R.id.tokenValue);
            tokenValue.setText(order.getToken());

            TextView timestampValue = view.findViewById(R.id.timestampValue);
            timestampValue.setText(order.getTimestamp());

            TextView employeeIdValue = view.findViewById(R.id.employeeIdValue);
            employeeIdValue.setText(order.getEmployeeName());

            TextView firstNameValue = view.findViewById(R.id.firstNameValue);
            firstNameValue.setText(order.getFirstName());

            TextView lastNameValue = view.findViewById(R.id.lastNameValue);
            lastNameValue.setText(order.getLastName());

            TextView packageSizeValue = view.findViewById(R.id.packageSizeValue);
            packageSizeValue.setText(order.getPackageSize());

            TextView actionInfoValue = view.findViewById(R.id.actionInfoValue);
            actionInfoValue.setText(order.getHandlingInfo());

            TextView deliveryDateValue = view.findViewById(R.id.deliveryDateValue);
            deliveryDateValue.setText(order.getDeliveryDate());

            TextView streetNameValue = view.findViewById(R.id.streetNameValue);
            streetNameValue.setText(order.getStreet());


            TextView housenNumberValue = view.findViewById(R.id.houseNumberValue);
            housenNumberValue.setText(order.getHouseNumber());

            TextView cityLabel = view.findViewById(R.id.cityValue);
            cityLabel.setText(order.getCity());

            TextView zipLabel = view.findViewById(R.id.zipValue);
            zipLabel.setText(order.getZip());

            // Weitere TextViews für andere Order-Informationen hinzufügen
            // Hier kannst du weitere TextViews hinzufügen, um andere Order-Informationen anzuzeigen
            // Beispiel:
            // TextView additionalInfoValue = view.findViewById(R.id.additionalInfoValue);
            // additionalInfoValue.setText(order.getAdditionalInfo());
        }

        return view;
    }



}
