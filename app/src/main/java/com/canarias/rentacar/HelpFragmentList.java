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
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpFragmentList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpFragmentList extends Fragment {

    private List<HelpSlide> items;


    public static HelpFragmentList newInstance() {
        HelpFragmentList fragment = new HelpFragmentList();

        return fragment;
    }

    public HelpFragmentList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_help_fragment_list, container, false);

        ListView lv = (ListView) v.findViewById(R.id.helpThemesListView);

        initHelpÌtems();

        lv.setDivider(null);
        lv.setDividerHeight(10);

        lv.setAdapter(new HelpListAdapter(getActivity(), R.layout.help_list_item, items));

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
