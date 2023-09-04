package com.example.sellapplingen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OrderHistoryFragment extends Fragment {
    private ArrayList<Order> placedOrders;
    private OrderHistoryAdapter orderHistoryAdapter;
    EditText serachOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placedOrders = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(requireContext(), placedOrders);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewQRCodeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderHistoryAdapter = new OrderHistoryAdapter(requireContext(), placedOrders);
        recyclerView.setAdapter(orderHistoryAdapter);

        //TODO: refactor
        if (isNetworkAvailable()) {
            executorService.execute(() -> {
                ArrayList<Order> result = downloadData("http://131.173.65.77:8080/api/allOrders");
                if (result != null) {
                    requireActivity().runOnUiThread(() -> updateUI(result));
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Die Verbindung zum Server ist fehlgeschlagen", Toast.LENGTH_LONG).show());
                }
            });
        } else {
            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Keine Netzwerkverbindung", Toast.LENGTH_LONG).show());
        }

        serachOrder = view.findViewById(R.id.serachOrder);
        serachOrder.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                orderHistoryAdapter.filter(serachOrder.getText().toString());
                hideKeyboard(v);
                return true;
            }
            return false;
        });


        serachOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Die Filtermethode mit dem aktuellen Text aufrufen
                orderHistoryAdapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
    }


    private ArrayList<Order> downloadData(String urlStr) {
        ArrayList<Order> allOrders = new ArrayList<>();

        try {

            CompletableFuture<String> placedOrderFuture = NetworkManager.sendGetRequest(NetworkManager.APIEndpoints.PLACED_ORDERS.getUrl());
            String response = placedOrderFuture.join();
            JSONArray jsonArray = new JSONArray(response.toString());

            Gson gson = new Gson();

            for (int storeIndex = 0; storeIndex < jsonArray.length(); storeIndex++) {
                JSONObject jsonObject = jsonArray.getJSONObject(storeIndex);
                Order placedOrder = gson.fromJson(jsonObject.toString(), Order.class);
                allOrders.add(placedOrder);
            }
            return allOrders;
            } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(ArrayList<Order> result) {
        placedOrders.clear();
        placedOrders.addAll(result);
        if (orderHistoryAdapter != null) {
            orderHistoryAdapter.notifyDataSetChanged();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private String getSavedToken() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LogInData.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}