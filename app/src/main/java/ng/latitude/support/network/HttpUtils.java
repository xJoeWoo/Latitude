package ng.latitude.support.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import ng.latitude.support.conf.Latitude;

/**
 * Created by Ng on 15/5/24
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class HttpUtils {

    private static RequestQueue requestQueue = Volley.newRequestQueue(Latitude.getContext());

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public interface Params {
        String ACCOUNT = "account";
        String PASSWORD = "password";
        String FORCE = "group";
        String TOKEN = "token";
        String NAME = "name";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String ID = "id";
        String USER_ID = "uid";
        String SPOT_TITLE = "title";
        String SPOT_SNIPPET = "context";
        String LEFT_TOP_LATITUDE = "maxlatitude";
        String LEFT_TOP_LONGITUDE = "minlongitude";
        String RIGHT_BOTTOM_LATITUDE = "minlatitude";
        String RIGHT_BOTTOM_LONGITUDE = "maxlongitude";
        String SCORE_PLAYER = "score";
        String SCORE_FORCE = "gscore";
    }

    public interface Errors {
        String NO_RETURN = "No Return";
        String ACTION_FAILED = "Action Fail";
    }

    public interface Urls {
        String LOGIN = "http://114.215.80.157/Home/Game/login";             // account | password
        String LOGON = "http://114.215.80.157/Home/Game/register";          // account | password
        String CHANGE_NAME = "http://114.215.80.157/Home/Game/name";             // name | token
        String SET_SPOT = "http://114.215.80.157/Home/Game/setpoint";             // uid | title | snippet | latitude | longitude | force
        String GET_SPOTS = "http://114.215.80.157/Home/Game/getpoint";             // 4 location
        String CAPTURE_SPOT = "http://114.215.80.157/Home/Game/grabpoint";             // spotid | uid | force
        String GET_SCORE = "http://114.215.80.157/Home/Game/getscore";             // uid | group
    }

}