package ng.latitude.support.conf;

/**
 * Created by Joe on 2015/5/26.
 */
public class Constants {

    public static final boolean DEBUG = false;

    public static final int NETWORK_TIMEOUT = 8 * 1000;
    public static final int NETWORK_MAX_RETRIES = 2;

    public static final int ANIM_LOGIN_LOGO_DELAY_TIME = 350;
    public static final int ANIM_LOGIN_LOGO_ANIM_DURATION = 800;
    public static final int ANIM_LOGIN_POP_DELAY_TIME = 1300;
    public static final int ANIM_LOGIN_POP_ANIM_DURATION = 500;
    public static final long ANIM_COMMON_DURATION = 150l;
    public static final long ANIM_SLOW_DURATION = 350l;
    public static final long ANIM_SHAKE_DURATION = 500l;
    public static final float ANIM_SHAKE_RANGE = 25;
    public static final int ANIM_SHAKE_TIMES = 2;
    public static final long ANIM_BUTTON_ALPHA_DURATION = 750l;

    public static final String OBJECT_ANIM_ALPHA = "alpha";
    public static final String OBJECT_ANIM_TRANSLATION_Y = "translationY";
    public static final String OBJECT_ANIM_TRANSLATION_X = "translationX";

    public static final int LOCATION_UPDATE_ACCURATE = 2;
    public static final int LOCATION_UPDATE_INTERVAL = 8 * 1000;

    public static final float MAP_INIT_ZOOM_LEVEL = 18f;
    public static final float MAP_INIT_TILT = 45f;

    public static final String LOCATION_PROVIDER_LBS = "lbs";
    public static final String LOCATION_PROVIDER_GPS = "gps";

    public static final int SENSOR_HEADING_UPDATE_LIMIT = 3;
    public static final float GAMING_CAPTURE_RANGE = 40f;
    public static final double GAMING_SCAN_LATITUDE_RADIUS = 0.02;
    public static final double GAMING_SCAN_LONGITUDE_RADIUS = 0.0228;


    public static final int REFRESH_SCORE_INTERVAL = 30 * 1000;
    public static final int REFRESH_SPOTS_INTERVAL = REFRESH_SCORE_INTERVAL * 2;


    public interface Force {
        int ONE = 0;
        int TWO = 1;
    }

}
