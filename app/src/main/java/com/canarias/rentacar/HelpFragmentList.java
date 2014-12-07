package com.canarias.rentacar;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.canarias.rentacar.adapters.HelpListAdapter;
import com.canarias.rentacar.model.HelpSlide;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragmento que muestra la lista de temas de ayuda
 */
public class HelpFragmentList extends Fragment {
    //Lista de items de ayuda
    private List<HelpSlide> items;

    /**
     * Crea una instancia del fragmento
     * @return el fragmento
     */
    public static HelpFragmentList newInstance() {
        HelpFragmentList fragment = new HelpFragmentList();

        return fragment;
    }

    public HelpFragmentList() {
        // Required empty public constructor
    }

    /**
     * Crea el fragmento
     * @param savedInstanceState estado previo
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Crea la vista del fragmento
     * @param inflater objeto para inflar las vistas
     * @param container la vista padre a la que el fragmento será asociado
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     * @return la vista generada para el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla la interfaz
        View v = inflater.inflate(R.layout.fragment_help_fragment_list, container, false);

        ListView lv = (ListView) v.findViewById(R.id.helpThemesListView);

        //Inicializa los temas de la ayuda
        initHelpÌtems();

        lv.setDivider(null);
        lv.setDividerHeight(10);

        lv.setAdapter(new HelpListAdapter(getActivity(), R.layout.help_list_item, items));
        //Establece el evento click para cada elemento de la lista
        //que abre la activity de presentaciones (SlideActivity)
        // con el tema seleccionado
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HelpSlide item = items.get(position);

                Intent intent = new Intent(getActivity(), SlideActivity.class);
                intent.putExtra(SlideActivity.ARG_ITEM_ID, item.getIdentifier());

                startActivity(intent);


            }
        });

        return v;
    }

    /**
     * Inicializa la lista de items de ayuda
     */
    private void initHelpÌtems() {

        items = new ArrayList<HelpSlide>();

        HelpSlide item = new HelpSlide();
        item.setTitleAction(getString(R.string.help_newbook_title_action));
        item.setSubtitle(getString(R.string.help_newbook_subtitle_action));
        item.setIdentifier(SlideFragment.TYPE_NEW_BOOKING);
        items.add(item);

        item = new HelpSlide();
        item.setTitleAction(getString(R.string.help_cancel_update_book_title_action));
        item.setSubtitle(getString(R.string.help_cancel_update_book_subtitle_action));
        item.setIdentifier(SlideFragment.TYPE_CANCEL_UPDATE_BOOKING);
        items.add(item);

        item = new HelpSlide();
        item.setTitleAction(getString(R.string.help_search_car_title_action));
        item.setSubtitle(getString(R.string.help_search_car_subtitle_action));
        item.setIdentifier(SlideFragment.TYPE_SEARCH_CAR);
        items.add(item);

        item = new HelpSlide();
        item.setTitleAction(getString(R.string.help_search_office_title_action));
        item.setSubtitle(getString(R.string.help_search_office_subtitle_action));
        item.setIdentifier(SlideFragment.TYPE_SEARCH_OFFICE);
        items.add(item);

        item = new HelpSlide();
        item.setTitleAction(getString(R.string.help_settings_title_action));
        item.setSubtitle(getString(R.string.help_settings_subtitle_action));
        item.setIdentifier(SlideFragment.TYPE_SETTINGS);
        items.add(item);


    }
}
