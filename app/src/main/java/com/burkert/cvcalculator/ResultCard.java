package com.burkert.cvcalculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by michael.weaver on 10/3/2014.
 */
public class ResultCard extends Activity {

    TextView resultTextView;
    Spinner resultSpinner;
    String resultText;
    Double flowRate;
    Double diffPressure;

    ArrayList<Unit> flowUnitList;
    ArrayList<Unit> pressureUnitList;
    UnitsXmlAdapter flowUnitsXmlAdapter;
    UnitsXmlAdapter pressureUnitsXmlAdapter;
    Unit selectedFlowUnit;
    Unit selectedPressureUnit;
    String selectedUnit;
    String resultType;

    protected RelativeLayout fullLayout;

    private void setResultText(String text) {
        resultText = text;
        resultTextView.setText(resultText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("RESULT_TEXT")) {
            resultText = intent.getStringExtra("RESULT_TEXT");
            resultType = "Cv";
        } else if (intent.hasExtra("FLOW_RATE")) {
            flowRate = intent.getDoubleExtra("FLOW_RATE", 0.0);
            resultType = "Flow";
        } else if (intent.hasExtra("DIFF_PRESSURE")) {
            diffPressure = intent.getDoubleExtra("DIFF_PRESSURE", 0.0);
            selectedUnit = intent.getStringExtra("SELECTED_UNIT");
            resultType = "Pressure";
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.result_card);

        this.setFinishOnTouchOutside(false);
    }

    @Override
    public void setContentView(int layoutResID) {

        fullLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.result_card, null);
        super.setContentView(fullLayout);

        resultSpinner = (Spinner) findViewById(R.id.result_card_spinner);
        resultTextView = (TextView) findViewById(R.id.result_card_text);

        if (!resultType.equals("Cv")) {
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

            if (resultType.equals("Flow")) {
                flowUnitsXmlAdapter = new UnitsXmlAdapter(flowUnitList);

                DecimalFormat df;

                for (Unit unit : flowUnitList) {
                    if (unit.unitName.equals(getResources().getString(R.string.default_flow_unit))) {
                        resultSpinner.setSelection(flowUnitList.indexOf(unit));
                        selectedFlowUnit = unit;
                        break;
                    }
                }

                if (flowRate / selectedFlowUnit.factor > 9999) {
                    df = new DecimalFormat("#");
                } else {
                    df = new DecimalFormat("0.0##");
                }

                resultSpinner.setVisibility(View.VISIBLE);
                resultSpinner.setAdapter(flowUnitsXmlAdapter);
                resultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        selectedFlowUnit = (Unit) parent.getItemAtPosition(pos);
                        DecimalFormat df;
                        if (flowRate / selectedFlowUnit.factor > 9999) {
                            df = new DecimalFormat("#");
                        } else {
                            df = new DecimalFormat("0.0##");
                        }
                        setResultText("Flow rate: \t" + df.format(flowRate / selectedFlowUnit.factor));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        // Do nothing
                    }
                });
                resultSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Unit unit : flowUnitList) {
                            if (unit.unitName.equals(getResources().getString(R.string.default_flow_unit))) {
                                resultSpinner.setSelection(flowUnitList.indexOf(unit));
                                break;
                            }
                        }
                    }
                });
                setResultText("Flow rate: \t" + df.format(flowRate / selectedFlowUnit.factor));
            } else {
                final DecimalFormat df;

                for (Unit unit : pressureUnitList) {
                    if (unit.unitName.equals(selectedUnit)) {
                        resultSpinner.setSelection(pressureUnitList.indexOf(unit));
                        selectedPressureUnit = unit;
                        break;
                    }
                }

                if (diffPressure / selectedPressureUnit.factor > 9999) {
                    df = new DecimalFormat("#");
                } else {
                    df = new DecimalFormat("0.0##");
                }

                pressureUnitsXmlAdapter = new UnitsXmlAdapter(pressureUnitList);

                resultSpinner.setVisibility(View.VISIBLE);
                resultSpinner.setAdapter(pressureUnitsXmlAdapter);
                resultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        selectedPressureUnit = (Unit) parent.getItemAtPosition(pos);
                        setResultText("Pressure drop: \t" + df.format(diffPressure / selectedPressureUnit.factor));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        // Do nothing
                    }
                });
                resultSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Unit unit : pressureUnitList) {
                            if (unit.unitName.equals(selectedUnit)) {
                                resultSpinner.setSelection(pressureUnitList.indexOf(unit));
                                break;
                            }
                        }
                    }
                });
                setResultText("Pressure drop: \t" + df.format(diffPressure / selectedPressureUnit.factor));
            }
        } else {
            resultSpinner.setVisibility(View.GONE);
        }
        resultTextView.setText(resultText);
    }

    public void onClick_OkButton(View v) {
        Handler handler = new Handler();
        final Activity activity = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
            }
        }, 250);
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
                LayoutInflater vi = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.spinner_dropdown_item, parent, false);
            }
            ((TextView) convertView).setTextColor(getResources().getColor(R.color.experience_gray));
            ((TextView) convertView).setText(data.get(position).unitName);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.spinner_dropdown_item, parent, false);
            }
            ((TextView) convertView).setTextColor(getResources().getColor(R.color.experience_gray));
            ((TextView) convertView).setText(data.get(position).unitName);
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
                        }
                    }
            }
            eventType = parser.next();
        }
    }
}
