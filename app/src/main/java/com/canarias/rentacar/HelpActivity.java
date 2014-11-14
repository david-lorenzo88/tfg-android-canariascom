package com.canarias.rentacar;

import android.app.Activity;
import android.os.Bundle;


public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, HelpFragmentList.newInstance())

                    .commit();
        }
    }



}
