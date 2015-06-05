package ng.latitude.support.conf;

/**
 * Created by Joe on 2015/5/26.
 */
public class Constants {

    public static final String URL_LOGIN = "http://114.215.80.157/Home/Game/login";             // account | password
    public static final String URL_LOGON = "http://114.215.80.157/Home/Game/register";          // account | password
    public static final String URL_CHANGE_NAME = "http://114.215.80.157/Home/Game/name";             // name | token

    public static final String PARAM_ACCOUNT = "account";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_NAME = "name";

    public static final String ERROR_ACCOUNT_NOT_EXIST = "Account Not Exist";

    public static final long ANIM_COMMON_DURATION = 150l;
    public static final long ANIM_BUTTON_ALPHA_DURATION = 750l;

    public static final String OBJECT_ANIM_ALPHA = "alpha";
    public static final String OBJECT_ANIM_TRANSLATION_Y = "translationY";
    public static final String OBJECT_ANIM_TRANSLATION_X = "translationX";

    public static final int LOCATION_ACCURATE = 10;
    public static final int LOCATION_UPDATE_INTERVAL = 3000;

    public static final String LOCATION_PROVIDER_LBS = "lbs";
    public static final String LOCATION_PROVIDER_GPS = "gps";

}
