package com.example.sellapplingen;

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
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.example.sellapplingen.databinding.FragmentScannerBinding;

public class ScannerFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private FragmentScannerBinding binding;
    private ActivityResultLauncher<Intent> barLauncher;
    private Order currentOrder;

    public ScannerFragment() {
        // Required empty public constructor
    }

    public static ScannerFragment newInstance(Order order) {
        ScannerFragment fragment = new ScannerFragment();
        fragment.setCurrentOrder(order);
        return fragment;
    }

    public void setCurrentOrder(Order order) {
        currentOrder = order;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScannerBinding.inflate(inflater, container, false);
        setupViews();
        setupBarcodeScanner();

        return binding.getRoot();
    }

    private void setupViews() {
        binding.btnScan.setOnClickListener(v -> scanCode());
        // Füge diesen Code in setupViews() im ScannerFragment hinzu
        binding.btnEnterAddress.setOnClickListener(v -> openManualInputFragment());

    }

    private void setupBarcodeScanner() {
        barLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                String contents = result.getData().getStringExtra("SCAN_RESULT");
                showResultDialog(contents);
                // Speichere die gescannten Informationen in Order
                saveScanResultToOrder(contents);
            }
        });
    }

    private void saveScanResultToOrder(String scanResult) {
        String[] scanResultArray = scanResult.split("&");
        if (scanResultArray.length == 6) {
            currentOrder.setLastName(scanResultArray[0]);
            currentOrder.setFirstName(scanResultArray[1]);
            currentOrder.setStreet(scanResultArray[2]);
            currentOrder.setHouseNumber(scanResultArray[3]);
            currentOrder.setZip(scanResultArray[4]);
            currentOrder.setCity(scanResultArray[5]);
        } else {
            // Wenn der gescannte Inhalt nicht das erwartete Format hat, zeige eine Fehlermeldung
            Toast.makeText(requireContext(), "Ungültiger QR-Code-Format.", Toast.LENGTH_SHORT).show();
        }
    }

    private void scanCode() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Wenn die Kameraerlaubnis erteilt wurde, starte den QR-Code-Scanner
            Intent intent = new Intent(requireContext(), CaptureActivity.class);
            barLauncher.launch(intent);
        } else {
            // Wenn die Kameraerlaubnis nicht erteilt wurde, frage den Benutzer nach der Erlaubnis
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            // Zeige eine Erklärung, warum die Kameraerlaubnis benötigt wird
            new AlertDialog.Builder(requireContext()).setTitle("Kameraerlaubnis erforderlich").setMessage("Die Kameraerlaubnis wird benötigt, um den QR-Code zu scannen.").setPositiveButton("OK", (dialog, which) -> requestPermission()).setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss()).create().show();
        } else {
            // Frage den Benutzer nach der Kameraerlaubnis
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Wenn die Kameraerlaubnis erteilt wurde, starte den QR-Code-Scanner
                Intent intent = new Intent(requireContext(), CaptureActivity.class);
                barLauncher.launch(intent);
            } else {
                // Wenn die Kameraerlaubnis nicht erteilt wurde, zeige eine Fehlermeldung
                Toast.makeText(requireContext(), "Kameraerlaubnis wurde nicht erteilt.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showResultDialog(String contents) {
        String[] res = contents.split("&");
        Order order = new Order();
        order.setLastName(res[0]);
        order.setFirstName(res[1]);
        order.setStreet(res[2]);
        order.setHouseNumber(res[3]);
        order.setZip(res[4]);
        order.setCity(res[5]);

        String orderInfoForDialog = TextUtils.join(", ", res);

        new AlertDialog.Builder(requireContext())
                .setTitle("Scan-Ergebnis")
                .setMessage(orderInfoForDialog)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
/*        HandlingInfoFragment fragment = new HandlingInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        fragment.setArguments(args);*/
  /*      FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
*/


        Bundle args = new Bundle();
        args.putSerializable("order", order);
        HandlingInfoFragment toFragment = new HandlingInfoFragment();
        toFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, toFragment, "tariq").commit();


        Log.i("tariq", "showResultDialog: " + res[0]+"\n"+res[2]+"\n"+res[3]+"\n"+res[4]+"\n"+res[5]);
    }

    private void openManualInputFragment() {
        // Erstelle das ManualInputFragment
        ManualInputFragment manualInputFragment = new ManualInputFragment();
        manualInputFragment.setCurrentOrder(currentOrder);

        // Führe den Fragment-Wechsel durch
        FragmentManager fragmentManager = requireFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, manualInputFragment, "manualInputFragment");
        transaction.addToBackStack(null); // Füge das Fragment zur Rückwärtsnavigation hinzu
        transaction.commit();
    }

}
