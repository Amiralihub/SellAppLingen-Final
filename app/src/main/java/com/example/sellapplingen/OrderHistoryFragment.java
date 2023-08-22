package com.example.sellapplingen;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OrderHistoryFragment extends Fragment {
    private ArrayList<PlacedOrder> placedOrders;
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
                ArrayList<PlacedOrder> result = downloadData("http://131.173.65.77:8080/api/allOrders");
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


    private ArrayList<PlacedOrder> downloadData(String urlStr) {
        String testToken = getSavedToken();
        ArrayList<PlacedOrder> allOrders = new ArrayList<>();

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", testToken);

            URL url = new URL("http://131.173.65.77:8080/api/allOrders");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString()); // Missing jsonArray initialization

                for (int storeIndex = 0; storeIndex < jsonArray.length(); storeIndex++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(storeIndex);
                    String orderID = jsonObject.getString("orderID");
                    String timestamp = jsonObject.getString("timestamp");
                    String employeeName = jsonObject.getString("employeeName");
                    String packageSize = jsonObject.getString("packageSize");
                    String deliveryDate = jsonObject.getString("deliveryDate");
                    String customDropOffPlace = jsonObject.getString("customDropoffPlace");
                    String handlingInfo = jsonObject.getString("handlingInfo");
                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
                    String street = jsonObject.getString("street");
                    String houseNumber = jsonObject.getString("houseNumber");
                    String zip = jsonObject.getString("ZIP");

                    PlacedOrder placedOrders = new PlacedOrder(orderID, timestamp, employeeName, packageSize, PlacedOrder.formatDate(deliveryDate),
                            customDropOffPlace, handlingInfo, firstName, lastName, street, houseNumber, zip);
                    allOrders.add(placedOrders);
                    System.out.println(placedOrders.toString());
                }
                return allOrders;
            } else {
                Log.e(TAG, "Server returned status code: " + responseCode);
                return null;
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error downloading or decoding JSON data", e);
            e.printStackTrace();
            return null;
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(ArrayList<PlacedOrder> result) {
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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}