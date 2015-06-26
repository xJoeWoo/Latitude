package ng.latitude.support.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;

import java.util.HashMap;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;

/**
 * Created by Ng on 15/6/24
 * <p>
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public final class InterfaceUtils {

    private static HashMap<View, ObjectAnimator> blinkingViews = new HashMap<>();

    /**
     * 摇动特定的 {@link EditText} 产生摇头效果并显示成红色
     *
     * @param editText 需要摇动的 {@link EditText}
     * @param clear    是否清空 {@link EditText} 里的内容
     */
    public static void shakeEditText(final EditText editText, final boolean clear) {

        final int textColor = editText.getCurrentTextColor();

        final ObjectAnimator oa = ObjectAnimator.ofFloat(editText, AnimPropertyName.TRANSLATION_X, 0f, Constants.ANIM_SHAKE_RANGE).setDuration(Constants.ANIM_SHAKE_DURATION);

        oa.setInterpolator(new CycleInterpolator(Constants.ANIM_SHAKE_TIMES));
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                editText.setTextColor(Latitude.getContext().getResources().getColor(R.color.red_warning));
                editText.getBackground().setColorFilter(Latitude.getContext().getResources().getColor(R.color.red_warning), PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                editText.setTextColor(textColor);
                editText.getBackground().setColorFilter(null);
                if (clear)
                    editText.setText("");
                oa.removeAllListeners();
            }
        });
        oa.start();
    }

    /**
     * 通过改变 {@code Alpha} 值闪烁特定的 {@link View}
     *
     * @param view  需要闪烁的 {@link View}
     * @param start 是否开始闪烁， {@code true} 为开始闪烁， {@code false} 为停止闪烁
     */
    public static void blinkView(View view, boolean start) {

        if (start && !blinkingViews.containsKey(view)) {
            ObjectAnimator oa = ObjectAnimator.ofFloat(view, AnimPropertyName.ALPHA, 1f, 0f).setDuration(Constants.ANIM_BUTTON_ALPHA_DURATION);
            oa.setInterpolator(new LinearInterpolator());
            oa.setRepeatMode(ObjectAnimator.REVERSE);
            oa.setRepeatCount(ObjectAnimator.INFINITE);
            oa.start();
            blinkingViews.put(view, oa);
        } else {
            blinkingViews.get(view).cancel();
            view.setAlpha(1f);
            blinkingViews.remove(view);
        }
    }

    public interface AnimPropertyName {
        String ALPHA = "alpha";
        String TRANSLATION_Y = "translationY";
        String TRANSLATION_X = "translationX";
    }
}
