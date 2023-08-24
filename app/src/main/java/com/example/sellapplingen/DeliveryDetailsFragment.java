package com.example.sellapplingen;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DeliveryDetailsFragment extends Fragment {



    private Order order;

    public String orderId;

    private String token;

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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://131.173.65.77:8080/api/order");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject();
                        jsonObject.put("token", token);
                        jsonObject.put("timestamp", order.getTimestamp());
                        jsonObject.put("employeeName", order.getEmployeeName());
                        jsonObject.put("firstName", order.getFirstName());
                        jsonObject.put("lastName", order.getLastName());
                        jsonObject.put("street", order.getStreet());
                        jsonObject.put("houseNumber", order.getHouseNumber());
                        jsonObject.put("zip", order.getZip());
                        jsonObject.put("city", order.getCity());
                        jsonObject.put("numberPackage", order.getNumberPackage());
                        jsonObject.put("packageSize", order.getPackageSize());
                        jsonObject.put("handlingInfo", order.getHandlingInfo());
                        jsonObject.put("deliveryDate", order.getDeliveryDate());
                        jsonObject.put("customDropOffPlace", order.getCustomDropOffPlace());

                        String jsonString = jsonObject.toString();
                        byte[] postData = jsonString.getBytes(StandardCharsets.UTF_8);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.write(postData);
                        os.flush();
                        os.close();

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = conn.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            StringBuilder responseBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                responseBuilder.append(line);
                            }
                            reader.close();
                            inputStream.close();

                            String responseString = responseBuilder.toString();
                            JSONObject responseJson = new JSONObject(responseString);

                            orderId = responseJson.getString("orderID");
                            showSuccessPopup(orderId);
                        } else {
                            showSuccessPopup(orderId); // Hier könnte eine Fehlerbehandlung stehen
                        }

                        conn.disconnect();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
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

    // Füge diese Methode zum Anzeigen des Popups hinzu
    private void showSuccessPopup(String orderId) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View popupView = getLayoutInflater().inflate(R.layout.popup_success_deliverydetail, null);

                TextView successMessageTextView = popupView.findViewById(R.id.successMessageTextView);
                TextView orderIdTextView = popupView.findViewById(R.id.orderIdTextView);
                Button backToScannerButton = popupView.findViewById(R.id.backToScannerButton);

                orderIdTextView.setText("Bestellungs-ID: " + orderId);

                AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                        .setView(popupView)
                        .create();

                backToScannerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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





    // In deiner `sendOrderDataToServer`-Methode, nachdem du die `orderId` erhalten hast


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

// In onCreateView von DeliveryDetailsFragment
        TextView timestampValue = view.findViewById(R.id.timestampValue);

        String myFormat = "dd-MM-yyyy:HH-mm-ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        String getTime = dateFormat.format(Calendar.getInstance().getTime());
        timestampValue.setText(getTime);

        // Hier wird die Uhrzeit in das Order-Objekt gespeichert
        if (order != null) {
            order.setTimestamp(getTime);
        }



        // Hole das Order-Objekt aus den Fragment-Argumenten
        Bundle args = getArguments();
        if (args != null && args.containsKey("order")) {
            order = (Order) args.getSerializable("order");
        }

        // Zeige die Order-Informationen in den entsprechenden TextViews an
        if (order != null) {
            createTestOrder();


            TextView employeeIdValue = view.findViewById(R.id.employeeIdValue);
            employeeIdValue.setText(order.getEmployeeName());

            TextView firstNameValue = view.findViewById(R.id.firstNameValue);
            firstNameValue.setText(order.getFirstName() +" ");

            TextView lastNameValue = view.findViewById(R.id.lastNameValue);
            lastNameValue.setText(order.getLastName());

            TextView packageSizeValue = view.findViewById(R.id.packageSizeValue);
            packageSizeValue.setText(order.getPackageSize());

            TextView actionInfoValue = view.findViewById(R.id.actionInfoValue);
            actionInfoValue.setText(order.getHandlingInfo());

            TextView customDropOffValue = view.findViewById(R.id.customDropOffValue);
            customDropOffValue.setText(order.getCustomDropOffPlace());

            TextView deliveryDateValue = view.findViewById(R.id.deliveryDateValue);
            deliveryDateValue.setText(order.getDeliveryDate());

            TextView streetNameValue = view.findViewById(R.id.streetNameValue);
            streetNameValue.setText(order.getStreet() +" ");


            TextView housenNumberValue = view.findViewById(R.id.houseNumberValue);
            housenNumberValue.setText(order.getHouseNumber());

            TextView cityLabel = view.findViewById(R.id.cityValue);
            cityLabel.setText(order.getCity() + " ");

            TextView zipLabel = view.findViewById(R.id.zipValue);
            zipLabel.setText(order.getZip() + " ");

            // Weitere TextViews für andere Order-Informationen hinzufügen
            // Hier kannst du weitere TextViews hinzufügen, um andere Order-Informationen anzuzeigen
            // Beispiel:
            // TextView additionalInfoValue = view.findViewById(R.id.additionalInfoValue);
            // additionalInfoValue.setText(order.getAdditionalInfo());
        }

        return view;
    }

    private String getSavedToken() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LoginManager.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }



}