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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    ArrayList<Unit> flowUnitList = null;
    ArrayList<Unit> pressureUnitList = null;
    ArrayList<Unit> temperatureUnitList = null;
    ArrayList<Unit> cvkvUnitList = null;
    ArrayList<Unit> densityUnitList = null;

    public void setTemperatureEnabled(boolean temperatureEnabled) {
        if (temperatureEnabled) {
            temperatureField.setVisibility(View.VISIBLE);
            temperatureUnits.setVisibility(View.VISIBLE);
        } else {
            temperatureField.setVisibility(View.INVISIBLE);
            temperatureUnits.setVisibility(View.INVISIBLE);
        }
        validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
    }

    MainActivity mainActivity;

    Unit selectedCvKvUnit, selectedInletUnit, selectedOutletUnit, selectedTemperatureUnit;

    OnCalculateFlowListener mCallback;

    public interface OnCalculateFlowListener {
        public void displayResultCard(String message);
    }

    public FlowFragment(){
        handler = new Handler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCalculateFlowListener)activity;
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

        View rootView = inflater.inflate(R.layout.fragment_flow, container, false);

        cvField = (FloatLabelEditText)rootView.findViewById(R.id.cvkv_label_f);
        cvField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        cvField.getEditText().addTextChangedListener(new TextValidator(cvField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
            }
        });

        inletField = (FloatLabelEditText)rootView.findViewById(R.id.inlet_label_f);
        inletField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        inletField.getEditText().addTextChangedListener(new TextValidator(inletField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
            }
        });
        outletField = (FloatLabelEditText)rootView.findViewById(R.id.outlet_label_f);
        outletField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        outletField.getEditText().addTextChangedListener(new TextValidator(outletField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
            }
        });

        temperatureField = (FloatLabelEditText)rootView.findViewById(R.id.temperature_label_f);
        temperatureField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        temperatureField.getEditText().addTextChangedListener(new TextValidator(temperatureField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{cvField, inletField, outletField, temperatureField});
            }
        });

        cvkvUnits = (Spinner)rootView.findViewById(R.id.cvkv_units_f);
        UnitsXmlAdapter unitsXmlAdapter = new UnitsXmlAdapter(cvkvUnitList);
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

        inletUnits = (Spinner)rootView.findViewById(R.id.inlet_units_f);
        unitsXmlAdapter = new UnitsXmlAdapter(pressureUnitList);
        inletUnits.setAdapter(unitsXmlAdapter);
        inletUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedInletUnit = (Unit)parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        outletUnits = (Spinner)rootView.findViewById(R.id.outlet_units_f);
        unitsXmlAdapter = new UnitsXmlAdapter(pressureUnitList);
        outletUnits.setAdapter(unitsXmlAdapter);
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

        temperatureUnits = (Spinner)rootView.findViewById(R.id.temperature_units_f);
        unitsXmlAdapter = new UnitsXmlAdapter(temperatureUnitList);
        temperatureUnits.setAdapter(unitsXmlAdapter);
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

        goButton = (ImageButton)rootView.findViewById(R.id.go_button_f);
        goButton.setOutline(outline);
        goButton.setClipToOutline(true);
        goButton.setVisibility(View.INVISIBLE);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calculate();
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

    public void Calculate() {
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
        String resultText = String.format("\r\nFlow Rate:\t%.3f m³/h", flowRate);

        mCallback.displayResultCard(resultText);
    }
}
