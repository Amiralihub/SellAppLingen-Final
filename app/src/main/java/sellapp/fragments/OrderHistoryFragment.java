package sellapp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import sellapp.models.NetworkManager;
import sellapp.models.Order;
import sellapp.adapters.OrderHistoryAdapter;

import com.example.sellapplingen.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderHistoryFragment extends Fragment
    {
    private ArrayList<Order> placedOrders;
    private OrderHistoryAdapter orderHistoryAdapter;
    private EditText serachOrder;

        /**
         * Called when the fragment is first created. Initializes the list of placed orders
         * and sets up the adapter for the order history.
         */
    @Override
    public void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        placedOrders = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(requireContext(), placedOrders);
        }

        /**
         * Called to create the view hierarchy associated with the fragment. Initializes the UI components,
         * sets up the RecyclerView, and fetches order data from the server if a network connection is available.
         *
         * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
         * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
         * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
         * @return The root view of the fragment's layout.
         */
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
                            )
        {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewQRCodeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderHistoryAdapter = new OrderHistoryAdapter(requireContext(), placedOrders);
        recyclerView.setAdapter(orderHistoryAdapter);

        if (isNetworkAvailable())
            {
            executorService.execute(() ->
                                        {
                                        ArrayList<Order> result = downloadData(
                                                "http://131.173.65.77:8080/api/allOrders");
                                        if (result != null)
                                            {
                                            requireActivity().runOnUiThread(() -> updateUI(result));
                                            }
                                        else
                                            {
                                            requireActivity().runOnUiThread(() -> showErrorPopup(
                                                    "Die Verbindung zum Server ist fehlgeschlagen"));
                                            }
                                        });
            }
        else
            {
            requireActivity().runOnUiThread(() -> showErrorPopup("Keine Netzwerkverbindung"));
            }

        serachOrder = view.findViewById(R.id.serachOrder);
        serachOrder.setOnEditorActionListener((v, actionId, event) ->
                                                  {
                                                  if (actionId == EditorInfo.IME_ACTION_DONE)
                                                      {
                                                      orderHistoryAdapter.filter(
                                                              serachOrder.getText().toString());
                                                      hideKeyboard(v);
                                                      return true;
                                                      }
                                                  return false;
                                                  });

        serachOrder.addTextChangedListener(new TextWatcher()
            {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                // Die Filtermethode mit dem aktuellen Text aufrufen
                orderHistoryAdapter.filter(charSequence.toString());
                }

            @Override
            public void afterTextChanged(Editable editable)
                {
                }
            });

        return view;
        }

        /**
         * Downloads a list of orders from a specified URL and parses them into an ArrayList of Order objects.
         *
         * @param urlStr The URL from which to download the orders.
         * @return An ArrayList containing the downloaded orders as Order objects.
         * @throws RuntimeException If there is an error while parsing JSON data or if no data is received from the server.
         */
    private ArrayList<Order> downloadData(String urlStr)
        {
        ArrayList<Order> allOrders = new ArrayList<>();
        try
            {
            CompletableFuture<String> placedOrderFuture = NetworkManager.sendGetRequest(
                    NetworkManager.APIEndpoints.PLACED_ORDERS.getUrl());
            String response = placedOrderFuture.join();

            if (response != null)
                {
                JSONArray jsonArray = new JSONArray(response.toString());
                Gson gson = new Gson();

                for (int storeIndex = 0; storeIndex < jsonArray.length(); storeIndex++)
                    {
                    JSONObject jsonObject = jsonArray.getJSONObject(storeIndex);
                    Order placedOrder = gson.fromJson(jsonObject.toString(), Order.class);
                    allOrders.add(placedOrder);
                    }
                }
            else
                {
                requireActivity().runOnUiThread(() -> showErrorPopup(
                        "Keine Daten vom Server empfangen oder keine Internetverbindung."));

                }

            return allOrders;
            } catch (JSONException ex)
            {
            throw new RuntimeException(ex);
            }
        }

        /**
         * Updates the UI with a new list of orders and notifies the adapter of the data change.
         *
         * @param result The list of orders to be displayed in the UI.
         */
    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(ArrayList<Order> result)
        {
        placedOrders.clear();
        placedOrders.addAll(result);
        if (orderHistoryAdapter != null)
            {
            orderHistoryAdapter.notifyDataSetChanged();
            }
        }

        /**
         * Checks if a network connection is available.
         *
         * @return True if a network connection is available, otherwise false.
         */
    private boolean isNetworkAvailable()
        {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
        }

        /**
         * Displays an error message as a Toast popup.
         *
         * @param message The error message to be displayed.
         */
    private void showErrorPopup(String message)
        {
        requireActivity().runOnUiThread(() ->
                                            {
                                            Toast.makeText(
                                                         requireContext(), message, Toast.LENGTH_LONG)
                                                 .show();
                                            });
        }

        /**
         * Hides the software keyboard.
         *
         * @param view The view that currently has focus and should lose it to hide the keyboard.
         */
    private void hideKeyboard(View view)
        {
        InputMethodManager inputMethodManager
                = (InputMethodManager) requireContext().getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
