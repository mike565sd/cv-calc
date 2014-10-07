package com.burkert.cvcalculator;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by michael.weaver on 9/26/2014.
 */
public class PressureDropFragment extends Fragment {

    FloatLabelEditText flowField;
    FloatLabelEditText cvkvField;
    FloatLabelEditText pressureField;
    FloatLabelEditText temperatureField;
    Spinner flowUnits;
    Spinner cvkvUnits;
    Spinner pressureUnits;
    Spinner temperatureUnits;

    Handler handler;

    OnCalculatePressureDropListener mCallback;

    public interface OnCalculatePressureDropListener {
        public void displayResultCard(String message);
    }

    public PressureDropFragment(){
        handler = new Handler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCalculatePressureDropListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCalculateListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pressure_drop, container, false);
        flowField = (FloatLabelEditText)rootView.findViewById(R.id.flow_label_pd);
        cvkvField = (FloatLabelEditText)rootView.findViewById(R.id.cvkv_label_pd);
        pressureField = (FloatLabelEditText)rootView.findViewById(R.id.pressure_label_pd);
        temperatureField = (FloatLabelEditText)rootView.findViewById(R.id.temperature_label_pd);

        flowUnits = (Spinner)rootView.findViewById(R.id.flow_units_pd);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.flow_units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flowUnits.setAdapter(adapter);

        cvkvUnits = (Spinner)rootView.findViewById(R.id.cvkv_units_pd);
        adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.cvkv_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cvkvUnits.setAdapter(adapter);

        pressureUnits = (Spinner)rootView.findViewById(R.id.pressure_units_pd);
        adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.pressure_units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pressureUnits.setAdapter(adapter);

        temperatureUnits = (Spinner)rootView.findViewById(R.id.temperature_units_pd);
        adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.temperature_units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temperatureUnits.setAdapter(adapter);

        return  rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
