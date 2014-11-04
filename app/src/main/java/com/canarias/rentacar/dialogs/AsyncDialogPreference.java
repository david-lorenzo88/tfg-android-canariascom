package com.canarias.rentacar.dialogs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.canarias.rentacar.async.DownloadCarsAsyncTask;
import com.canarias.rentacar.async.DownloadDestinationsAsyncTask;

/**
 * Created by David on 04/09/2014.
 */
public class AsyncDialogPreference extends DialogPreference {


    public AsyncDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        String key = getKey();
        if (key.equals("download_offices")) {
            DownloadDestinationsAsyncTask task = new DownloadDestinationsAsyncTask(getContext());
            task.execute();
        } else if (key.equals("download_cars")) {
            DownloadCarsAsyncTask task = new DownloadCarsAsyncTask(getContext());
            task.execute();
        }
    }


}
