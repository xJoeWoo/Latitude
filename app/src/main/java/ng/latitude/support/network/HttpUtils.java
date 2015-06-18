package ng.latitude.support.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import ng.latitude.support.conf.Latitude;

/**
 * Created by Joe on 2015/5/24.
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
    }

    public interface Errors {
        String NO_RETURN = "No Return";
        String ACTION_FAILED = "Action Fail";
    }

}