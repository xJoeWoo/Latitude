package ng.latitude.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import ng.latitude.R;
import ng.latitude.support.bean.LoginBean;
import ng.latitude.support.bean.LogonBean;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.network.GsonRequest;
import ng.latitude.support.network.HttpUtils;
import ng.latitude.support.ui.GravityInterpolator;
import ng.latitude.support.ui.SelectForceDialog;


public class LoginActivity extends AppCompatActivity {

    private static final boolean DEBUG = false;

    private Button btnLogin;
    private Button btnLogon;
    private EditText etAccount;
    private EditText etPassword;
    private EditText etName;
    private EditText etPasswordConfirm;

    private ObjectAnimator oaBtn;
    private float oaBtnValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        findViews();
        setListeners();


        if (DEBUG) {
            etAccount.setText("ng");
            etPassword.setText("233");
            btnLogin.performClick();
        }

    }

    private void findViews() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogon = (Button) findViewById(R.id.btn_logon);
        etAccount = (EditText) findViewById(R.id.et_login_account);
        etPassword = (EditText) findViewById(R.id.et_login_password);
        etPasswordConfirm = (EditText) findViewById(R.id.et_login_password_confirm);
        etName = (EditText) findViewById(R.id.et_login_name);
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

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && etPasswordConfirm.getVisibility() == View.GONE) {
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogon.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void requestLogin() {

        setButtonStatus(btnLogin, false);

        HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, Constants.URL_LOGIN, getInput(), LoginBean.class, new Response.Listener<LoginBean>() {
            @Override
            public void onResponse(LoginBean response) {
                setButtonStatus(true);
                Latitude.initUserInfo(response);
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

        if (etPasswordConfirm.getVisibility() == View.VISIBLE) { // 展开后

            new SelectForceDialog(this, new SelectForceDialog.OnForceSelectedListener() {
                @Override
                public void onForceSelected(int force) {

                    setButtonStatus(btnLogon, false);

                    Map<String, String> params = getInput();
                    params.put(Constants.PARAM_FORCE, String.valueOf(force));

                    HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, Constants.URL_LOGON, params, LogonBean.class, new Response.Listener<LogonBean>() {
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
            }).show();

        } else { // 未展开

            final RelativeLayout buttonsContainer = (RelativeLayout) findViewById(R.id.login_pop_buttons_container);
            final RelativeLayout container = (RelativeLayout) findViewById(R.id.login_pop_container);

            final RelativeLayout.LayoutParams containerLayoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
            final int paddingBottom = getResources().getDimensionPixelSize(R.dimen.login_pop_horizontal_padding);

            final int oriHeight = container.getMeasuredHeight();
            final int targetHeight = oriHeight + etPassword.getHeight() * 2 + paddingBottom * 2;

            final int oriBtnWidth = btnLogon.getWidth();
            final int targetBtnWidth = etAccount.getWidth();
            RelativeLayout.LayoutParams btnLayoutParams = (RelativeLayout.LayoutParams) btnLogon.getLayoutParams();
            btnLayoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
            btnLogon.setLayoutParams(btnLayoutParams);


            ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
            va.setInterpolator(new GravityInterpolator(true));
            va.setDuration(Constants.ANIM_COMMON_DURATION);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (targetHeight - oriHeight) * (float) animation.getAnimatedValue();
                    containerLayoutParams.height = (int) (oriHeight + value);
                    container.setLayoutParams(containerLayoutParams);

                    buttonsContainer.setY(containerLayoutParams.height - buttonsContainer.getMeasuredHeight() - paddingBottom);

                    btnLogon.setWidth((int) (oriBtnWidth + (targetBtnWidth - oriBtnWidth) * (float) animation.getAnimatedValue()));

                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    etName.setVisibility(View.VISIBLE);
                    etPasswordConfirm.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(etName, Constants.OBJECT_ANIM_ALPHA, 0f, 1f).setDuration(Constants.ANIM_COMMON_DURATION).start();
                    ObjectAnimator.ofFloat(etPasswordConfirm, Constants.OBJECT_ANIM_ALPHA, 0f, 1f).setDuration(Constants.ANIM_COMMON_DURATION).start();

                    btnLogin.setVisibility(View.GONE);
                    btnLogon.setTextColor(getResources().getColor(R.color.green_primary));

                    etPasswordConfirm.requestFocus();
                }
            });
            va.start();
        }


    }

    private Map<String, String> getInput() {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.PARAM_ACCOUNT, etAccount.getText().toString());
        map.put(Constants.PARAM_PASSWORD, etPassword.getText().toString());
        if (etName.getText().toString().trim().isEmpty())
            map.put(Constants.PARAM_NAME, etPassword.getText().toString());

        return map;
    }

    private boolean checkInput() {
        if (!etAccount.getText().toString().trim().isEmpty() && !etPassword.getText().toString().trim().isEmpty()
                && (etPasswordConfirm.getVisibility() == View.GONE || (etPasswordConfirm.getVisibility() == View.VISIBLE && etPasswordConfirm.getText().toString().trim().equals(etPassword.getText().toString().trim())))
                && (etName.getVisibility() == View.GONE || (etName.getVisibility() == View.VISIBLE && !etName.getText().toString().trim().isEmpty()))) {
            return true;
        } else {
            if (etAccount.getText().toString().trim().isEmpty())
                Latitude.shakeEditText(etAccount, false);

            if (etPassword.getText().toString().trim().isEmpty())
                Latitude.shakeEditText(etPassword, false);

            if (!etPasswordConfirm.getText().toString().trim().equals(etPassword.getText().toString().trim()))
                Latitude.shakeEditText(etPasswordConfirm, true);

            if (etName.getText().toString().trim().isEmpty())
                Latitude.shakeEditText(etName, false);


            return false;
        }
    }


    private void setButtonStatus(boolean status) {
        setButtonStatus(null, status);
    }

    private void setButtonStatus(Button animBtn, boolean status) {
        if (status) {
            oaBtn.cancel();
//            oaBtn = ObjectAnimator.ofFloat(animBtn, Constants.OBJECT_ANIM_ALPHA, oaBtnValue, 1f).setDuration(Constants.ANIM_BUTTON_ALPHA_DURATION / 5);
//            oaBtn.setInterpolator(new GravityInterpolator(true));
//            oaBtn.start();

            btnLogin.setAlpha(1f);

            btnLogon.setClickable(true);
            btnLogin.setClickable(true);

        } else {
            oaBtn = ObjectAnimator.ofFloat(animBtn, Constants.OBJECT_ANIM_ALPHA, 1f, 0f).setDuration(Constants.ANIM_BUTTON_ALPHA_DURATION);
            oaBtn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    oaBtnValue = (float) animation.getAnimatedValue();
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
}