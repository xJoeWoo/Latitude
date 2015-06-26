package ng.latitude.support.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;

/**
 * Created by Ng on 15/6/3
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class BottomInfo extends RelativeLayout {

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

    public void show() {
        if (!state) {
            state = true;
            startAnim(true);
            startBlink();

        }
    }

    public void hide() {
        if (state) {
            state = false;
            startAnim(false);
            stopBlink();
        }
    }

    private void startAnim(final boolean visible) {

        final int height = getHeight();

        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("a", visible ? 0f : 1f, visible ? 1f : 0f);
        PropertyValuesHolder translation = PropertyValuesHolder.ofFloat("y", visible ? height : 0f, visible ? 0f : height);

        final ValueAnimator oa = ObjectAnimator.ofPropertyValuesHolder(alpha, translation).setDuration(Constants.ANIM_COMMON_DURATION);

        oa.setInterpolator(GravityInterpolator.getInstance(visible));
        oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setAlpha((float) animation.getAnimatedValue("a"));
                setTranslationY((float) animation.getAnimatedValue("y"));
            }
        });
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (visible)
                    setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!visible)
                    setVisibility(GONE);
                oa.removeAllUpdateListeners();
                oa.removeAllListeners();
            }
        });
        oa.start();
    }

    public void startBlink() {
        oa = ObjectAnimator.ofFloat(main, InterfaceUtils.AnimPropertyName.ALPHA, 1f, 0f).setDuration((long) (Constants.ANIM_BUTTON_ALPHA_DURATION * 1.5));
        oa.setInterpolator(new LinearInterpolator());
        oa.setRepeatCount(ValueAnimator.INFINITE);
        oa.setRepeatMode(ValueAnimator.REVERSE);
        oa.start();
    }

    public void stopBlink() {
//        oa.cancel();
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
