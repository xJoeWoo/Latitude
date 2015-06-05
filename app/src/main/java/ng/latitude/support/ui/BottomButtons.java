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
import android.widget.Button;
import android.widget.RelativeLayout;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;

/**
 * Created by Joe on 2015/5/26.
 */
public class BottomButtons extends RelativeLayout {

    private Button ok;
    private Button cancel;

    public BottomButtons(Context context) {
        super(context);
    }

    public BottomButtons(Context context, AttributeSet attrs) {
        super(context, attrs);

        View v = LayoutInflater.from(context).inflate(R.layout.widget_bottom_buttons, this);

        ok = (Button) v.findViewById(R.id.widget_bottom_buttons_ok);
        cancel = (Button) v.findViewById(R.id.widget_bottom_buttons_cancel);
    }

    public BottomButtons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnClickListener okListener, OnClickListener cancelListener) {
        ok.setOnClickListener(okListener);
        cancel.setOnClickListener(cancelListener);
    }


    public void show() {
        startAnim(true);
    }

    public void hide() {
        startAnim(false);
    }

    private void startAnim(final boolean visible) {

        final int height = getHeight();

        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("a", visible ? 0f : 1f, visible ? 1f : 0f);
        PropertyValuesHolder translation = PropertyValuesHolder.ofFloat("y", visible ? height : 0f, visible ? 0f : height);

        ValueAnimator oa = ObjectAnimator.ofPropertyValuesHolder(alpha, translation).setDuration(Constants.ANIM_COMMON_DURATION);

        oa.setInterpolator(new GravityInterpolator(visible));
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
            }
        });
        oa.start();
    }
}
