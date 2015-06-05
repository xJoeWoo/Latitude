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
public class BottomInfo extends BottomButtons{

    private TextView main;
    private TextView sub;

    public BottomInfo(Context context) {
        super(context);
    }

    public BottomInfo(Context context, AttributeSet attrs) {
        super(context, attrs);

        View v = LayoutInflater.from(context).inflate(R.layout.widget_bottom_info, this);

        main = (TextView) v.findViewById(R.id.widget_bottom_info_main);
        sub = (TextView) v.findViewById(R.id.widget_bottom_info_sub);

        startBlink();
    }

    public BottomInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startBlink(){
        ObjectAnimator oa = ObjectAnimator.ofFloat(main, Constants.OBJECT_ANIM_ALPHA, 1f, 0f).setDuration((long)(Constants.ANIM_BUTTON_ALPHA_DURATION *1.5));
        oa.setInterpolator(new LinearInterpolator());
        oa.setRepeatCount(ValueAnimator.INFINITE);
        oa.setRepeatMode(ValueAnimator.REVERSE);
        oa.start();
    }

    public void stopBlink(){

    }
}
