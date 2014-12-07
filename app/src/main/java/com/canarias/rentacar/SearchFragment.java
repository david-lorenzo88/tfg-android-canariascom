package com.canarias.rentacar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.CarDataSource;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.dialogs.CalendarDatePickerDialog;
import com.canarias.rentacar.dialogs.TimePickerDialog;
import com.canarias.rentacar.dialogs.ZonePickerDialog;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;
import com.canarias.rentacar.utils.AnimationHelper;
import com.canarias.rentacar.widgets.StatusRelativeLayout;
import com.google.android.gms.plus.model.people.Person;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Fragmento que muestra el formulario de búsqueda para buscar disponibilidad de vehículos.
 * Implementa las interfaces de los dialogos para el Calendario, Selector de Zona y
 * Selector de hora.
 */
public class SearchFragment extends Fragment implements CalendarDatePickerDialog.OnDateSetListener,
        ZonePickerDialog.OnZoneChangedListener, TimePickerDialog.OnTimeChangedListener {

    //Parameter names
    public static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    public static final String TAG_SELECTED_MODEL = "selected_model";
    public static final String TAG_PICKUP_DATE = "pickup_date";
    public static final String TAG_PICKUP_ZONE = "pickup_zone";
    public static final String TAG_PICKUP_TIME = "pickup_time";
    public static final String TAG_DROPOFF_DATE = "dropoff_date";
    public static final String TAG_DROPOFF_ZONE = "dropoff_zone";
    public static final String TAG_DROPOFF_TIME = "dropoff_time";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView pickupZoneLabel;
    private TextView dropoffZoneLabel;
    private TextView pickupDateLabel;
    private TextView dropoffDateLabel;
    private TextView pickupTimeLabel;
    private TextView dropoffTimeLabel;
    private StatusRelativeLayout pickupZoneLayout;
    private StatusRelativeLayout dropoffZoneLayout;
    private StatusRelativeLayout pickupDateLayout;
    private StatusRelativeLayout dropoffDateLayout;
    private StatusRelativeLayout pickupTimeLayout;
    private StatusRelativeLayout dropoffTimeLayout;
    private Button btnSearchCars;
    private Zone pickupZone;
    private Zone dropoffZone;
    private Office pickupOffice;
    private Office dropoffOffice;
    private String pickupDate;
    private String dropoffDate;
    private String pickupTime;
    private String dropoffTime;

    //Coche seleccionado (cuando se busca por coche)
    private Car selectedCar;

    //Estados de cada elemento
    private int pickupPointLayoutStatus = StatusRelativeLayout.STATUS_PENDING;
    private int dropoffPointLayoutStatus = StatusRelativeLayout.STATUS_PENDING;
    private int pickupDateLayoutStatus = StatusRelativeLayout.STATUS_PENDING;
    private int dropoffDateLayoutStatus = StatusRelativeLayout.STATUS_PENDING;
    private int pickupTimeLayoutStatus = StatusRelativeLayout.STATUS_PENDING;
    private int dropoffTimeLayoutStatus = StatusRelativeLayout.STATUS_PENDING;


    public SearchFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchFragment newInstance(int sectionNumber, String pickupOfficeCode,
                                             String dropoffOfficeCode) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(TAG_PICKUP_ZONE, pickupOfficeCode);
        args.putString(TAG_DROPOFF_ZONE, dropoffOfficeCode);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchFragment newInstance(int sectionNumber, String selectedModel) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(TAG_SELECTED_MODEL, selectedModel);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Crea el fragmento
     * @param savedInstanceState estado previo
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Restauramos el estado previo
        if (savedInstanceState != null) {


            pickupPointLayoutStatus = savedInstanceState.getInt(Config.ARG_PICKUP_POINT_LAYOUT_STATE, StatusRelativeLayout.STATUS_PENDING);
            dropoffPointLayoutStatus = savedInstanceState.getInt(Config.ARG_DROPOFF_POINT_LAYOUT_STATE, StatusRelativeLayout.STATUS_PENDING);
            pickupDateLayoutStatus = savedInstanceState.getInt(Config.ARG_PICKUP_DATE_LAYOUT_STATE, StatusRelativeLayout.STATUS_PENDING);
            dropoffDateLayoutStatus = savedInstanceState.getInt(Config.ARG_DROPOFF_DATE_LAYOUT_STATE, StatusRelativeLayout.STATUS_PENDING);
            pickupTimeLayoutStatus = savedInstanceState.getInt(Config.ARG_PICKUP_TIME_LAYOUT_STATE, StatusRelativeLayout.STATUS_PENDING);
            dropoffTimeLayoutStatus = savedInstanceState.getInt(Config.ARG_DROPOFF_TIME_LAYOUT_STATE, StatusRelativeLayout.STATUS_PENDING);


            String pickupPointCode = savedInstanceState.getString(Config.ARG_PICKUP_POINT);
            String dropoffPointCode = savedInstanceState.getString(Config.ARG_DROPOFF_POINT);

            OfficeDataSource officeDS = new OfficeDataSource(getActivity());

            try {
                officeDS.open();

                if (pickupPointCode != null) {
                    pickupOffice = officeDS.getOffice(pickupPointCode);
                }

                if (dropoffPointCode != null) {
                    dropoffOffice = officeDS.getOffice(dropoffPointCode);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                officeDS.close();
            }

            int pickupZoneCode = savedInstanceState.getInt(Config.ARG_PICKUP_ZONE);
            int dropoffZoneCode = savedInstanceState.getInt(Config.ARG_DROPOFF_ZONE);

            ZoneDataSource zoneDS = new ZoneDataSource(getActivity());

            try {
                zoneDS.open();

                if (pickupZoneCode != 0) {
                    pickupZone = zoneDS.getZone(pickupZoneCode);
                }
                if (dropoffZoneCode != 0) {
                    dropoffZone = zoneDS.getZone(dropoffZoneCode);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                zoneDS.close();
            }

            pickupDate = savedInstanceState.getString(Config.ARG_PICKUP_DATE);
            dropoffDate = savedInstanceState.getString(Config.ARG_DROPOFF_DATE);
            pickupTime = savedInstanceState.getString(Config.ARG_PICKUP_TIME);
            dropoffTime = savedInstanceState.getString(Config.ARG_DROPOFF_TIME);

            if(savedInstanceState.containsKey(Config.ARG_SELECTED_CAR)) {
                CarDataSource carDS = new CarDataSource(getActivity());

                try {
                    carDS.open();
                    selectedCar = carDS.getCar(savedInstanceState.getString(Config.ARG_SELECTED_CAR));

                } catch (SQLException ex) {

                    ex.printStackTrace();

                } finally {
                    carDS.close();
                }
            }


        } else if (getArguments() != null && getArguments().containsKey(TAG_PICKUP_ZONE)
                && getArguments().containsKey(TAG_DROPOFF_ZONE)) {

            //Hay oficinas seleccionadas, las cargamos

            String pickupCode = getArguments().getString(TAG_PICKUP_ZONE);
            String dropoffCode = getArguments().getString(TAG_DROPOFF_ZONE);

            OfficeDataSource officeDS = new OfficeDataSource(getActivity());

            try {
                officeDS.open();

                if (pickupCode != null) {
                    pickupOffice = officeDS.getOffice(pickupCode);
                }

                if (dropoffCode != null) {
                    dropoffOffice = officeDS.getOffice(dropoffCode);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                officeDS.close();
            }

            int pickupZoneCode = pickupOffice.getZone().getCode();
            int dropoffZoneCode = dropoffOffice.getZone().getCode();

            ZoneDataSource zoneDS = new ZoneDataSource(getActivity());

            try {
                zoneDS.open();

                if (pickupZoneCode != 0) {
                    pickupZone = zoneDS.getZone(pickupZoneCode);
                }
                if (dropoffZoneCode != 0) {
                    dropoffZone = zoneDS.getZone(dropoffZoneCode);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                zoneDS.close();
            }


        }
        else if(getArguments() != null && getArguments().containsKey(TAG_SELECTED_MODEL)){
            CarDataSource carDS = new CarDataSource(getActivity());
            //Hay un coche seleccionado, lo cargamos
            try{
                carDS.open();
                selectedCar = carDS.getCar(getArguments().getString(TAG_SELECTED_MODEL));

            } catch (SQLException ex){

                ex.printStackTrace();

            } finally {
                carDS.close();
            }
        }

    }


    /**
     * Llamado cuando el sistema operativo crea la vista del fragmento
     * @param inflater objeto para inflar las vistas
     * @param container la vista padre a la que el fragmento será asociado
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     * @return la vista generada para el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        initActivity(rootView);

        getActivity().getActionBar().setTitle(getString(R.string.title_fragment_new_booking));


        return rootView;
    }

    /**
     * Ejecutado cuando el fragmento se asocia a la activity
     * @param activity la activity
     */
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Inicializa la interfaz
     * @param rootView la vista raiz
     */
    private void initActivity(final View rootView) {

        pickupZoneLabel = (TextView) rootView.findViewById(R.id.pickupZoneDefaultLabel);
        dropoffZoneLabel = (TextView) rootView.findViewById(R.id.dropoffZoneDefaultLabel);
        pickupDateLabel = (TextView) rootView.findViewById(R.id.pickupDateDefaultLabel);
        dropoffDateLabel = (TextView) rootView.findViewById(R.id.dropoffDateDefaultLabel);
        pickupTimeLabel = (TextView) rootView.findViewById(R.id.pickupTimeDefaultLabel);
        dropoffTimeLabel = (TextView) rootView.findViewById(R.id.dropoffTimeDefaultLabel);

        if (pickupOffice != null && pickupZone != null) {
            pickupZoneLabel.setText(pickupOffice.getName() + " (" + pickupZone.getName() + ")");
        }

        if (dropoffOffice != null && dropoffZone != null) {
            dropoffZoneLabel.setText(dropoffOffice.getName() + " (" + dropoffZone.getName() + ")");
        }



        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);

        if (pickupDate == null)
            pickupDate = sdf.format(cal.getTime());

        cal.add(Calendar.DATE, 5);

        if (dropoffDate == null)
            dropoffDate = sdf.format(cal.getTime());

        if (pickupTime == null)
            pickupTime = "12:00";
        if (dropoffTime == null)
            dropoffTime = "12:00";


        pickupDateLabel.setText(pickupDate);
        dropoffDateLabel.setText(dropoffDate);
        pickupTimeLabel.setText(pickupTime);
        dropoffTimeLabel.setText(dropoffTime);

        pickupZoneLayout = (StatusRelativeLayout) rootView.findViewById(R.id.pickupZoneLayout);

        pickupZoneLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ZonePickerDialog dialog = new ZonePickerDialog(rootView.getContext(), TAG_PICKUP_ZONE);

                dialog.setTitle(R.string.pickupPointTitle);

                dialog.setCallback(SearchFragment.this);

                dialog.show();
            }
        });
        pickupZoneLayout.setStatus(pickupPointLayoutStatus);

        dropoffZoneLayout = (StatusRelativeLayout) rootView.findViewById(R.id.dropoffZoneLayout);

        dropoffZoneLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final ZonePickerDialog dialog = new ZonePickerDialog(rootView.getContext(), TAG_DROPOFF_ZONE);

                dialog.setTitle(R.string.dropoffPointTitle);

                dialog.setCallback(SearchFragment.this);

                dialog.show();
            }
        });

        dropoffZoneLayout.setStatus(dropoffPointLayoutStatus);

        pickupDateLayout = (StatusRelativeLayout) rootView.findViewById(R.id.pickupDateLayout);

        pickupDateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.parse(pickupDate);
                    cal = sdf.getCalendar();
                } catch (Exception ex) {
                    Log.v("TEST", ex.getMessage());
                }

                final CalendarDatePickerDialog dialog = CalendarDatePickerDialog.
                        newInstance(SearchFragment.this,
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH),
                                TAG_PICKUP_DATE,
                                null
                        );

                dialog.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
        pickupDateLayout.setStatus(pickupDateLayoutStatus);

        dropoffDateLayout = (StatusRelativeLayout) rootView.findViewById(R.id.dropoffDateLayout);

        dropoffDateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.parse(dropoffDate);
                    cal = sdf.getCalendar();
                } catch (Exception ex) {
                    Log.v("TEST", ex.getMessage());
                }

                Calendar validateCal = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.parse(pickupDate);
                    validateCal = sdf.getCalendar();
                } catch (Exception ex) {
                    Log.v("TEST", ex.getMessage());
                }

                final CalendarDatePickerDialog dialog = CalendarDatePickerDialog.
                        newInstance(SearchFragment.this,
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH),
                                TAG_DROPOFF_DATE,
                                validateCal

                        );

                dialog.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
        dropoffDateLayout.setStatus(dropoffDateLayoutStatus);

        pickupTimeLayout = (StatusRelativeLayout) rootView.findViewById(R.id.pickupTimeLayout);

        pickupTimeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(rootView.getContext(), TAG_PICKUP_TIME);
                dialog.setCallback(SearchFragment.this);
                dialog.setTitle(R.string.pickupTimeDialogTitle);
                dialog.show();
            }
        });
        pickupTimeLayout.setStatus(pickupTimeLayoutStatus);

        dropoffTimeLayout = (StatusRelativeLayout) rootView.findViewById(R.id.dropoffTimeLayout);

        dropoffTimeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(rootView.getContext(), TAG_DROPOFF_TIME);
                dialog.setCallback(SearchFragment.this);
                dialog.setTitle(R.string.dropoffTimeDialogTitle);
                dialog.show();
            }
        });
        dropoffTimeLayout.setStatus(dropoffTimeLayoutStatus);

        btnSearchCars = (Button) rootView.findViewById(R.id.btnSearchCars);

        //Boton de buscar disponibilidad
        btnSearchCars.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validamos el estado de cada item del formulario
                boolean valid = true;
                validateDateLayoutStatus();

                if (pickupZoneLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    pickupZoneLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (dropoffZoneLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    dropoffZoneLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (pickupDateLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    pickupDateLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (dropoffDateLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    dropoffDateLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (pickupTimeLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    pickupTimeLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (dropoffTimeLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    dropoffTimeLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }

                if (!valid) {
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.btnSearchCarsInvalidStatus),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                SearchResultsFragment fragment = SearchResultsFragment.
                        newInstance(getArguments().getInt(ARG_SECTION_NUMBER));

                Bundle args = new Bundle();

                //Todos los campos son validos, continuamos la busqueda

                args.putString(Config.ARG_PICKUP_POINT, pickupOffice.getCode());
                args.putString(Config.ARG_DROPOFF_POINT, dropoffOffice.getCode());
                args.putInt(Config.ARG_PICKUP_ZONE, pickupZone.getCode());
                args.putInt(Config.ARG_DROPOFF_ZONE, dropoffZone.getCode());
                args.putString(Config.ARG_PICKUP_DATE, pickupDate);
                args.putString(Config.ARG_DROPOFF_DATE, dropoffDate);
                args.putString(Config.ARG_PICKUP_TIME, pickupTime);
                args.putString(Config.ARG_DROPOFF_TIME, dropoffTime);

                if(selectedCar != null)
                    args.putString(Config.ARG_SELECTED_CAR, selectedCar.getModel());

                fragment.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();


            }
        });

        //Si hay un coche seleccionado, cargamos la interfaz

        if(selectedCar != null) {
            ImageView carImage = (ImageView) rootView.findViewById(R.id.carImage);
            ImageDownloader downloader = new ImageDownloader(9999, getActivity());
            downloader.download(selectedCar.getImageUrl(), carImage);

            ((TextView)rootView.findViewById(R.id.carModel)).setText(selectedCar.getModel());

            rootView.findViewById(R.id.selectedCarExternalWrap).setVisibility(View.VISIBLE);

            rootView.findViewById(R.id.btnRemoveSelectedCar).setOnClickListener(
                    new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog confirmDialog = new AlertDialog.Builder(getActivity()).create();

                    confirmDialog.setTitle(getString(R.string.remove_selected_car_dialog_title));

                    confirmDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    confirmDialog.dismiss();
                                }
                            });

                    confirmDialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.accept),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AnimationHelper.collapse(
                                            rootView.findViewById(R.id.selectedCarExternalWrap));

                                    selectedCar = null;

                                    confirmDialog.dismiss();
                                }
                            });


                    confirmDialog
                            .setMessage(getString(R.string.remove_selected_car_dialog_msg));
                    confirmDialog.show();
                }
            });
        }

    }

    /**
     * Guardamos el estado del fragmento para restaurarlo posteriormente
     * @param outState parametros para almacenar el estado actual
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (pickupOffice != null)
            outState.putString(Config.ARG_PICKUP_POINT, pickupOffice.getCode());
        if (dropoffOffice != null)
            outState.putString(Config.ARG_DROPOFF_POINT, dropoffOffice.getCode());
        if (pickupZone != null)
            outState.putInt(Config.ARG_PICKUP_ZONE, pickupZone.getCode());
        if (dropoffZone != null)
            outState.putInt(Config.ARG_DROPOFF_ZONE, dropoffZone.getCode());
        outState.putString(Config.ARG_PICKUP_DATE, pickupDate);
        outState.putString(Config.ARG_DROPOFF_DATE, dropoffDate);
        outState.putString(Config.ARG_PICKUP_TIME, pickupTime);
        outState.putString(Config.ARG_DROPOFF_TIME, dropoffTime);

        if (pickupZoneLayout != null)
            outState.putInt(Config.ARG_PICKUP_POINT_LAYOUT_STATE, pickupZoneLayout.getStatus());
        if (dropoffZoneLayout != null)
            outState.putInt(Config.ARG_DROPOFF_POINT_LAYOUT_STATE, dropoffZoneLayout.getStatus());
        if (pickupDateLayout != null)
            outState.putInt(Config.ARG_PICKUP_DATE_LAYOUT_STATE, pickupDateLayout.getStatus());
        if (dropoffDateLayout != null)
            outState.putInt(Config.ARG_DROPOFF_DATE_LAYOUT_STATE, dropoffDateLayout.getStatus());
        if (pickupTimeLayout != null)
            outState.putInt(Config.ARG_PICKUP_TIME_LAYOUT_STATE, pickupTimeLayout.getStatus());
        if (dropoffTimeLayout != null)
            outState.putInt(Config.ARG_DROPOFF_TIME_LAYOUT_STATE, dropoffTimeLayout.getStatus());

        if(selectedCar != null)
            outState.putString(Config.ARG_SELECTED_CAR, selectedCar.getModel());


        super.onSaveInstanceState(outState);
    }

    /**
     * Callback que establece la fecha seleccionada en el calendario
     * @param dialog      The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility with {@link java.util.Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     * @param tag Indica si es la fecha de recogida o la de devolucion
     */
    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth, String tag) {
        if (tag.equals(TAG_PICKUP_DATE)) {
            pickupDate = dayOfMonth + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year;
            pickupDateLabel.setText(pickupDate);

        } else if (tag.equals(TAG_DROPOFF_DATE)) {
            dropoffDate = dayOfMonth + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year;
            dropoffDateLabel.setText(dropoffDate);
        }

        validateDateLayoutStatus();
    }

    /**
     * Valida el estado de cada elemento y establece su nuevo estado
     */
    private void validateDateLayoutStatus() {
        Date pickupCal, dropoffCal;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dropoffCal = sdf.parse(dropoffDate + " " + dropoffTime);
            pickupCal = sdf.parse(pickupDate + " " + pickupTime);

            if (pickupOffice == null || pickupZone == null)
                pickupZoneLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
            else
                pickupZoneLayout.setStatus(StatusRelativeLayout.STATUS_OK);

            if (dropoffOffice == null || dropoffZone == null)
                dropoffZoneLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
            else
                dropoffZoneLayout.setStatus(StatusRelativeLayout.STATUS_OK);

            if (pickupCal.getTime() >= dropoffCal.getTime()) {
                dropoffDateLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                pickupDateLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                pickupTimeLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                dropoffTimeLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
            } else {
                pickupDateLayout.setStatus(StatusRelativeLayout.STATUS_OK);
                dropoffDateLayout.setStatus(StatusRelativeLayout.STATUS_OK);
                pickupTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);
                dropoffTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);
            }
        } catch (Exception ex) {
            Log.v("TEST", ex.getMessage());
        }
    }

    /**
     * Callback que establece la oficina y zona seleccionada desde el dialogo selector de zonas
     * @param tag indica si es la oficina de recogida o la de devolución
     * @param zone objeto zona
     * @param office objeto oficina
     */
    @Override
    public void onZoneChanged(String tag, Zone zone, Office office) {

        if (tag.equals(TAG_PICKUP_ZONE)) {
            pickupZoneLabel.setText(office.getName() + " (" + zone.getName() + ")");
            pickupZone = zone;
            pickupOffice = office;
            pickupZoneLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        } else if (tag.equals(TAG_DROPOFF_ZONE)) {
            dropoffZoneLabel.setText(office.getName() + " (" + zone.getName() + ")");
            dropoffZone = zone;
            dropoffOffice = office;
            dropoffZoneLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        }
    }

    /**
     * Callback que establece la hora seleccionada
     * @param tag indica si es la hora de recogida o la de devolución
     * @param time la hora
     */
    @Override
    public void onTimeChanged(String tag, String time) {
        if (tag.equals(TAG_PICKUP_TIME)) {
            pickupTime = time;
            pickupTimeLabel.setText(time);
            pickupTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        } else if (tag.equals(TAG_DROPOFF_TIME)) {
            dropoffTime = time;
            dropoffTimeLabel.setText(time);
            dropoffTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        }
    }
}
