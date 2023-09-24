package sellapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sellapp.models.Order;
import com.example.sellapplingen.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.example.sellapplingen.databinding.FragmentScannerBinding;

public class ScannerFragment extends Fragment
{

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private FragmentScannerBinding binding;
    private ActivityResultLauncher<Intent> barLauncher;
    private Order currentOrder;

    public ScannerFragment()
    {
    }

    public static ScannerFragment newInstance(Order order)
    {
        ScannerFragment fragment = new ScannerFragment();
        fragment.setCurrentOrder(order);
        return fragment;
    }

    public void setCurrentOrder(Order order)
    {
        currentOrder = order;
    }

    /**
     * Fragment for scanning QR codes and processing the results.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The saved state of the fragment (not used in this case).
     * @return A View representing the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentScannerBinding.inflate(inflater, container, false);
        setupViews();
        setupBarcodeScanner();

        return binding.getRoot();
    }

    /**
     * Initializes the UI views and their click listeners.
     */
    private void setupViews()
    {
        binding.btnScan.setOnClickListener(v -> scanCode());
        binding.btnEnterAddress.setOnClickListener(v -> openManualInputFragment());

    }

    /**
     * Initializes the barcode scanner and sets up the result handling.
     */
    private void setupBarcodeScanner()
    {
        barLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
            {
                String contents = result.getData().getStringExtra("SCAN_RESULT");
                showResultDialog(contents);
                saveScanResultToOrder(contents);
            }
        });
    }

    /**
     * Saves the scan result to the current order.
     *
     * @param scanResult The scanned QR code result.
     */
    private void saveScanResultToOrder(String scanResult)
    {
        String[] scanResultArray = scanResult.split("&");
        if (scanResultArray.length == 6)
        {

            currentOrder.setRecipientOverOrder(scanResultArray[0], scanResultArray[1], scanResultArray[2],
                    scanResultArray[3], scanResultArray[4]);
        }
    }

    /**
     * Initiates the QR code scanning process.
     */
    private void scanCode()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(requireContext(), CaptureActivity.class);
            barLauncher.launch(intent);
        } else
        {
            requestCameraPermission();
        }
    }

    /**
     * Requests camera permission from the user.
     */
    private void requestCameraPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA))
        {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Kameraerlaubnis erforderlich")
                    .setMessage("Die Kameraerlaubnis wird benötigt, um den QR-Code zu scannen.")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE))
                    .setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
        else
        {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Callback method called when the user responds to a permission request.
     *
     * @param requestCode  The request code specified when requesting permission.
     * @param permissions  The requested permissions.
     * @param grantResults The results of the permission request indicating whether the permissions were granted or not.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(requireContext(), CaptureActivity.class);
                barLauncher.launch(intent);
            } else
            {
                Toast.makeText(requireContext(), "Kameraerlaubnis wurde nicht erteilt.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Displays a dialog with the scanned QR code result and handles the result data.
     *
     * @param contents The scanned QR code result as a string.
     */
    private void showResultDialog(String contents)
    {
        String[] res = contents.split("&");
        if (res.length == 6)
        {
            Order order = new Order();

        System.out.println(res[0] + " Sacn erste Stelle! ");
            order.setRecipientOverOrder(res[0], res[1], res[2],
                    res[3], res[4]);

        String orderInfoForDialog = TextUtils.join(", ", res);

        new AlertDialog.Builder(requireContext())
                .setTitle("Scan-Ergebnis")
                .setMessage(orderInfoForDialog)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

        Bundle args = new Bundle();
        args.putSerializable("order", order);
        HandlingInfoFragment toFragment = new HandlingInfoFragment();
        toFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, toFragment, "tariq").commit();


        Log.i("Sadik", "showResultDialog: " + res[0] + "\n" + res[2] + "\n" + res[3] + "\n" + res[4] + "\n" + res[5]);

        } else
        {

            Toast.makeText(requireContext(), "Ungültiges QR-Code-Format.", Toast.LENGTH_SHORT).show();
        }

}

    /**
     * Opens the ManualInputFragment and passes the current order data to it.
     */
    private void openManualInputFragment()
    {
        ManualInputFragment manualInputFragment = new ManualInputFragment();
        manualInputFragment.setCurrentOrder(currentOrder);

        FragmentManager fragmentManager = requireFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, manualInputFragment, "manualInputFragment");
        transaction.addToBackStack(null); // Füge das Fragment zur Rückwärtsnavigation hinzu
        transaction.commit();
    }

}
