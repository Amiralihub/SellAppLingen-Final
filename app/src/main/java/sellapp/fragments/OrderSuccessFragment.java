package sellapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sellapplingen.R;

public class OrderSuccessFragment extends Fragment
    {

    public OrderSuccessFragment()
        {
        }

        /**
         * Fragment for displaying a success message with an order ID.
         *
         * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
         * @param container          The parent view that the fragment's UI should be attached to.
         * @param savedInstanceState The saved state of the fragment (not used in this case).
         * @return A View representing the fragment's UI.
         */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
                            )
        {
        View view = inflater.inflate(R.layout.fragment_order_success, container, false);




        TextView orderIdTextView = view.findViewById(R.id.orderIdTextView);

        Bundle args = getArguments();
        if (args != null && args.containsKey("orderId"))
            {
            String orderId = args.getString("orderId");
            orderIdTextView.setText("Bestellungs-ID: " + orderId);
            }

        return view;
        }
    }
