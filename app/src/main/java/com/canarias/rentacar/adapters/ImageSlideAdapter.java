package com.canarias.rentacar.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.model.HelpSlide;

import java.util.List;


public class ImageSlideAdapter extends PagerAdapter {

	List<HelpSlide> items;
    Context context;

	public ImageSlideAdapter(Context context, List<HelpSlide> items) {
        this.context = context;
        this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();

	}

	@Override
	public View instantiateItem(ViewGroup container, final int position) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.vp_image, container, false);

        HelpSlide item = items.get(position);

        if(item != null) {

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(item.getTitle());

            TextView description = (TextView) view.findViewById(R.id.description);
            description.setText(item.getDescription());

            ImageView image = (ImageView) view.findViewById(R.id.image);
            image.setImageDrawable(context.getResources().getDrawable(item.getImageDrawableId()));
        }
		container.addView(view);
		return view;

	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}


}