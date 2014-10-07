package com.burkert.cvcalculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;



/**
 * Created by michael.weaver on 10/3/2014.
 */
public class ResultCard extends Activity {

    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String resultText = intent.getStringExtra("RESULT_TEXT");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.result_card);

        resultTextView = (TextView)findViewById(R.id.result_card_text);
        resultTextView.setText(resultText);

        this.setFinishOnTouchOutside(false);
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
}
