package ng.latitude.support.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;

/**
 * Created by Ng on 15/6/3.
 */
public class BottomInfo extends BottomButtons {

    private TextView main;
    private TextView sub;
    private ObjectAnimator oa;
    private boolean state = false;
    private int textColor;

    public BottomInfo(Context context) {
        super(context);
    }

    public BottomInfo(Context context, AttributeSet attrs) {
        super(context, attrs);

        View v = LayoutInflater.from(context).inflate(R.layout.widget_bottom_info, this);

        main = (TextView) v.findViewById(R.id.widget_bottom_info_main);
        sub = (TextView) v.findViewById(R.id.widget_bottom_info_sub);

        textColor = sub.getCurrentTextColor();

        startBlink();
    }

    public BottomInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void show() {
        if (!state) {
            state = true;
            super.show();
            startBlink();

        }
    }

    @Override
    public void hide() {
        if (state) {
            state = false;
            super.hide();
            stopBlink();
        }
    }

    public void startBlink() {
        oa = ObjectAnimator.ofFloat(main, Constants.OBJECT_ANIM_ALPHA, 1f, 0f).setDuration((long) (Constants.ANIM_BUTTON_ALPHA_DURATION * 1.5));
        oa.setInterpolator(new LinearInterpolator());
        oa.setRepeatCount(ValueAnimator.INFINITE);
        oa.setRepeatMode(ValueAnimator.REVERSE);
        oa.start();
    }

    public void stopBlink() {
        oa.cancel();
        main.setAlpha(1f);
    }

    public void setMainText(int id) {
        main.setText(id);
    }

    public void setSubText(int id) {
        sub.setText(id);

        if (id == R.string.widget_bottom_info_sub_gps_not_available)
            sub.setTextColor(getResources().getColor(R.color.red_warning));
        else
            sub.setTextColor(textColor);
    }

    public void reset() {
        main.setText(R.string.widget_bottom_info_main);
        sub.setText(R.string.widget_bottom_info_sub);
        main.setTextColor(textColor);
        sub.setTextColor(textColor);
        stopBlink();
    }
}
