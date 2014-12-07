package com.canarias.rentacar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.canarias.rentacar.adapters.ExtrasAdapter;
import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;
import com.canarias.rentacar.utils.AnimationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Fragment que muestra los extras disponibles para reservar durante el proceso de reserva
 */
public class SelectExtrasFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    //Nombres de los parametros
    private static final String ARG_AVAILABILITY_IDENTIFIER = "availability_identifier";
    private static final String ARG_CAR_IMAGE = "car_image";
    private static final String ARG_CAR_PRICE = "car_price";
    private static final String ARG_CAR_MODEL = "car_model";
    private static final String ARG_EXTRAS = "extras";

    //Estado del panel del resumen
    private static final int SUMMARY_STATUS_EXPANDED = 0;
    private static final int SUMMARY_STATUS_COLLAPSED = 1;
    private int mSummaryStatus = SUMMARY_STATUS_EXPANDED;

    private SelectExtrasFragment mReference;

    private HashMap<Integer, Integer> extrasQuantity;

    private ListView extrasListView;

    private String extrasSavedState = null;

    public SelectExtrasFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SelectExtrasFragment newInstance(int sectionNumber, String availabilityIdentifier,
                                                   String carImage, float carPrice, String carModel,
                                                   String extrasString) {
        SelectExtrasFragment fragment = new SelectExtrasFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_AVAILABILITY_IDENTIFIER, availabilityIdentifier);
        args.putString(ARG_CAR_IMAGE, carImage);
        args.putFloat(ARG_CAR_PRICE, carPrice);
        args.putString(ARG_CAR_MODEL, carModel);
        args.putString(SearchResultsFragment.ARG_EXTRAS_STRING, extrasString);
        fragment.setArguments(args);
        fragment.setReference(fragment);
        return fragment;
    }

    public void setReference(SelectExtrasFragment reference) {
        mReference = reference;
    }

    /**
     * Crea el fragment
     * @param savedInstanceState estado previo
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReference = this;

        extrasQuantity = new HashMap<Integer, Integer>();

        if(savedInstanceState != null){
            //Restauramos el estado previo
            String extras = savedInstanceState.getString(Config.ARG_EXTRAS);

            if(extras != null && !extras.isEmpty()){
                extrasSavedState = extras;
            }


            if(extras != null && !extras.isEmpty()){
                String[] parts = extras.split(";");
                for(String e : parts){
                    String[] eParts = e.split("-");
                    if(eParts.length == 2){
                        try {
                            extrasQuantity.put(Integer.parseInt(eParts[0]), Integer.parseInt(eParts[1]));
                        }catch (NumberFormatException ex){
                            ex.printStackTrace();
                        }
                    }
                }
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
        // Inflamos la interfaz
        final View rootView = inflater.inflate(R.layout.fragment_select_extras, container, false);

        Bundle args = getArguments();

        LinearLayout changeBtn = (LinearLayout) rootView.findViewById(R.id.changeBtn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        final List<Extra> extras = new ArrayList<Extra>();

        //Lista de extras desde string serializado
        //Formato: code##modelCode##extraName##dayPrice__code##modelCode##extraName##dayPrice...
        String serializedExtras = args.getString(SearchResultsFragment.ARG_EXTRAS_STRING);
        if (serializedExtras != null) {
            String[] extrasParts = serializedExtras.split("__");
            if (extrasParts.length > 0) {
                //Hay al menos un extra
                for (String extra : extrasParts) {

                    String[] extraParts = extra.split("##");
                    if (extraParts.length == 4) {
                        //El formato es correcto
                        Extra oExtra = new Extra();
                        oExtra.setPrice(Float.parseFloat(extraParts[3]));
                        oExtra.setName(extraParts[2]);
                        oExtra.setModelCode(Integer.parseInt(extraParts[1]));
                        oExtra.setCode(Integer.parseInt(extraParts[0]));

                        if(extrasQuantity.containsKey(oExtra.getCode())){
                            oExtra.setQuantity(extrasQuantity.get(oExtra.getCode()));
                        }

                        extras.add(oExtra);
                    }
                }
            }
        }

        extrasListView = (ListView) rootView.findViewById(R.id.listViewSelectExtras);
        extrasListView.setAdapter(new ExtrasAdapter(getActivity(), R.layout.extra_item, extras));

        TextView lblPickupPoint = (TextView) rootView.findViewById(R.id.selectExtrasPickupPointValue);
        TextView lblDropoffPoint = (TextView) rootView.findViewById(R.id.selectExtrasDropoffPointValue);
        TextView lblPickupDateTime = (TextView) rootView.findViewById(R.id.selectExtraspickupDateValue);
        TextView lblDropoffDateTime = (TextView) rootView.findViewById(R.id.selectExtrasDropoffDateValue);

        ImageView carImage = (ImageView) rootView.findViewById(R.id.selectExtrasImageCar);
        TextView carModel = (TextView) rootView.findViewById(R.id.selectExtrasCarModel);
        TextView carPrice = (TextView) rootView.findViewById(R.id.selectExtrasTxtCarPrice);

        ImageDownloader downloader = new ImageDownloader(9999, getActivity());
        downloader.download(args.getString(ARG_CAR_IMAGE), carImage);

        carModel.setText(args.getString(ARG_CAR_MODEL));

        carPrice.setText(String.format("%.02f", args.getFloat(ARG_CAR_PRICE)) + "€");

        //Cargamos zonas y oficinas
        ZoneDataSource zoneDS = new ZoneDataSource(getActivity());
        OfficeDataSource officeDS = new OfficeDataSource(getActivity());

        try {
            zoneDS.open();
            officeDS.open();

            Office pickupPoint = officeDS.getOffice(args
                    .getString(Config.ARG_PICKUP_POINT));
            Office dropoffPoint = officeDS.getOffice(args
                    .getString(Config.ARG_DROPOFF_POINT));

            Zone pickupZone = zoneDS.getZone(args.getInt(Config.ARG_PICKUP_ZONE));
            Zone dropoffZone = zoneDS.getZone(args.getInt(Config.ARG_DROPOFF_ZONE));

            lblPickupPoint.setText(pickupPoint.getName() + " (" + pickupZone.getName() + ")");
            lblDropoffPoint.setText(dropoffPoint.getName() + " (" + dropoffZone.getName() + ")");
            lblPickupDateTime.setText(
                    args.getString(Config.ARG_PICKUP_DATE) + " - "
                            + args.getString(Config.ARG_PICKUP_TIME));
            lblDropoffDateTime.setText(
                    args.getString(Config.ARG_DROPOFF_DATE) + " - "
                            + args.getString(Config.ARG_DROPOFF_TIME));


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally{
            zoneDS.close();
            officeDS.close();
        }

        final TextView summaryCollapsedText = (TextView) rootView.findViewById(R.id.selectExtrasSummaryCollapsedText);
        summaryCollapsedText.setText(
                getActivity().getString(R.string.showSummary)
                        + " (" + String.format("%.02f", args.getFloat(ARG_CAR_PRICE)) + "€)");


        final ImageView collapseBtn = (ImageView) rootView.findViewById(R.id.selectExtrasIconCollapse);
        collapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSummaryStatus == SUMMARY_STATUS_EXPANDED) {
                    AnimationHelper.collapse(rootView.findViewById(R.id.selectExtrasSummary));
                    mSummaryStatus = SUMMARY_STATUS_COLLAPSED;
                    summaryCollapsedText.setVisibility(View.VISIBLE);
                    AnimationHelper.rotate(collapseBtn, 0, 180, 500);
                } else {
                    AnimationHelper.expand(rootView.findViewById(R.id.selectExtrasSummary));
                    mSummaryStatus = SUMMARY_STATUS_EXPANDED;
                    summaryCollapsedText.setVisibility(View.GONE);
                    AnimationHelper.rotate(collapseBtn, 180, 0, 500);
                }
            }
        });

        //Boton continuar
        Button continueBtn = (Button) rootView.findViewById(R.id.btnSelectExtras);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = mReference.getArguments();

                ExtrasAdapter adapter = (ExtrasAdapter) extrasListView.getAdapter();

                HashMap<Integer, Integer> extrasQuantity = adapter.getExtrasQuantity();


                Iterator<Integer> it = extrasQuantity.keySet().iterator();

                String sExtras = "";

                while (it.hasNext()) {
                    Integer c = it.next();
                    if (!extrasQuantity.get(c).equals(0)) {
                        Extra e = searchExtra(extras, c.intValue());
                        sExtras += c + ";" + extrasQuantity.get(c) + ";" + e.getName() + ";" + e.getPrice() + ";" + e.getModelCode() + "#";
                    }
                }

                if (sExtras.length() > 0) {
                    args.putString(ARG_EXTRAS, sExtras.substring(0, sExtras.length() - 1));
                } else {
                    args.putString(ARG_EXTRAS, "");
                }
                MakeBookingFragment fragment = MakeBookingFragment.newInstance();

                fragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        //Configuracion para smartphones
        boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
        if(!isTablet) {
            new CountDownTimer(700, 700) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    AnimationHelper.collapse(rootView.findViewById(R.id.selectExtrasSummary));
                    mSummaryStatus = SUMMARY_STATUS_COLLAPSED;
                    summaryCollapsedText.setVisibility(View.VISIBLE);
                    AnimationHelper.rotate(collapseBtn, 0, 180, 500);
                }
            }.start();
        }
        getActivity().getActionBar().setTitle(getString(R.string.title_fragment_new_booking) + " - " + getString(R.string.title_fragment_select_extras));


        return rootView;
    }

    /**
     * Almacenamos el estado del fragment
     * @param outState parametros para salvar el estado
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(extrasListView != null) {
            ExtrasAdapter adapter = (ExtrasAdapter) extrasListView.getAdapter();

            HashMap<Integer, Integer> extrasQuantity = adapter.getExtrasQuantity();

            Iterator<Integer> it = extrasQuantity.keySet().iterator();

            String extras = "";

            while (it.hasNext()) {
                int key = it.next();
                extras += key + "-" + extrasQuantity.get(key) + ";";
            }
            if (extras.length() > 0)
                extras = extras.substring(0, extras.length() - 1);
            outState.putString(Config.ARG_EXTRAS, extras);

        } else if(extrasSavedState != null)
            outState.putString(Config.ARG_EXTRAS, extrasSavedState);

        super.onSaveInstanceState(outState);
    }

    /**
     * Busca un extra en la lista de extras
     * @param extras lista de extras
     * @param code codigo del extra a buscar
     * @return el extra si se encontró o null en otro caso
     */
    private Extra searchExtra(List<Extra> extras, int code) {
        for (Extra e : extras) {
            if (e.getCode() == code)
                return e;
        }
        return null;
    }

    /**
     * Ejecutado al asociar el fragment a la activity
     * @param activity la activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


}
