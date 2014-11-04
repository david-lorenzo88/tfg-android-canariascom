package com.canarias.rentacar.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.canarias.rentacar.R;

/**
 * Created by David on 09/09/2014.
 */
public class StatusRelativeLayout extends RelativeLayout {

    public static final int STATUS_OK = 1;
    public static final int STATUS_PENDING = 2;
    public static final int STATUS_ERROR = 3;

    private int status;

    public StatusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StatusRelativeLayout(Context context) {
        super(context);
    }

    private void processStatus(int status) {

        switch (status) {
            case STATUS_OK:
                setBackgroundResource(R.drawable.border_bottom);
                setPadding(20, 20, 20, 20);
                break;
            case STATUS_ERROR:
                //setBackgroundColor(getResources().getColor(R.color.status_error));
                setBackgroundResource(R.drawable.border_bottom_failed);
                setPadding(20, 20, 20, 20);
                break;
            case STATUS_PENDING:
                //setBackgroundColor(getResources().getColor(R.color.status_pending));
                setBackgroundResource(R.drawable.border_bottom);
                setPadding(20, 20, 20, 20);
                break;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;

        processStatus(status);
    }

}
