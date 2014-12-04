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

/**
 * Created by David on 30/10/2014.
 * Adapter que gestiona la lista de imágenes del Slider
 * En este caso hereda de PagerAdapter a diferencia del resto.
 * Está preparado para gestionar las páginas que contiene un ViewPager
 * que en este caso se usará para mostrar el contenido de la Ayuda.
 */
public class ImageSlideAdapter extends PagerAdapter {

    //Items de la ayuda
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

    /**
     * Crea la página para la posición dada
     * @param container ViewPager
     * @param position posición actual
     * @return la página de la posición
     */
	@Override
	public View instantiateItem(ViewGroup container, final int position) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.vp_image, container, false);

        HelpSlide item = items.get(position);

        if(item != null) {

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(item.getTitle());

            TextView titleAction = (TextView) view.findViewById(R.id.titleAction);
            titleAction.setText(item.getTitleAction());

            TextView description = (TextView) view.findViewById(R.id.description);
            description.setText(item.getDescription());

            ImageView image = (ImageView) view.findViewById(R.id.image);
            image.setImageDrawable(context.getResources().getDrawable(item.getImageDrawableId()));
        }
		container.addView(view);
		return view;

	}

    /**
     * Elimina una página
     * @param container ViewPager
     * @param position posición a eliminar
     * @param object objeto a eliminar
     */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

    /**
     * Determina si una vista está asociada con un objeto
     * @param view la vista
     * @param object el objeto
     * @return true si la vista está asociada al objeto, o false en
     * caso contrario
     */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}


}