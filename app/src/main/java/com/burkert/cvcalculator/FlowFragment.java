package com.burkert.cvcalculator;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ${PROJECT_NAME}
 * Created by michael.weaver on 9/26/2014.
 */
public class FlowFragment extends Fragment {

    FloatLabelEditText cvField;
    FloatLabelEditText inletField;
    FloatLabelEditText outletField;
    FloatLabelEditText temperatureField;
    ImageButton goButton;
    Spinner cvkvUnits;
    Spinner inletUnits;
    Spinner outletUnits;
    Spinner temperatureUnits;

    Handler handler;

    public void setTemperatureEnabled(boolean temperatureEnabled) {
        if (temperatureEnabled) {
            temperatureField.setVisibility(View.VISIBLE);
            temperatureUnits.setVisibility(View.VISIBLE);
        } else {
            temperatureField.setVisibility(View.INVISIBLE);
            temperatureUnits.setVisibility(View.INVISIBLE);
        }
        mainActivity.validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField}, goButton);
    }

    MainActivity mainActivity;

    Unit selectedCvKvUnit, selectedInletUnit, selectedOutletUnit, selectedTemperatureUnit;

    OnCalculateFlowListener mCallback;

    public interface OnCalculateFlowListener {
        public void displayResultCard(Double flowRate);
    }

    public FlowFragment() {
        handler = new Handler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCalculateFlowListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCalculateListener");
        }
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            int result = i & EditorInfo.IME_MASK_ACTION;
            switch (result) {
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_NEXT:
                    mainActivity.validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField}, goButton);
                    return false;
            }
            return false;
        }
    };

    private TextValidator getTextValidator(TextView textView) {
        return new TextValidator(textView) {
            @Override
            public void validate(TextView textView, String text) {
                mainActivity.validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField}, goButton);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        View rootView = inflater.inflate(R.layout.fragment_flow, container, false);

        cvField = (FloatLabelEditText) rootView.findViewById(R.id.cvkv_label_f);
        cvField.getEditText().setOnEditorActionListener(editorActionListener);
        cvField.getEditText().addTextChangedListener(getTextValidator(cvField.getEditText()));

        inletField = (FloatLabelEditText) rootView.findViewById(R.id.inlet_label_f);
        inletField.getEditText().setOnEditorActionListener(editorActionListener);
        inletField.getEditText().addTextChangedListener(getTextValidator(inletField.getEditText()));

        outletField = (FloatLabelEditText) rootView.findViewById(R.id.outlet_label_f);
        outletField.getEditText().setOnEditorActionListener(editorActionListener);
        outletField.getEditText().addTextChangedListener(getTextValidator(outletField.getEditText()));

        temperatureField = (FloatLabelEditText) rootView.findViewById(R.id.temperature_label_f);
        temperatureField.getEditText().setOnEditorActionListener(editorActionListener);
        temperatureField.getEditText().addTextChangedListener(getTextValidator(temperatureField.getEditText()));

        cvkvUnits = (Spinner) rootView.findViewById(R.id.cvkv_units_f);
        cvkvUnits.setAdapter(mainActivity.cvkvUnitsXmlAdapter);
        cvkvUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedCvKvUnit = (Unit) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        inletUnits = (Spinner) rootView.findViewById(R.id.inlet_units_f);
        inletUnits.setAdapter(mainActivity.pressureUnitsXmlAdapter);
        inletUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedInletUnit = (Unit) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        outletUnits = (Spinner) rootView.findViewById(R.id.outlet_units_f);
        outletUnits.setAdapter(mainActivity.pressureUnitsXmlAdapter);
        outletUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedOutletUnit = (Unit) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        temperatureUnits = (Spinner) rootView.findViewById(R.id.temperature_units_f);
        temperatureUnits.setAdapter(mainActivity.temperatureUnitsXmlAdapter);
        temperatureUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedTemperatureUnit = (Unit) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        if (mainActivity.selectedFluid != null) {
            if (mainActivity.selectedFluid.getState().equals("gas")) {
                temperatureField.setVisibility(View.VISIBLE);
                temperatureUnits.setVisibility(View.VISIBLE);
            } else {
                temperatureField.setVisibility(View.INVISIBLE);
                temperatureUnits.setVisibility(View.INVISIBLE);
            }
        }

        int diameter = getResources().getDimensionPixelSize(R.dimen.round_button_diameter);
        Outline outline = new Outline();
        outline.setOval(0, 0, diameter, diameter);

        goButton = (ImageButton) rootView.findViewById(R.id.go_button_f);
        goButton.setOutline(outline);
        goButton.setClipToOutline(true);
        goButton.setVisibility(View.INVISIBLE);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calculate();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        goButton.setEnabled(true);
    }

    public void Calculate() {
        goButton.setEnabled(false);
        Fluid selectedFluid = mainActivity.selectedFluid;
        double flowRate;
        double kvValue = Double.parseDouble(cvField.getText()) *
                selectedCvKvUnit.factor;
        double inletPressure = (Double.parseDouble(inletField.getText()) *
                selectedInletUnit.factor) + 1;
        double outletPressure = (Double.parseDouble(outletField.getText()) *
                selectedOutletUnit.factor) + 1;
        double diffPressure = inletPressure - outletPressure;
        double temperature = 0;
        if (outletPressure >= inletPressure) {
            Toast.makeText(getActivity().getBaseContext(), "Inlet pressure must be higher than outlet pressure.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedFluid.getState().equals("gas")) {
            temperature = Double.parseDouble(temperatureField.getText());
            if (selectedTemperatureUnit.unitName.equals("°C")) {
                temperature = temperature + 273.15;
            } else if (selectedTemperatureUnit.unitName.equals("°F")) {
                temperature = ((temperature - 32) * (5 / 9) + 273.15);
            }
        }
        if (selectedFluid.getState().equals("liquid")) {
            flowRate = kvValue * Math.sqrt(diffPressure / selectedFluid.getDensity());
        } else {
            // subcritical flow
            if (outletPressure > (0.53 * inletPressure)) {
                flowRate = (kvValue * 514) * Math.sqrt((diffPressure * outletPressure) /
                        (selectedFluid.getDensity() * temperature));
            } else { // supercritical flow
                flowRate = (kvValue * 257 * inletPressure) *
                        (1 / Math.sqrt(selectedFluid.getDensity() * temperature));
            }
        }

        final Double finalFlowRate = flowRate;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.displayResultCard(finalFlowRate);
            }
        }, 200);
    }
}
