package com.canarias.rentacar;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity que encapsula las presentaciones en slide de los temas de ayuda
 */
public class SlideActivity extends Activity {
    public static String ARG_ITEM_ID = "item_id";

    /**
     * Crea la activity
     * @param savedInstanceState estado previo
     */
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
