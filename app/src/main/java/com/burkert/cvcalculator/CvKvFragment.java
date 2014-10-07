package com.burkert.cvcalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.os.Handler;
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
public class CvKvFragment extends Fragment {

    FloatLabelEditText flowField;
    FloatLabelEditText inletField;
    FloatLabelEditText outletField;
    FloatLabelEditText temperatureField;
    Spinner flowUnits;
    Spinner inletUnits;
    Spinner outletUnits;
    Spinner temperatureUnits;
    ImageButton goButton;

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
        validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
    }

    MainActivity mainActivity;

    Unit selectedFlowUnit, selectedInletUnit, selectedOutletUnit, selectedTemperatureUnit;

    OnCalculateCvKvListener mCallback;

    public interface OnCalculateCvKvListener {
        public void displayResultCard(String message);
    }

    public CvKvFragment(){
        handler = new Handler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnCalculateCvKvListener)activity;
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

        View rootView = inflater.inflate(R.layout.fragment_cvkv, container, false);

        flowField = (FloatLabelEditText)rootView.findViewById(R.id.flow_label);
        flowField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        flowField.getEditText().addTextChangedListener(new TextValidator(flowField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
            }
        });

        inletField = (FloatLabelEditText)rootView.findViewById(R.id.inlet_label);
        inletField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
                }
                return false;
            }
        });
        inletField.getEditText().addTextChangedListener(new TextValidator(inletField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
            }
        });

        outletField = (FloatLabelEditText)rootView.findViewById(R.id.outlet_label);
        outletField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        outletField.getEditText().addTextChangedListener(new TextValidator(outletField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
            }
        });

        temperatureField = (FloatLabelEditText)rootView.findViewById(R.id.temperature_label);
        temperatureField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                        validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
                        return false;
                }
                return false;
            }
        });
        temperatureField.getEditText().addTextChangedListener(new TextValidator(temperatureField.getEditText()) {
            @Override
            public void validate(TextView textView, String text) {
                validateTextFields(new FloatLabelEditText[]{flowField, inletField, outletField, temperatureField});
            }
        });

        flowUnits = (Spinner)rootView.findViewById(R.id.flow_units);
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

        inletUnits = (Spinner)rootView.findViewById(R.id.inlet_units);
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

        outletUnits = (Spinner)rootView.findViewById(R.id.outlet_units);
        unitsXmlAdapter = new UnitsXmlAdapter(pressureUnitList);
        outletUnits.setAdapter(unitsXmlAdapter);
        outletUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedOutletUnit = (Unit)parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        temperatureUnits = (Spinner)rootView.findViewById(R.id.temperature_units);
        unitsXmlAdapter = new UnitsXmlAdapter(temperatureUnitList);
        temperatureUnits.setAdapter(unitsXmlAdapter);
        temperatureUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedTemperatureUnit = (Unit)parent.getItemAtPosition(pos);
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

        goButton = (ImageButton)rootView.findViewById(R.id.go_button);
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
        double kvValue, diameter, qnn;
        double flowRate = Double.parseDouble(flowField.getText()) *
               selectedFlowUnit.factor ;
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
            kvValue = flowRate * Math.sqrt(selectedFluid.getDensity() / diffPressure);
        } else {
            if (selectedFlowUnit.unitType.equals("Mass")) {
                flowRate = flowRate / selectedFluid.getDensity();
            }
            // subcritical flow
            if (outletPressure > (0.53 * inletPressure)) {
                kvValue = (flowRate / 514) * Math.sqrt((selectedFluid.getDensity() * temperature) / (diffPressure * outletPressure));
            } else { //supercritical flow
                kvValue = (flowRate / (257 * inletPressure) * Math.sqrt(selectedFluid.getDensity() * temperature));
            }
        }
        qnn = kvValue * 1078;
        diameter = Math.sqrt(qnn / 30);
        String resultText =
                String.format("Cv:\t\t%.4f\r\nKv:\t\t%.4f\r\nQNN-value:\t%.3f l/min", kvValue * 1.156, kvValue, qnn);
        if (diameter < 6) {
            resultText += String.format("\r\nDN approx:\t%.3f mm", diameter);
        }

        mCallback.displayResultCard(resultText);
    }
}
