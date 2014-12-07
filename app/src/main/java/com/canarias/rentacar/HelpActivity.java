package com.canarias.rentacar;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity para mostrar la ayuda de la aplicación
 */
public class HelpActivity extends Activity {
    /**
     * Crea la activity. Genera una transicion para mostrar
     * el fragment con la lista de temas de ayuda.
     * @param savedInstanceState
     */
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
