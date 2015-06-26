package ng.latitude.support.conf;

import android.app.Application;
import android.content.Context;

import ng.latitude.support.bean.LoginBean;

/**
 * Created by Ng on 15/5/24
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class Latitude extends Application {

    private static Context context;
    private static UserInfo userInfo;

    /**
     * 获取当前 {@link Application} 的 {@link Context}
     *
     * @return 当前 {@link Application} 的 {@link Context}
     */
    public static Context getContext() {
        return context;
    }

    public static void initUserInfo(LoginBean loginBean) {
        userInfo = UserInfo.newInstance(loginBean);
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息对象 {@link UserInfo}
     */
    public static UserInfo getUserInfo() {
        return userInfo;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
