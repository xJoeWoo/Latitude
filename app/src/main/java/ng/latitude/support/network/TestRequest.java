package ng.latitude.support.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Ng on 15/5/31.
 */
public class TestRequest extends StringRequest {

    public TestRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> params) {
        super(method, url, listener, errorListener);
        this.params =params;
    }

    private Map<String, String> params;


    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return params != null ? params : super.getHeaders();
    }
}
