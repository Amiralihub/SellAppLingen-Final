package com.example.sellapplingen;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Order> orderList;


    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderID.setText(order.getOrderID());
        holder.recipient.setText(String.format("%s%s", order.getRecipient().getFirstName(), order.getRecipient().getLastName()));
        holder.deliveryDateRecipient.setText(order.getDeliveryDate());
        holder.openDetails.setOnClickListener(view -> goToOrderHistoryDetailsFragment(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, recipient, deliveryDateRecipient;
        LinearLayout openDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.orderID);
            recipient = itemView.findViewById(R.id.recipient);
            deliveryDateRecipient = itemView.findViewById(R.id.deliveryDateRecipient);
            openDetails = itemView.findViewById(R.id.openDetails);
        }
    }

    private void goToOrderHistoryDetailsFragment(Order selectedOrder) {
        OrderHistoryDetailsFragment fragment = new OrderHistoryDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_order", selectedOrder);
        fragment.setArguments(bundle);

        FragmentManagerHelper.goToFragment(
                ((AppCompatActivity) context).getSupportFragmentManager(),
                R.id.frame_layout,
                fragment,
                R.anim.slide_in_right,
                R.anim.slide_out,
                true
        );
    }

    public void filter(String query) {
        List<Order> filteredList = new ArrayList<>();
        for (Order order : orderList) {
            if (order.getRecipient().getFirstName().toLowerCase().contains(query.toLowerCase())
                    || order.getRecipient().getLastName().toLowerCase().contains(query.toLowerCase())
                    || order.getOrderID().toLowerCase().contains(query.toLowerCase())
                    || order.getDeliveryDate().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(order);
            }
        }
        this.orderList = filteredList;
        notifyDataSetChanged();
    }


}
