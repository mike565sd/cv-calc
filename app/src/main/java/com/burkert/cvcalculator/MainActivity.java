package com.burkert.cvcalculator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class Fluid {
    public String getFluidName() {
        return mFluidName;
    }

    public void setFluidName(String mFluidName) {
        this.mFluidName = mFluidName;
    }

    public String getFormula() {
        return mFormula;
    }

    public void setFormula(String mFormula) {
        this.mFormula = mFormula;
    }

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState;
    }

    public float getDensity() {
        return mDensity;
    }

    public void setDensity(float mDensity) {
        this.mDensity = mDensity;
    }

    private String mFluidName;
    private String mFormula;
    private String mState;
    private float mDensity;
}

class Unit {
    public String unitName;
    public float factor;
    public String unitType;
}

public class MainActivity extends Activity
    implements CvKvFragment.OnCalculateCvKvListener, FlowFragment.OnCalculateFlowListener,
                PressureDropFragment.OnCalculatePressureDropListener {

    public Fluid selectedFluid;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    ArrayList<Fluid> fluids = null;
    ImageView formulaImage;
    TextView formulaField;
    Spinner fluidList;

    CvKvFragment cv;
    FlowFragment fl;
    PressureDropFragment pd;

    protected RelativeLayout fullLayout;
    protected android.support.v4.view.ViewPager actContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public void setContentView(final int layoutResID) {

        XmlPullParserFactory pullParserFactory;

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = this.getResources().openRawResource(R.raw.fluidlist);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseFluidXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fullLayout = (RelativeLayout)getLayoutInflater().inflate(R.layout.activity_main, null);
        actContent = (ViewPager)fullLayout.findViewById(R.id.pager);
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        fluidList = (Spinner)findViewById(R.id.fluid_list);
        final FluidsXmlAdapter fluidsXmlAdapter = new FluidsXmlAdapter(fluids);
        fluidList.setAdapter(fluidsXmlAdapter);
        fluidList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Fluid f = (Fluid)parent.getItemAtPosition(pos);
                selectedFluid = f;
                if (f.getFormula() == null || f.getFormula().isEmpty()) {
                    formulaField.setText("");
                    if (f.getState().equals("gas")){
                        formulaImage.setImageResource(R.drawable.air);
                    } else {
                        formulaImage.setImageResource(R.drawable.liquid);
                    }
                } else {
                    formulaImage.setImageDrawable(null);
                    formulaField.setText(Html.fromHtml(f.getFormula()));
                }
                if (f.getState().equals("gas")) {
                    if (cv != null) {
                        cv.setTemperatureEnabled(true);
                    }
                    if (fl != null) {
                        fl.setTemperatureEnabled(true);
                    }
                    if (pd != null) {
                        pd.setTemperatureEnabled(true);
                    }
                } else {
                    if (cv != null) {
                        cv.setTemperatureEnabled(false);
                    }
                    if (fl != null) {
                        fl.setTemperatureEnabled(false);
                    }
                    if (pd != null) {
                        pd.setTemperatureEnabled(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
        fluidList.post(new Runnable() {
            @Override
            public void run() {
                for (Fluid fluid : fluids) {
                    if (fluid.getFluidName().equals(getResources().getString(R.string.air))) {
                        fluidList.setSelection(fluids.indexOf(fluid));
                        break;
                    }
                }
            }
        });

        formulaField = (TextView)findViewById(R.id.fluid_symbol_text);
        formulaField.setText(Html.fromHtml(((Fluid)fluidList.getSelectedItem()).getFormula()));

        formulaImage = (ImageView)findViewById(R.id.fluid_symbol_image);
    }

    private class FluidsXmlAdapter extends BaseAdapter implements SpinnerAdapter {

        private final List<Fluid> data;

        public FluidsXmlAdapter(List<Fluid> data) {
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
                LayoutInflater vi = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.spinner_dropdown_item, parent, false);
            }
            ((TextView)convertView).setText(data.get(position).getFluidName());
            return convertView;
        }

        @Override
        public View getView(int position, View recycle, ViewGroup parent) {
            TextView text;
            if (recycle != null) {
                text = (TextView)recycle;
            } else {
                text = (TextView)getLayoutInflater().inflate(
                        R.layout.spinner_item, parent, false
                );
            }
            text.setText(data.get(position).getFluidName());
            return text;
        }
    }

    private void parseFluidXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        Fluid currentFluid = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    fluids = new ArrayList<Fluid>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("Fluid")) {
                        currentFluid = new Fluid();
                    } else if (currentFluid != null) {
                        if (name.equals("DisplayName")) {
                            currentFluid.setFluidName(parser.nextText());
                        } else if (name.equals("Formula")) {
                            currentFluid.setFormula(parser.nextText());
                        } else if (name.equals("Density")) {
                            currentFluid.setDensity(Float.parseFloat(parser.nextText()));
                        } else if (name.equals("State")) {
                            currentFluid.setState(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Fluid") && currentFluid != null) {
                        fluids.add(currentFluid);
                    }
            }
            eventType = parser.next();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    cv = new CvKvFragment();
                    return cv;
                case 1:
                    fl = new FlowFragment();
                    return fl;
                case 2:
                    pd = new PressureDropFragment();
                    return pd;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void displayResultCard(String message) {
        Intent intent = new Intent(this,ResultCard.class);
        intent.putExtra("RESULT_TEXT", message);
        startActivity(intent);
    }
}
