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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_details, container, false);

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

            TextView packageCountValue = view.findViewById(R.id.packageCountValue);
            packageCountValue.setText(order.getNumberPackage());

            TextView packageSizeValue = view.findViewById(R.id.packageSizeValue);
            packageSizeValue.setText(order.getPackageSize());

            TextView actionInfoValue = view.findViewById(R.id.actionInfoValue);
            actionInfoValue.setText(order.getHandlingInfo());

            TextView deliveryDateValue = view.findViewById(R.id.deliveryDateValue);
            deliveryDateValue.setText(order.getDeliveryDate());

            TextView deliveryAddressValue = view.findViewById(R.id.deliveryAddressValue);
            String deliveryAddress = order.getStreet() + " " + order.getHouseNumber() + ", " +
                    order.getZip() + " " + order.getCity();
            deliveryAddressValue.setText(deliveryAddress);

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
