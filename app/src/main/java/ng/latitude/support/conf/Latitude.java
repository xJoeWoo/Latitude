package ng.latitude.support.conf;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Application;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.animation.CycleInterpolator;
import android.widget.EditText;

import ng.latitude.R;
import ng.latitude.support.bean.LoginBean;

/**
 * Created by Joe on 2015/5/24.
 */
public class Latitude extends Application {

    private static Context context;
    private static UserInfo userInfo;

    public static Context getContext() {
        return context;
    }

    public static void initUserInfo(LoginBean loginBean) {
        userInfo = UserInfo.newInstance(loginBean);
    }

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public static void shakeEditText(final EditText v, final boolean reset) {

        final int textColor = v.getCurrentTextColor();

        ObjectAnimator oa = ObjectAnimator.ofFloat(v, Constants.OBJECT_ANIM_TRANSLATION_X, 0f, Constants.ANIM_SHAKE_RANGE).setDuration(Constants.ANIM_SHAKE_DURATION);

        oa.setInterpolator(new CycleInterpolator(Constants.ANIM_SHAKE_TIMES));
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                v.setTextColor(context.getResources().getColor(R.color.red_warning));
                v.getBackground().setColorFilter(context.getResources().getColor(R.color.red_warning), PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setTextColor(textColor);
                v.getBackground().setColorFilter(null);
                if (reset)
                    v.setText("");
            }
        });
        oa.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
