package ng.latitude.support.conf;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Objects;

import ng.latitude.support.bean.LoginBean;
import ng.latitude.support.bean.LogonBean;

/**
 * Created by Joe on 2015/5/24.
 */
public class Latitude extends Application {

    private static Context context;
    private static LoginBean loginBean;

    public static Context getContext() {
        return context;
    }

    public static LoginBean getLoginBean() {
        return loginBean;
    }

    public static void setLoginBean(LoginBean loginBean) {
        Latitude.loginBean = loginBean;
    }

    public static String getToken(){
        return loginBean.getToken();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
