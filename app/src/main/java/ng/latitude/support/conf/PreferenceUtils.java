package ng.latitude.support.conf;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joe on 2015/5/24
 * <p>
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class PreferenceUtils {

    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATLNG = "latlng";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_RAN_BEFORE = "firstRun";

    public static final float FLOAT_NOT_EXIST = 0f;
    public static final String STRING_NOT_EXIST = "";
    public static final boolean BOOLEAN_NOT_EXIST = false;

    private static final String PREFERENCES_FILE_NAME = "wow.latitude";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    static {
        sharedPreferences = Latitude.getContext().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static float getFloat(String key) {
        return sharedPreferences.getFloat(key, FLOAT_NOT_EXIST);
    }

    public static boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, BOOLEAN_NOT_EXIST);
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, STRING_NOT_EXIST);
    }

    public static void savePreference(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void savePreference(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void savePreference(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }
}
