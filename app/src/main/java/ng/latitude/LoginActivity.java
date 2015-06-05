package ng.latitude;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import ng.latitude.support.bean.LoginBean;
import ng.latitude.support.bean.LogonBean;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.network.GsonRequest;
import ng.latitude.support.network.HttpUtils;
import ng.latitude.support.ui.GravityInterpolator;


public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnLogon;
    private EditText etAccount;
    private EditText etPassword;

    private ObjectAnimator oaBtn;
    private float oaBtnValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        findViews();

        setListeners();
    }

    private void findViews() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogon = (Button) findViewById(R.id.btn_logon);
        etAccount = (EditText) findViewById(R.id.et_login_account);
        etPassword = (EditText) findViewById(R.id.et_login_password);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput())
                    requestLogin();
            }
        });

        btnLogon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput())
                    requestLogon();
            }
        });
    }

    private void requestLogin() {

        setButtonStatus(btnLogin, false);

        HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, Constants.URL_LOGIN, getInput(), LoginBean.class, new Response.Listener<LoginBean>() {
            @Override
            public void onResponse(LoginBean response) {

                setButtonStatus(true);
                Latitude.setLoginBean(response);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setButtonStatus(true);
                error.printStackTrace();
                Log.e("message", error.getLocalizedMessage());
                if (error.getMessage().contains(Constants.ERROR_ACCOUNT_NOT_EXIST)) {
                    new AlertDialog.Builder(LoginActivity.this).setTitle(R.string.dialog_confirm_logon_title).setMessage(R.string.dialog_confirm_logon_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLogon();
                        }
                    }).setNegativeButton(android.R.string.cancel, null).show();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    private void requestLogon() {

        setButtonStatus(btnLogon, false);

        HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, Constants.URL_LOGON, getInput(), LogonBean.class, new Response.Listener<LogonBean>() {
            @Override
            public void onResponse(LogonBean response) {
                Toast.makeText(LoginActivity.this, R.string.toast_logon_succeed, Toast.LENGTH_SHORT).show();
                setButtonStatus(true);
                requestLogin();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setButtonStatus(true);
                error.printStackTrace();

                Toast.makeText(LoginActivity.this, R.string.toast_logon_failed, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private Map<String, String> getInput() {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.PARAM_ACCOUNT, etAccount.getText().toString());
        map.put(Constants.PARAM_PASSWORD, etPassword.getText().toString());
        return map;
    }

    private boolean checkInput() {
        if (!etAccount.getText().toString().equals("") && !etPassword.getText().toString().equals("")) {
            return true;
        } else {
            if (etAccount.getText().toString().equals("")) {
                Toast.makeText(this, R.string.toast_input_account, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_input_password, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    private void setButtonStatus(boolean status) {
        setButtonStatus(null, status);
    }

    private void setButtonStatus(Button animBtn, boolean status) {
        if (status) {
            oaBtn.cancel();
            oaBtn = ObjectAnimator.ofFloat(animBtn, Constants.OBJECT_ANIM_ALPHA, oaBtnValue, 1f).setDuration(Constants.ANIM_BUTTON_ALPHA_DURATION/5);
            oaBtn.setInterpolator(new GravityInterpolator(true));
            oaBtn.start();

            btnLogon.setClickable(true);
            btnLogin.setClickable(true);

        } else {
            oaBtn = ObjectAnimator.ofFloat(animBtn, Constants.OBJECT_ANIM_ALPHA, 1f, 0f).setDuration(Constants.ANIM_BUTTON_ALPHA_DURATION);
            oaBtn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    oaBtnValue=(float)animation.getAnimatedValue();
                }
            });
            oaBtn.setInterpolator(new LinearInterpolator());
            oaBtn.setRepeatMode(ObjectAnimator.REVERSE);
            oaBtn.setRepeatCount(ObjectAnimator.INFINITE);
            oaBtn.start();

            btnLogon.setClickable(false);
            btnLogin.setClickable(false);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_lo  gin, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
