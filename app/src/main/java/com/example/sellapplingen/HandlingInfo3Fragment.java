package com.example.sellapplingen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//TODO XML (Alaa): Ein weiterer backTofragmentButton (mein ImageButton kann verwendet werden), der oben links platziert ist
//TODO XML (Alaa): Kalenderansicht auf dem aktuellen Datum aktualisieren (Ist das +berhaupt durch XML machbar?)
//TODO: backTofragmentButton leitet den Nutzer auf dem QRCOdeScanner
//TODO: confirmButton leitet den Nutzer auf die Zusammenfassung-Ansicht weiter
//TODO: backButton leitet den Nutzer auf HandlingInfo2 weiter
//TODO: Die Informationen in einem zugehörigen Objekt speichern


public class HandlingInfo3Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HandlingInfo3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HandlingInfo3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HandlingInfo3Fragment newInstance(String param1, String param2) {
        HandlingInfo3Fragment fragment = new HandlingInfo3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_handling_info3, container, false);
    }
}