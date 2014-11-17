package com.canarias.rentacar.async;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.controller.WebServiceController;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.webservice.ErrorResponse;
import com.canarias.rentacar.model.webservice.GetExtrasResponse;
import com.canarias.rentacar.model.webservice.Response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GetExtrasAsyncTask extends
        AsyncTask<Void, Void, List<Extra>> {
    private HashMap<String, String> params;
    private Context context;
    private ErrorResponse error;
    private AlertDialog loadingDialog;
    private ProgressDialog progress;
    private LinearLayout extrasContainer;

    private LayoutInflater inflater;
    private HashMap<Integer, Integer> extrasQuantity;
    private ViewGroup container;
    List<Extra> result;

    public GetExtrasAsyncTask(Context context,
                              HashMap<String, String> params, LinearLayout extrasContainer,
                               LayoutInflater inflater,
                              ViewGroup container, HashMap<Integer, Integer> extrasQuantity) {
        this.params = params;
        this.context = context;
        this.extrasContainer = extrasContainer;

        this.inflater = inflater;
        this.extrasQuantity = extrasQuantity;
        this.container = container;



    }

    @Override
    protected List<Extra> doInBackground(Void... params) {
        // TODO Auto-generated method stub
        WebServiceController wsc = new WebServiceController(
        );
        Response result = wsc.getExtras(this.params.get(Config.ARG_PICKUP_DATE),
                this.params.get(Config.ARG_PICKUP_TIME),
                this.params.get(Config.ARG_DROPOFF_DATE),
                this.params.get(Config.ARG_DROPOFF_TIME),
                this.params.get(Config.ARG_PICKUP_POINT),
                this.params.get(Config.ARG_DROPOFF_POINT));

        if (result != null && result.getClass().equals(GetExtrasResponse.class)) {

            GetExtrasResponse resp = (GetExtrasResponse) result;

            Log.v("KEYS_MAP", "Bucle interno");
            for (Extra extra : resp.getExtras()) {
                Log.v("KEYS_MAP", "Resp: " + extra.getCode() + " - "+extra.getQuantity());
                if(extrasQuantity.containsKey(extra.getCode())){
                    extra.setQuantity(extrasQuantity.get(extra.getCode()));
                }
            }


            return resp.getExtras();
        } else {
            if (result != null && result.getClass().equals(ErrorResponse.class)) {
                error = (ErrorResponse) result;
            }
            return null;
        }
    }

    protected void onPostExecute(List<Extra> result) {
        this.result = result;
        progress.dismiss();
        if (result != null) {


            for (Extra extra : result) {
                Log.v("KEYS_MAP", "Bucle final");
                Log.v("KEYS_MAP", extra.getCode() + " - "+extra.getQuantity());
                View extraWrap = inflater.inflate(R.layout.extra_item, container, false);

                if (!extrasQuantity.containsKey(extra.getCode())) {
                    extrasQuantity.put(extra.getCode(), extra.getQuantity());
                }
                NumberPicker numPicker = (NumberPicker) extraWrap.findViewById(R.id.extraSelector);
                numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        extrasQuantity.put(Integer.parseInt(picker.getTag().toString()), newVal);
                        Log.v("KEYS_MAP", "onValueChange");
                        printMap();
                    }
                });
                ImageView extraIcon = (ImageView) extraWrap.findViewById(R.id.extraIcon);
                if (Extra.extraIcons.containsKey(String.valueOf(extra.getModelCode()))) {
                    //Set icon from mapping
                    extraIcon.setImageDrawable(context.getResources()
                            .getDrawable(Extra.extraIcons.get(String.valueOf(extra.getModelCode()))));
                } else {
                    //Set default icon
                    extraIcon.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_action_new));
                }

                TextView extraName = (TextView) extraWrap.findViewById(R.id.extraName);
                extraName.setText(extra.getName());

                TextView extraPrice = (TextView) extraWrap.findViewById(R.id.extraPrice);
                if (Extra.extraPriceType.containsKey(String.valueOf(extra.getModelCode()))) {
                    //Set text
                    extraPrice.setText(
                            extra.getPrice() + "€ / " + context.getString(
                                    Extra.extraPriceType.get(String.valueOf(extra.getModelCode())).equals(Extra.PriceType.DAILY) ? R.string.priceTypeDaily : R.string.priceTypeTotal));
                } else {
                    //Set default text
                    extraPrice.setText(
                            extra.getPrice() + "€ / " + context.getString(R.string.priceTypeTotal));
                }

                numPicker.setMaxValue(9);
                numPicker.setMinValue(0);
                numPicker.setWrapSelectorWheel(false);
                numPicker.setTag(extra.getCode());
                if (extra != null && extrasQuantity.containsKey(extra.getCode())) {
                    Log.v("KEYS", String.valueOf(extra.getCode()));
                    numPicker.setValue(extrasQuantity.get(extra.getCode()));
                } else {
                    numPicker.setValue(0);
                }

                extrasContainer.addView(extraWrap);

            }
            Log.v("KEYS_MAP", "FINAL");
            printMap();


        } else {
            //Error
            //Check for error


            loadingDialog = new AlertDialog.Builder(context).create();

            loadingDialog.setTitle(context.getString(R.string.cancel_reservation));


            loadingDialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.dismiss();
                        }
                    });

            if (error != null) {
                loadingDialog.setMessage(error.getCode() + error.getDescription());
            } else {
                loadingDialog.setMessage(context.getString(R.string.error));
            }


            loadingDialog.show();


        }
    }

    protected void onPreExecute() {

        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.getting_extras_title));
        progress.setMessage(context.getString(R.string.getting_extras));
        progress.show();


    }


    private void printMap() {
        Iterator<Integer> it = extrasQuantity.keySet().iterator();
        while (it.hasNext()) {
            Integer c = it.next();
            Log.v("KEYS_MAP", c.toString() + " - " + extrasQuantity.get(c).toString());
        }
    }

    public HashMap<Integer, Integer> getExtrasQuantity() {
        Log.v("KEYS_MAP", "Getter");
        printMap(); return this.extrasQuantity;
    }

    public List<Extra> getResult(){ return result; }
}