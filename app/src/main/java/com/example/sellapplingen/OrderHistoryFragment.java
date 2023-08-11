package com.example.sellapplingen;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class OrderHistoryFragment extends Fragment {
    private ArrayList<Order> placedOrders;
    private OrderHistoryFragment orderHistoryAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placedOrders = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }


    private ArrayList<Order> downloadData(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            ArrayList<Order> allOrders = new ArrayList<>();
            int storeIndex;
            for (storeIndex = 0; storeIndex < jsonArray.length(); storeIndex++) {
                JSONObject jsonObject = jsonArray.getJSONObject(storeIndex);
                String token = jsonObject.getString("token");
                String timestamp = jsonObject.getString("timestamp");
                String employeeName = jsonObject.getString("employeeName");
                String firstName = jsonObject.getString("firstName");
                String lastName = jsonObject.getString("lastName");
                String street = jsonObject.getString("street");
                String houseNumber = jsonObject.getString("houseNumber");
                String zip = jsonObject.getString("zip");
                String city = jsonObject.getString("city");
                String numberPackage = jsonObject.getString("numberPackage");
                String packageSize = jsonObject.getString("packageSize");
                String handlingInfo = jsonObject.getString("packageSize");
                String deliveryDate = jsonObject.getString("packageSize");
                Order placedOrders = new Order(token, timestamp, employeeName, firstName, lastName, street, houseNumber, zip, city, numberPackage, packageSize, handlingInfo, deliveryDate);
                allOrders.add(placedOrders);
                System.out.println(packageSize);
            }
            return allOrders;
            //catch block problem
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error downloading or decoding JSON data", e);
            return null;
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
}