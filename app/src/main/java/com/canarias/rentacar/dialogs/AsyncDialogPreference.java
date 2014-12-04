package com.canarias.rentacar.dialogs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.canarias.rentacar.async.DownloadCarsAsyncTask;
import com.canarias.rentacar.async.DownloadDestinationsAsyncTask;

/**
 * Created by David on 04/11/2014.
 * Diálogo usado en la pantalla de ajustes para realizar la descarga de oficinas y vehículos
 */
public class AsyncDialogPreference extends DialogPreference {


    public AsyncDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Callback ejecutado al cerrar el diálogo
     * @param positiveResult si se ha pulsado el botón positivo o no
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        //Obtenemos la clave
        String key = getKey();

        if (key.equals("download_offices")) {
            //Descargamos oficinas
            DownloadDestinationsAsyncTask task = new DownloadDestinationsAsyncTask(getContext());
            task.execute();
        } else if (key.equals("download_cars")) {
            //Descargamos vehículos
            DownloadCarsAsyncTask task = new DownloadCarsAsyncTask(getContext());
            task.execute();
        }
    }


}
