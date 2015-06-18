package ng.latitude.support.conf;

/**
 * Created by Joe on 2015/5/26.
 */
public class Constants {

    public static final String URL_LOGIN = "http://114.215.80.157/Home/Game/login";             // account | password
    public static final String URL_LOGON = "http://114.215.80.157/Home/Game/register";          // account | password
    public static final String URL_CHANGE_NAME = "http://114.215.80.157/Home/Game/name";             // name | token
    public static final String URL_SET_SPOT = "http://114.215.80.157/Home/Game/setpoint";             // uid | title | snippet | latitude | longitude | force
    public static final String URL_GET_SPOTS = "http://114.215.80.157/Home/Game/getpoint";             // 4 location
    public static final String URL_CAPTURE_SPOT = "http://114.215.80.157/Home/Game/grabpoint";             // 4 location

    public static final int NETWORK_TIMEOUT = 8 * 1000;
    public static final int NETWORK_MAX_RETRIES = 2;

    public static final long ANIM_COMMON_DURATION = 150l;
    public static final long ANIM_SHAKE_DURATION = 500l;
    public static final float ANIM_SHAKE_RANGE = 25;
    public static final int ANIM_SHAKE_TIMES = 2;
    public static final long ANIM_BUTTON_ALPHA_DURATION = 750l;

    public static final String OBJECT_ANIM_ALPHA = "alpha";
    public static final String OBJECT_ANIM_TRANSLATION_Y = "translationY";
    public static final String OBJECT_ANIM_TRANSLATION_X = "translationX";

    public static final int LOCATION_ACCURATE = 10;
    public static final int LOCATION_UPDATE_INTERVAL = 3000;

    public static final float MAP_INIT_ZOOM_LEVEL = 14f;
    public static final float MAP_INIT_TILT = 45f;

    public static final String LOCATION_PROVIDER_LBS = "lbs";
    public static final String LOCATION_PROVIDER_GPS = "gps";

    public static final int SENSOR_HEADING_UPDATE_LIMIT = 3;
    public static final float GAMING_CAPTURE_RANGE = 40f;

    public interface Force {
        int ONE = 0;
        int TWO = 1;
    }

}
