package com.canarias.rentacar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SlideActivity extends ActionBarActivity {
    public static String ARG_ITEM_ID = "item_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Bundle args = getIntent().getExtras();


        if (savedInstanceState == null && args != null && args.getString(ARG_ITEM_ID) != null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, SlideFragment.newInstance(args.getString(ARG_ITEM_ID)))
                    .commit();
        }
    }



}
