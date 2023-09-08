package sellapp.fragments;

import static customerapp.models.customerapp.FragmentManagerHelper.goBackToPreviousFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.sellapplingen.R;

import sellapp.models.Order;


public class OrderHistoryDetailsFragment extends Fragment
{
    private Order order;
    public TextView deliveryDetailsTextView;
    public TextView addressDetailsTextView;
    public TextView orderNumberTextView;
    public TextView recipientOrderNumberTextView;
    public TextView orderDateTextView;
    public TextView recipientOrderDateTextView;
    public TextView employeeTextView;
    public TextView employeeOrderTextView;
    public TextView orderPackageTextView;
    public TextView recipientOrderPackageTextView;
    public TextView orderDropPlaceTextView;
    public TextView recipientOrderDropPlaceTextView;
    public TextView recipientTextView;
    public TextView recipientOrderTextView;
    public TextView recipientOrderDropPlaceDateTextView;
    public TextView recipientStreetTextView;
    public TextView orderStreetTextView;
    public TextView recipientCityTextView;
    public TextView orderCityTextView;
    public ImageView deliveryImageView;
    public ImageView homeImageView;
    public ImageButton backToHistoryButton;

    public OrderHistoryDetailsFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_order_history_details, container, false);
        if (getArguments() != null)
        {
            order = (Order) getArguments().getSerializable("selected_order");
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
        recipientOrderDropPlaceDateTextView = view.findViewById(R.id.recipientOrderDropPlaceDateTextView);


        if (order != null)
        {
            recipientOrderNumberTextView.setText(order.getOrderID());
            recipientOrderDateTextView.setText(dateFormater(order.getTimestamp()));
            recipientOrderDropPlaceDateTextView.setText(dateFormater(order.getDeliveryDate()));
            employeeOrderTextView.setText(order.getEmployeeName());
            recipientOrderPackageTextView.setText(order.getPackageSize());
            recipientOrderDropPlaceTextView.setText(order.getCustomDropOffPlace());
            recipientOrderTextView.setText(order.getRecipient().getFirstName() + " " + order.getRecipient().getLastName());
            orderStreetTextView.setText(order.getRecipient().getAddress().getStreet() + " " + order.getRecipient().getAddress().getHouseNumber());
            orderCityTextView.setText(order.getRecipient().getAddress().getZip() + " Lingen");
        }


        backToHistoryButton = view.findViewById(R.id.backToHistoryButton);
        backToHistoryButton.setOnClickListener(v ->
        {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            goBackToPreviousFragment(fragmentManager);
        });

        return view;
    }

    public String dateFormater(String update)
    {
        String newFormat [] = update.split(":");
        return newFormat[0];
    }

}