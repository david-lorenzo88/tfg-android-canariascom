package com.canarias.rentacar.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.canarias.rentacar.R;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;

import java.util.List;

/**
 * Created by David on 07/09/2014.
 */
public class ZonePickerDialog extends Dialog {


    private final static String DEFAULT_ZONE_CODE = "10";

    private Spinner selectorZone;
    private Spinner selectorOffice;
    private Button btnAccept;
    private String mTag;

    private OnZoneChangedListener mCallback;

    private String selectedZoneCode;
    private String selectedOfficeCode;


    public ZonePickerDialog(Context context, String tag, String selectedOfficeCode, String selectedZoneCode) {
        super(context);
        mTag = tag;
        this.selectedOfficeCode = selectedOfficeCode;
        this.selectedZoneCode = selectedZoneCode;
    }

    public ZonePickerDialog(Context context, String tag) {
        super(context);
        mTag = tag;
    }

    public OnZoneChangedListener getCallback() {
        return mCallback;
    }

    public void setCallback(OnZoneChangedListener mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zone_dialog);

        selectorOffice = (Spinner) findViewById(R.id.selectorOffice);
        selectorZone = (Spinner) findViewById(R.id.selectorZone);
        btnAccept = (Button) findViewById(R.id.btnSelectZone);

        final ZoneDataSource zoneDS = new ZoneDataSource(getContext());
        final OfficeDataSource officeDS = new OfficeDataSource(getContext());

        try {
            zoneDS.open();
            officeDS.open();

            final List<Zone> zones = zoneDS.getZones();
            List<Office> offices = officeDS.getOffices(selectedZoneCode != null ? selectedZoneCode : DEFAULT_ZONE_CODE);

            selectorZone.setAdapter(new ArrayAdapter<Zone>(
                    getContext(),
                    R.layout.office_spinner,
                    R.id.office_item_name,
                    zones
            ));

            if (selectedZoneCode != null) {
                Zone selectedZone = zoneDS.getZone(Integer.parseInt(selectedZoneCode));
                if (selectedZone != null)
                    selectorZone.setSelection(zones.indexOf(selectedZone));
            }

            selectorOffice.setAdapter(new ArrayAdapter<Office>(
                    getContext(),
                    R.layout.office_spinner,
                    R.id.office_item_name,
                    offices
            ));

            if (selectedOfficeCode != null) {
                Office selectedOffice = officeDS.getOffice(selectedOfficeCode);
                if (selectedOffice != null)
                    selectorOffice.setSelection(zones.indexOf(selectedOffice));
            }

            selectorZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    OfficeDataSource ds = new OfficeDataSource(getContext());
                    try {
                        ds.open();
                        List<Office> offices = ds.getOffices(String.valueOf(zones.get(position).getCode()));

                        selectorOffice.setAdapter(new ArrayAdapter<Office>(
                                getContext(),
                                R.layout.office_spinner,
                                R.id.office_item_name,
                                offices
                        ));
                        ds.close();
                    } catch (Exception ex) {
                        Log.v("TEST", ex.getMessage());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            zoneDS.close();
            officeDS.close();


        } catch (Exception ex) {
            Log.e("TEST", ex.getMessage());
        }


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnZoneChangedListener callback = getCallback();
                if (callback != null) {
                    Zone zone = (Zone) selectorZone.getSelectedItem();
                    Office office = (Office) selectorOffice.getSelectedItem();

                    if (zone == null && office == null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.zoneOfficeValidator), Toast.LENGTH_LONG);
                        return;
                    }
                    if (zone == null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.zoneValidator), Toast.LENGTH_LONG);
                        return;
                    }

                    if (office == null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.officeValidator), Toast.LENGTH_LONG);
                        return;
                    }

                    callback.onZoneChanged(mTag, zone, office);

                }


                dismiss();
            }
        });

    }

    /**
     * The callback used to notify selected zone and office to source activity.
     */
    public interface OnZoneChangedListener {

        public void onZoneChanged(String tag, Zone zone, Office office);
    }
}
