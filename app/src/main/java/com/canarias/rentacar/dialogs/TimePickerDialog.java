package com.canarias.rentacar.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.canarias.rentacar.R;

/**
 * Created by David on 07/11/2014.
 * Diálogo usado para seleccionar la hora al realizar una búsqueda de disponibilidad
 */
public class TimePickerDialog extends Dialog {

    //Callback usado para avisar a la activity del valor seleccionado
    private OnTimeChangedListener mCallback;

    private TimePicker timePicker;

    private String mTag;
    private int hour = -1;
    private int minutes = -1;

    public TimePickerDialog(Context context, String tag, int hour, int minutes) {
        super(context);
        mTag = tag;
        this.hour = hour;
        this.minutes = minutes;
    }

    public TimePickerDialog(Context context, String tag) {
        super(context);
        mTag = tag;
    }

    /**
     * Obtiene el callback
     * @return el callback
     */
    public OnTimeChangedListener getCallback() {
        return mCallback;
    }

    /**
     * Establece el callback
     * @param callback el callback
     */
    public void setCallback(OnTimeChangedListener callback) {
        mCallback = callback;
    }

    /**
     * Ejecutado al crear el diálogo. Inicializa la interfaz gráfica
     * @param savedInstanceState Información de recuperación de Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_dialog);

        timePicker = (TimePicker) findViewById(R.id.time_picker);

        timePicker.setIs24HourView(true);

        if (hour != -1) {
            timePicker.setCurrentHour(hour);
        }
        if (minutes != -1) {
            timePicker.setCurrentMinute(minutes);
        }

        Button acceptBtn = (Button) findViewById(R.id.btnSelectHour);


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onTimeChanged(mTag,
                            timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                }
                dismiss();
            }
        });

    }

    /**
     * El Callback usado para notificar la hora seleccionada a la Activity fuente
     */
    public interface OnTimeChangedListener {

        public void onTimeChanged(String tag, String time);
    }
}
