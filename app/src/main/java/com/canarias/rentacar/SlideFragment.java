package com.canarias.rentacar;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.canarias.rentacar.adapters.ImageSlideAdapter;
import com.canarias.rentacar.model.HelpSlide;
import com.canarias.rentacar.utils.CirclePageIndicator;
import com.canarias.rentacar.utils.PageIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SlideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlideFragment extends Fragment {
    public static final String ARG_ITEM_ID = "slide_fragment";

    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private static final long ANIM_VIEWPAGER_DELAY_USER_VIEW = 10000;

    public static final String TYPE_NEW_BOOKING = "new_booking";
    public static final String TYPE_SEARCH_CAR = "search_car";
    public static final String TYPE_SEARCH_OFFICE = "search_office";
    public static final String TYPE_CANCEL_UPDATE_BOOKING = "cancel_update_booking";
    public static final String TYPE_SETTINGS = "settings";

    private ViewPager mViewPager;
    PageIndicator mIndicator;

    private List<HelpSlide> items;

    boolean stopSliding = true;

    private Runnable animateViewPager;
    private Handler handler;

    private String mType;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HelpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SlideFragment newInstance(String type) {
        SlideFragment fragment = new SlideFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, type);
        fragment.setArguments(args);
        return fragment;
    }

    public SlideFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mType = getArguments().getString(ARG_ITEM_ID);
        }

        loadItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);

        mIndicator.setOnPageChangeListener(new PageChangeListener());
        mViewPager.setOnPageChangeListener(new PageChangeListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        // calls when touch release on ViewPager
                        if (items != null && items.size() != 0) {
                            //stopSliding = false;
                            runnable(items.size());
                            handler.postDelayed(animateViewPager,
                                    ANIM_VIEWPAGER_DELAY_USER_VIEW);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // calls when ViewPager touch
                        if (handler != null && stopSliding == false) {
                            //stopSliding = true;
                            handler.removeCallbacks(animateViewPager);
                        }
                        break;
                }
                return false;
            }
        });


        return view;
    }

    public void runnable(final int size) {
        handler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (mViewPager.getCurrentItem() == size - 1) {
                        mViewPager.setCurrentItem(0);
                    } else {
                        mViewPager.setCurrentItem(
                                mViewPager.getCurrentItem() + 1, true);
                    }
                    handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }

    @Override
    public void onResume() {
        if (items == null) {
            loadItems();
        } else {
            mViewPager.setAdapter(new ImageSlideAdapter(getActivity(), items));

            mIndicator.setViewPager(mViewPager);
            
            runnable(items.size());
            //Re-run callback
            handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
        }
        super.onResume();
    }

    @Override
    public void onPause() {

        if (handler != null) {
            //Remove callback
            handler.removeCallbacks(animateViewPager);
        }
        super.onPause();
    }

    private void loadItems() {

        items = new ArrayList<HelpSlide>();

        if(mType != null) {
            if (mType.equals(TYPE_NEW_BOOKING)) {

                HelpSlide slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_newbook_title_action));
                slide.setDescription(getString(R.string.help_newbook1_desc));
                slide.setImageDrawableId(R.drawable.newbook_1);
                slide.setTitle(getString(R.string.help_newbook1_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_newbook_title_action));
                slide.setDescription(getString(R.string.help_newbook2_desc));
                slide.setImageDrawableId(R.drawable.newbook_2);
                slide.setTitle(getString(R.string.help_newbook2_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_newbook_title_action));
                slide.setDescription(getString(R.string.help_newbook3_desc));
                slide.setImageDrawableId(R.drawable.newbook_3);
                slide.setTitle(getString(R.string.help_newbook3_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_newbook_title_action));
                slide.setDescription(getString(R.string.help_newbook4_desc));
                slide.setImageDrawableId(R.drawable.newbook_4);
                slide.setTitle(getString(R.string.help_newbook4_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_newbook_title_action));
                slide.setDescription(getString(R.string.help_newbook5_desc));
                slide.setImageDrawableId(R.drawable.newbook_5);
                slide.setTitle(getString(R.string.help_newbook5_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_newbook_title_action));
                slide.setDescription(getString(R.string.help_newbook6_desc));
                slide.setImageDrawableId(R.drawable.newbook_6);
                slide.setTitle(getString(R.string.help_newbook6_title));
                items.add(slide);

            } else if (mType.equals(TYPE_CANCEL_UPDATE_BOOKING)) {
                //TODO: Terminar todos los casos y crear un fragment previo con una lista de los temas de ayuda
                HelpSlide slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_cancel_update_book_title_action));
                slide.setDescription(getString(R.string.help_bookingdetail1_desc));
                slide.setImageDrawableId(R.drawable.bookingdetail_1);
                slide.setTitle(getString(R.string.help_bookingdetail1_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_cancel_update_book_title_action));
                slide.setDescription(getString(R.string.help_bookingdetail2_desc));
                slide.setImageDrawableId(R.drawable.bookingdetail_2);
                slide.setTitle(getString(R.string.help_bookingdetail2_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_cancel_update_book_title_action));
                slide.setDescription(getString(R.string.help_bookingdetail3_desc));
                slide.setImageDrawableId(R.drawable.bookingdetail_3);
                slide.setTitle(getString(R.string.help_bookingdetail3_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_cancel_update_book_title_action));
                slide.setDescription(getString(R.string.help_bookingdetail4_desc));
                slide.setImageDrawableId(R.drawable.bookingdetail_4);
                slide.setTitle(getString(R.string.help_bookingdetail4_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_cancel_update_book_title_action));
                slide.setDescription(getString(R.string.help_bookingdetail5_desc));
                slide.setImageDrawableId(R.drawable.bookingdetail_5);
                slide.setTitle(getString(R.string.help_bookingdetail5_title));
                items.add(slide);



            }else if (mType.equals(TYPE_SEARCH_CAR)) {
                //TODO: Terminar todos los casos y crear un fragment previo con una lista de los temas de ayuda
                HelpSlide slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_search_car_title_action));
                slide.setDescription(getString(R.string.help_searchcar1_desc));
                slide.setImageDrawableId(R.drawable.searchcar_1);
                slide.setTitle(getString(R.string.help_searchcar1_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_search_car_title_action));
                slide.setDescription(getString(R.string.help_searchcar2_desc));
                slide.setImageDrawableId(R.drawable.searchcar_2);
                slide.setTitle(getString(R.string.help_searchcar2_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_search_car_title_action));
                slide.setDescription(getString(R.string.help_searchcar3_desc));
                slide.setImageDrawableId(R.drawable.searchcar_3);
                slide.setTitle(getString(R.string.help_searchcar3_title));
                items.add(slide);
            } else if (mType.equals(TYPE_SEARCH_OFFICE)) {
                //TODO: Terminar todos los casos y crear un fragment previo con una lista de los temas de ayuda
                HelpSlide slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_search_office_title_action));
                slide.setDescription(getString(R.string.help_searchoffice1_desc));
                slide.setImageDrawableId(R.drawable.searchoffice_1);
                slide.setTitle(getString(R.string.help_searchoffice1_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_search_office_title_action));
                slide.setDescription(getString(R.string.help_searchoffice2_desc));
                slide.setImageDrawableId(R.drawable.searchoffice_2);
                slide.setTitle(getString(R.string.help_searchoffice2_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_search_office_title_action));
                slide.setDescription(getString(R.string.help_searchoffice3_desc));
                slide.setImageDrawableId(R.drawable.searchoffice_3);
                slide.setTitle(getString(R.string.help_searchoffice3_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_search_office_title_action));
                slide.setDescription(getString(R.string.help_searchoffice4_desc));
                slide.setImageDrawableId(R.drawable.searchoffice_4);
                slide.setTitle(getString(R.string.help_searchoffice4_title));
                items.add(slide);
            } else if (mType.equals(TYPE_SETTINGS)) {
                //TODO: Terminar todos los casos y crear un fragment previo con una lista de los temas de ayuda
                HelpSlide slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_settings_title_action));
                slide.setDescription(getString(R.string.help_settings1_desc));
                slide.setImageDrawableId(R.drawable.settings_1);
                slide.setTitle(getString(R.string.help_settings1_title));
                items.add(slide);

                slide = new HelpSlide();
                slide.setTitleAction(getString(R.string.help_settings_title_action));
                slide.setDescription(getString(R.string.help_settings2_desc));
                slide.setImageDrawableId(R.drawable.settings_2);
                slide.setTitle(getString(R.string.help_settings2_title));
                items.add(slide);
            }
        }

    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (items!= null) {
                    /*imgNameTxt.setText(""
                            + ((Product) products.get(mViewPager
                            .getCurrentItem())).getName());*/
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
        }
    }

}
