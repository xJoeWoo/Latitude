package ng.latitude.support.conf;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joe on 2015/5/24.
 */
public class PreferenceUtils {

    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATLNG = "latlng";
    public static final float VALUE_NOT_EXIST = 0f;
    private static final String PREFERENCES_FILE_NAME = "wow.latitude";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    static {
        sharedPreferences = Latitude.getContext().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public static float getPreference(String key){
        return sharedPreferences.getFloat(key, VALUE_NOT_EXIST);
    }

    public static void savePreference(String key, float value){
        editor.putFloat(key,value);
        editor.commit();
    }
}
