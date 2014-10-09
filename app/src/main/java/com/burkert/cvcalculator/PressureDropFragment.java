package com.burkert.cvcalculator;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ${PACKAGE_NAME}
 * Created by michael.weaver on 9/26/2014.
 */
public class PressureDropFragment extends Fragment {

    FloatLabelEditText flowField;
    FloatLabelEditText cvField;
    FloatLabelEditText pressureField;
    FloatLabelEditText temperatureField;
    ImageButton goButton;
    Switch inletOuletSwitch;
    Spinner flowUnits;
    Spinner cvkvUnits;
    Spinner pressureUnits;
    Spinner temperatureUnits;

    Handler handler;

    boolean inletPressure = true;

    public void setTemperatureEnabled(boolean temperatureEnabled) {
        if (temperatureEnabled) {
            temperatureField.setVisibility(View.VISIBLE);
            temperatureUnits.setVisibility(View.VISIBLE);
        } else {
            temperatureField.setVisibility(View.INVISIBLE);
            temperatureUnits.setVisibility(View.INVISIBLE);
        }
        mainActivity.validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField}, goButton);
    }

    MainActivity mainActivity;

    Unit selectedFlowUnit, selectedCvKvUnit, selectedPressureUnit, selectedTemperatureUnit;

    OnCalculatePressureDropListener mCallback;

    public interface OnCalculatePressureDropListener {
        public void displayResultCard(Double diffPressure, String selectedUnit);
    }

    public PressureDropFragment() {
        handler = new Handler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCalculatePressureDropListener) activity;
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
                    mainActivity.validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField}, goButton);
                    return false;
            }
            return false;
        }
    };

    private TextValidator getTextValidator(TextView textView) {
        return new TextValidator(textView) {
            @Override
            public void validate(TextView textView, String text) {
                mainActivity.validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField}, goButton);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        View rootView = inflater.inflate(R.layout.fragment_pressure_drop, container, false);

        flowField = (FloatLabelEditText) rootView.findViewById(R.id.flow_label_pd);
        flowField.getEditText().setOnEditorActionListener(editorActionListener);
        flowField.getEditText().addTextChangedListener(getTextValidator(flowField.getEditText()));

        cvField = (FloatLabelEditText) rootView.findViewById(R.id.cvkv_label_pd);
        cvField.getEditText().setOnEditorActionListener(editorActionListener);
        cvField.getEditText().addTextChangedListener(getTextValidator(cvField.getEditText()));

        pressureField = (FloatLabelEditText) rootView.findViewById(R.id.pressure_label_pd);
        pressureField.getEditText().setOnEditorActionListener(editorActionListener);
        pressureField.getEditText().addTextChangedListener(getTextValidator(pressureField.getEditText()));

        temperatureField = (FloatLabelEditText) rootView.findViewById(R.id.temperature_label_pd);
        temperatureField.getEditText().setOnEditorActionListener(editorActionListener);
        temperatureField.getEditText().addTextChangedListener(getTextValidator(temperatureField.getEditText()));

        flowUnits = (Spinner) rootView.findViewById(R.id.flow_units_pd);
        flowUnits.setAdapter(mainActivity.flowUnitsXmlAdapter);
        flowUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedFlowUnit = (Unit) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        cvkvUnits = (Spinner) rootView.findViewById(R.id.cvkv_units_pd);
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

        pressureUnits = (Spinner) rootView.findViewById(R.id.pressure_units_pd);
        pressureUnits.setAdapter(mainActivity.pressureUnitsXmlAdapter);
        pressureUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedPressureUnit = (Unit) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        temperatureUnits = (Spinner) rootView.findViewById(R.id.temperature_units_pd);
        temperatureUnits.setAdapter(mainActivity.temperatureUnitsXmlAdapter);
        temperatureUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedTemperatureUnit = (Unit) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing;
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

        goButton = (ImageButton) rootView.findViewById(R.id.go_button_pd);
        goButton.setOutline(outline);
        goButton.setClipToOutline(true);
        goButton.setVisibility(View.INVISIBLE);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calculate();
            }
        });

        inletOuletSwitch = (Switch) rootView.findViewById(R.id.inlet_outlet_switch);
        inletOuletSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    pressureField.setHint(getString(R.string.outlet));
                    inletPressure = false;
                } else {
                    pressureField.setHint(getString(R.string.inlet));
                    inletPressure = true;
                }
            }
        });

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

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

    private void Calculate() {
        goButton.setEnabled(false);
        Fluid selectedFluid = mainActivity.selectedFluid;
        double flowRate = Double.parseDouble(flowField.getText()) *
                selectedFlowUnit.factor;
        double kvValue = Double.parseDouble(cvField.getText()) *
                selectedCvKvUnit.factor;
        double pressure = (Double.parseDouble(pressureField.getText()) *
                selectedPressureUnit.factor) + 1;
        double temperature;
        double diffPressure;

        if (selectedFluid.getState().equals("liquid")) {
            diffPressure = Math.pow(flowRate / kvValue, 2) * selectedFluid.getDensity();
        } else {
            temperature = Double.parseDouble(temperatureField.getText());
            if (selectedTemperatureUnit.unitName.equals("°C")) {
                temperature = temperature + 273.15;
            } else if (selectedTemperatureUnit.unitName.equals("°F")) {
                temperature = ((temperature - 32) * (5 / 9) + 273.15);
            }
            if (inletPressure) {
                if ((Math.pow(pressure / 2, 2) - Math.pow((flowRate / (514 * kvValue)), 2) *
                        selectedFluid.getDensity() * temperature) < 0) {
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.error1), Toast.LENGTH_SHORT).show();
                    goButton.setEnabled(true);
                    return;
                } else {
                    diffPressure = (pressure / 2) - (Math.sqrt(Math.pow(pressure / 2, 2) -
                            Math.pow(flowRate / (514 * kvValue), 2) * selectedFluid.getDensity() * temperature));
                }
            } else {
                diffPressure = Math.pow(flowRate / (514 * kvValue), 2) * temperature
                        * selectedFluid.getDensity() / pressure;
                if (pressure < ((pressure + diffPressure) / 2)) {
                    diffPressure = flowRate * Math.sqrt(selectedFluid.getDensity() * temperature)
                            / (257 * kvValue) - pressure;
                }
            }
        }

        final Double finalDiffPressure = diffPressure;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.displayResultCard(finalDiffPressure, selectedPressureUnit.unitName);
            }
        }, 150);
    }
}
