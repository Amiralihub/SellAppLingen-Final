package com.example.sellapplingen;

import static com.example.sellapplingen.FragmentManagerHelper.goBackToPreviousFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class OrderHistoryDetailsFragment extends Fragment {
    private PlacedOrder order;
    TextView deliveryDetailsTextView, addressDetailsTextView;
    TextView orderNumberTextView, recipientOrderNumberTextView;
    TextView orderDateTextView, recipientOrderDateTextView;
    TextView employeeTextView, employeeOrderTextView;
    TextView orderPackageTextView, recipientOrderPackageTextView;
    TextView orderDropPlaceTextView, recipientOrderDropPlaceTextView;
    TextView recipientTextView, recipientOrderTextView;
    TextView recipientStreetTextView, orderStreetTextView;
    TextView recipientCityTextView, orderCityTextView;
    ImageView deliveryImageView, homeImageView;
    ImageButton backToHistoryButton;

    public OrderHistoryDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history_details, container, false);
        if (getArguments() != null) {
            order = (PlacedOrder) getArguments().getSerializable("selected_order");
        }

        deliveryDetailsTextView = view.findViewById(R.id.deliveryDetailsTextView);
        addressDetailsTextView = view.findViewById(R.id.addressDetailsTextView);
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView);
        homeImageView = view.findViewById(R.id.homeImageView);
        recipientOrderNumberTextView = view.findViewById(R.id.recipientOrderNumberTextView);
        orderDateTextView = view.findViewById(R.id.orderDateTextView);
        recipientOrderDateTextView = view.findViewById(R.id.recipientOrderDateTextView);
        employeeTextView = view.findViewById(R.id.employeeTextView);
        employeeOrderTextView = view.findViewById(R.id.employeeOrderTextView);
        orderPackageTextView = view.findViewById(R.id.orderPackageTextView);
        recipientOrderPackageTextView = view.findViewById(R.id.recipientOrderPackageTextView);
        orderDropPlaceTextView = view.findViewById(R.id.orderDropPlaceTextView);
        recipientOrderDropPlaceTextView = view.findViewById(R.id.recipientOrderDropPlaceTextView);
        recipientTextView = view.findViewById(R.id.recipientTextView);
        recipientOrderTextView = view.findViewById(R.id.recipientOrderTextView);
        recipientStreetTextView = view.findViewById(R.id.recipientStreetTextView);
        orderStreetTextView = view.findViewById(R.id.orderStreetTextView);
        recipientCityTextView = view.findViewById(R.id.recipientCityTextView);
        orderCityTextView = view.findViewById(R.id.orderCityTextView);
        deliveryImageView = view.findViewById(R.id.deliveryImageView);


        if (order != null) {
            orderNumberTextView.setText(order.getOrderID());
            orderDateTextView.setText(order.getDeliveryDate());
            employeeTextView.setText(order.getEmployeeName());
            orderPackageTextView.setText(order.getPackageSize());
            orderDropPlaceTextView.setText(order.getCustomDropOffPlace());
            recipientTextView.setText(String.format("%s%s", order.getFirstName(), order.getLastName()));
            recipientStreetTextView.setText(String.format("%s%s", order.getStreet(), order.getHouseNumber()));
            recipientCityTextView.setText(order.getZip());
        }


        backToHistoryButton = view.findViewById(R.id.backToHistoryButton);
        backToHistoryButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            goBackToPreviousFragment(fragmentManager);
        });

        return view;
    }


}