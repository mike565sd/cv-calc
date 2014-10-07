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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    ArrayList<Unit> flowUnitList = null;
    ArrayList<Unit> pressureUnitList = null;
    ArrayList<Unit> temperatureUnitList = null;
    ArrayList<Unit> cvkvUnitList = null;
    ArrayList<Unit> densityUnitList = null;

    boolean inletPressure = true;

    public void setTemperatureEnabled(boolean temperatureEnabled) {
        if (temperatureEnabled) {
            temperatureField.setVisibility(View.VISIBLE);
            temperatureUnits.setVisibility(View.VISIBLE);
        } else {
            temperatureField.setVisibility(View.INVISIBLE);
            temperatureUnits.setVisibility(View.INVISIBLE);
        }
        validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
    }

    MainActivity mainActivity;

    Unit selectedFlowUnit, selectedCvKvUnit, selectedPressureUnit, selectedTemperatureUnit;

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
        mainActivity = (MainActivity)getActivity();

        XmlPullParserFactory pullParserFactory;

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = this.getResources().openRawResource(R.raw.units);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseUnitXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        View rootView = inflater.inflate(R.layout.fragment_pressure_drop, container, false);

        flowField = (FloatLabelEditText)rootView.findViewById(R.id.flow_label_pd);
        flowField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        flowField.getEditText().addTextChangedListener(new TextValidator(flowField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
            }
        });

        cvField = (FloatLabelEditText)rootView.findViewById(R.id.cvkv_label_pd);
        cvField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        cvField.getEditText().addTextChangedListener(new TextValidator(cvField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
            }
        });
        pressureField = (FloatLabelEditText)rootView.findViewById(R.id.pressure_label_pd);
        pressureField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        pressureField.getEditText().addTextChangedListener(new TextValidator(pressureField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
            }
        });

        temperatureField = (FloatLabelEditText)rootView.findViewById(R.id.temperature_label_pd);
        temperatureField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        temperatureField.getEditText().addTextChangedListener(new TextValidator(temperatureField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, cvField, pressureField, temperatureField});
            }
        });

        flowUnits = (Spinner)rootView.findViewById(R.id.flow_units_pd);
        UnitsXmlAdapter unitsXmlAdapter = new UnitsXmlAdapter(flowUnitList);
        flowUnits.setAdapter(unitsXmlAdapter);
        flowUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedFlowUnit = (Unit)parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        cvkvUnits = (Spinner)rootView.findViewById(R.id.cvkv_units_pd);
        unitsXmlAdapter = new UnitsXmlAdapter(cvkvUnitList);
        cvkvUnits.setAdapter(unitsXmlAdapter);
        cvkvUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedCvKvUnit = (Unit)parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        pressureUnits = (Spinner)rootView.findViewById(R.id.pressure_units_pd);
        unitsXmlAdapter = new UnitsXmlAdapter(pressureUnitList);
        pressureUnits.setAdapter(unitsXmlAdapter);
        pressureUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedPressureUnit = (Unit)parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        temperatureUnits = (Spinner)rootView.findViewById(R.id.temperature_units_pd);
        unitsXmlAdapter = new UnitsXmlAdapter(temperatureUnitList);
        temperatureUnits.setAdapter(unitsXmlAdapter);
        temperatureUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedTemperatureUnit = (Unit)parent.getItemAtPosition(pos);
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

        goButton = (ImageButton)rootView.findViewById(R.id.go_button_pd);
        goButton.setOutline(outline);
        goButton.setClipToOutline(true);
        goButton.setVisibility(View.INVISIBLE);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calculate();
            }
        });

        inletOuletSwitch = (Switch)rootView.findViewById(R.id.inlet_outlet_switch);
        inletOuletSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    pressureField.setHint(getString(R.string.outlet));
                    inletPressure = false;
                }
                else {
                    pressureField.setHint(getString(R.string.inlet));
                    inletPressure = true;
                }
            }
        });

        return  rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class UnitsXmlAdapter extends BaseAdapter implements SpinnerAdapter {

        private final List<Unit> data;

        public UnitsXmlAdapter(List<Unit> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.spinner_dropdown_item, parent, false);
            }
            ((TextView)convertView).setText(data.get(position).unitName);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.spinner_dropdown_item, parent, false);
            }
            ((TextView)convertView).setText(data.get(position).unitName);
            return convertView;
        }
    }

    private void parseUnitXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        Unit currentUnit = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    flowUnitList = new ArrayList<Unit>();
                    pressureUnitList = new ArrayList<Unit>();
                    temperatureUnitList = new ArrayList<Unit>();
                    cvkvUnitList = new ArrayList<Unit>();
                    densityUnitList = new ArrayList<Unit>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("Unit")) {
                        currentUnit = new Unit();
                    } else if (currentUnit != null) {
                        if (name.equals("Name")) {
                            currentUnit.unitName = parser.nextText();
                        } else if (name.equals("Factor")) {
                            currentUnit.factor = Float.parseFloat(parser.nextText());
                        } else if (name.equals("UnitType")) {
                            currentUnit.unitType = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Unit") && currentUnit != null) {
                        if (currentUnit.unitType.equals("Volumetric")) {
                            flowUnitList.add(currentUnit);
                        } else if (currentUnit.unitType.equals("Mass")) {
                            flowUnitList.add(currentUnit);
                        } else if (currentUnit.unitType.equals("Pressure")) {
                            pressureUnitList.add(currentUnit);
                        } else if (currentUnit.unitType.equals("Temperature")) {
                            temperatureUnitList.add(currentUnit);
                        } else if (currentUnit.unitType.equals("CvKv")) {
                            cvkvUnitList.add(currentUnit);
                        } else if (currentUnit.unitType.equals("Density")) {
                            densityUnitList.add(currentUnit);
                        }
                    }
            }
            eventType = parser.next();
        }
    }

    private void validateTextFields(FloatLabelEditText[] fields) {
        for (FloatLabelEditText field : fields) {
            if (field.getVisibility() == View.VISIBLE && field.getText().length() <=0 ) {
                goButton.setVisibility(View.INVISIBLE);
                return;
            }
        }
        goButton.setVisibility(View.VISIBLE);
    }

    private void Calculate() {
        Fluid selectedFluid = mainActivity.selectedFluid;
        double flowRate = Double.parseDouble(flowField.getText()) *
                selectedFlowUnit.factor;
        double kvValue = Double.parseDouble(cvField.getText()) *
                selectedCvKvUnit.factor;
        double pressure = Double.parseDouble(pressureField.getText()) *
                selectedPressureUnit.factor;
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
                    mCallback.displayResultCard(getString(R.string.error1));
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
        mCallback.displayResultCard(String.format("Pressure drop:\t%.3f %s", diffPressure / selectedPressureUnit.factor, selectedPressureUnit.unitName));
    }
}
