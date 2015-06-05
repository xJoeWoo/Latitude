package ng.latitude.support.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import ng.latitude.support.conf.Latitude;

/**
 * Created by Joe on 2015/5/24.
 */
public class HttpUtils {
    private static RequestQueue requestQueue = Volley.newRequestQueue(Latitude.getContext());

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

}