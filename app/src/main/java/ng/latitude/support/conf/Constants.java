package ng.latitude.support.conf;

/**
 * Created by Joe on 2015/5/26.
 */
public class Constants {

    public static final String URL_LOGIN = "http://114.215.80.157/Home/Game/login";             // account | password
    public static final String URL_LOGON = "http://114.215.80.157/Home/Game/register";          // account | password
    public static final String URL_CHANGE_NAME = "http://114.215.80.157/Home/Game/name";             // name | token
    public static final String URL_SET_SPOT = "http://114.215.80.157/Home/Game/setpoint";             // uid | title | snippet | latitude | longitude | force

    public static final int FORCE_1 = 0;
    public static final int FORCE_2 = 1;

    public static final String PARAM_ACCOUNT = "account";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_FORCE = "group";
    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_LATITUDE = "latitude";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_ID = "id";
    public static final String PARAM_USER_ID = "uid";
    public static final String PARAM_SPOT_TITLE = "title";
    public static final String PARAM_SPOT_SNIPPET = "context";

    public static final String ERROR_ACCOUNT_NOT_EXIST = "Account Not Exist";

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

    public static final float INIT_ZOOM_LEVEL = 16f;

    public static final String LOCATION_PROVIDER_LBS = "lbs";
    public static final String LOCATION_PROVIDER_GPS = "gps";

    public static final int SENSOR_HEADING_UPDATE_LIMIT = 3;

}
