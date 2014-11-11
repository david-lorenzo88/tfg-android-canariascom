package com.canarias.rentacar;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canarias.rentacar.adapters.ImageSlideAdapter;
import com.canarias.rentacar.model.HelpSlide;
import com.canarias.rentacar.utils.CirclePageIndicator;
import com.canarias.rentacar.utils.PageIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpFragment extends Fragment {
    public static final String ARG_ITEM_ID = "help_fragment";

    private static final long ANIM_VIEWPAGER_DELAY = 5000;
    private static final long ANIM_VIEWPAGER_DELAY_USER_VIEW = 10000;

    private ViewPager mViewPager;
    PageIndicator mIndicator;

    private List<HelpSlide> items;

    boolean stopSliding = true;

    private Runnable animateViewPager;
    private Handler handler;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HelpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();

        return fragment;
    }

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        HelpSlide slide = new HelpSlide();
        slide.setDescription("Descripción 1");
        slide.setImageDrawableId(R.drawable.slide_homeactivity);
        slide.setTitle("Titulo 1");
        items.add(slide);

        slide = new HelpSlide();
        slide.setDescription("Descripción 2");
        slide.setImageDrawableId(R.drawable.slide_homeactivity_menu);
        slide.setTitle("Titulo 2");
        items.add(slide);

        slide = new HelpSlide();
        slide.setDescription("Descripción 3");
        slide.setImageDrawableId(R.drawable.slide_searchfragment);
        slide.setTitle("Titulo 3");
        items.add(slide);

        slide = new HelpSlide();
        slide.setDescription("Descripción 4");
        slide.setImageDrawableId(R.drawable.slide_searchfragment_selector_fecha);
        slide.setTitle("Titulo 4");
        items.add(slide);

        slide = new HelpSlide();
        slide.setDescription("Descripción 5");
        slide.setImageDrawableId(R.drawable.slide_searchfragment_selector_hora);
        slide.setTitle("Titulo 5");
        items.add(slide);

        slide = new HelpSlide();
        slide.setDescription("Descripción 6");
        slide.setImageDrawableId(R.drawable.slide_searchfragment_selector_zona);
        slide.setTitle("Titulo 6");
        items.add(slide);



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
