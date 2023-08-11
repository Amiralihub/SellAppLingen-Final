package com.example.sellapplingen;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

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
        // Erstelle JSON-Objekt mit den Order-Daten
        JSONObject orderJson = new JSONObject();
        try {
            orderJson.put("token", order.getToken());
            orderJson.put("timestamp", order.getTimestamp());
            orderJson.put("employeeName", order.getEmployeeName());
            orderJson.put("firstName", order.getEmployeeName());
            orderJson.put("lastName", order.getEmployeeName());
            // Füge die anderen Order-Daten hinzu

            // Erstelle JSON-Web-Token (hardcodiert)
            String jsonWebToken = "dein_hardcodierter_token";

            // Füge das JSON-Web-Token dem JSON-Objekt hinzu
            orderJson.put("jsonWebToken", jsonWebToken);

            // Sende das JSON-Objekt an den Server (zum Beispiel mit einer HTTP-Anfrage)
            // Hier müsstest du deinen eigenen Code zur Serverkommunikation einfügen

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
