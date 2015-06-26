package ng.latitude.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import ng.latitude.support.conf.PreferenceUtils;
import ng.latitude.support.network.GsonRequest;
import ng.latitude.support.network.HttpUtils;
import ng.latitude.support.ui.GravityInterpolator;
import ng.latitude.support.ui.InterfaceUtils;
import ng.latitude.support.ui.SelectForceDialog;

/**
 * Created by Ng on 15/5/24
 * <p>
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnLogon;
    private EditText etAccount;
    private EditText etPassword;
    private EditText etName;
    private EditText etPasswordConfirm;
    private CoordinatorLayout snackBarLayout;
    private RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        findViews();
        setListeners();

        if (Constants.DEBUG) {
            etAccount.setText("ng");
            etPassword.setText("233");
            btnLogin.performClick();
        }

        etAccount.setText(PreferenceUtils.getString(PreferenceUtils.KEY_ACCOUNT));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-LightItalic.ttf");

        final TextView tvLogo = (TextView) findViewById(R.id.login_logo);
        tvLogo.setTypeface(typeface);
        ((TextView) findViewById(R.id.login_pop_logo)).setTypeface(typeface);

        final Handler handler = new Handler();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator oa = ObjectAnimator.ofFloat(tvLogo, InterfaceUtils.AnimPropertyName.ALPHA, 0f, 1f).setDuration(Constants.ANIM_LOGIN_LOGO_ANIM_DURATION);
                oa.setInterpolator(GravityInterpolator.getInstance(true));
                oa.start();
            }
        }, Constants.ANIM_LOGIN_LOGO_DELAY_TIME);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator oa = ObjectAnimator.ofFloat(container, InterfaceUtils.AnimPropertyName.ALPHA, 0f, 1f).setDuration(Constants.ANIM_LOGIN_POP_ANIM_DURATION);
                oa.setInterpolator(GravityInterpolator.getInstance(true));
                oa.start();
            }
        }, Constants.ANIM_LOGIN_POP_DELAY_TIME);

    }

    private void findViews() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogon = (Button) findViewById(R.id.btn_logon);
        etAccount = (EditText) findViewById(R.id.et_login_account);
        etPassword = (EditText) findViewById(R.id.et_login_password);
        etPasswordConfirm = (EditText) findViewById(R.id.et_login_password_confirm);
        etName = (EditText) findViewById(R.id.et_login_name);
        snackBarLayout = (CoordinatorLayout) findViewById(R.id.snb_login);
        container = (RelativeLayout) findViewById(R.id.login_pop_container);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    requestLogin();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etAccount.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
                }
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

    /**
     * 开始登录网络请求
     */
    private void requestLogin() {

        setButtonStatus(btnLogin, false);

        HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, HttpUtils.Urls.LOGIN, getInput(), LoginBean.class, new Response.Listener<LoginBean>() {
            @Override
            public void onResponse(LoginBean response) {
//                setButtonStatus(true);
                Latitude.initUserInfo(response);
                PreferenceUtils.savePreference(PreferenceUtils.KEY_ACCOUNT, etAccount.getText().toString().trim());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setButtonStatus(true);
                error.printStackTrace();
                Log.e("message", error.getLocalizedMessage());
                if (error.getMessage().contains(HttpUtils.Errors.ACTION_FAILED)) {
                    new AlertDialog.Builder(LoginActivity.this).setTitle(R.string.dialog_confirm_logon_title).setMessage(R.string.dialog_confirm_logon_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLogon();
                        }
                    }).setNegativeButton(android.R.string.cancel, null).show();
                } else {
//                    Toast.makeText(LoginActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                    Snackbar.make(snackBarLayout, R.string.toast_network_error, Snackbar.LENGTH_LONG).show();
                }
            }
        }));
    }

    /**
     * 开始注册网络请求
     */
    private void requestLogon() {

        if (etPasswordConfirm.getVisibility() == View.VISIBLE) { // 展开后

            SelectForceDialog dialog = SelectForceDialog.newInstance();

            dialog.setOnForceSelectedListener(new SelectForceDialog.OnForceSelectedListener() {
                @Override
                public void onForceSelected(int force) {
                    setButtonStatus(btnLogon, false);

                    Map<String, String> params = getInput();
                    params.put(HttpUtils.Params.FORCE, String.valueOf(force));

                    HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, HttpUtils.Urls.LOGON, params, LogonBean.class, new Response.Listener<LogonBean>() {
                        @Override
                        public void onResponse(LogonBean response) {
                            Snackbar.make(snackBarLayout, R.string.toast_logon_succeed, Snackbar.LENGTH_LONG).show();
//                            setButtonStatus(true);
                            requestLogin();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setButtonStatus(true);
                            error.printStackTrace();
                            Snackbar.make(snackBarLayout, R.string.toast_logon_failed, Snackbar.LENGTH_LONG).show();
                        }
                    }));
                }
            });
            dialog.show(getFragmentManager(), "SelectForceDialog");

        } else { // 未展开

            final RelativeLayout buttonsContainer = (RelativeLayout) findViewById(R.id.login_pop_buttons_container);
            final RelativeLayout.LayoutParams containerLayoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
            final int paddingBottom = getResources().getDimensionPixelSize(R.dimen.login_pop_horizontal_padding);

            final int oriHeight = container.getMeasuredHeight();
            final int targetHeight = oriHeight + findViewById(R.id.login_pop_til_account).getHeight() * 2 + paddingBottom * 2;

            final int oriBtnWidth = btnLogon.getWidth();
            final int targetBtnWidth = findViewById(R.id.login_pop_til_account).getWidth();
            final int buttonsContainerHeight = buttonsContainer.getHeight();
            final RelativeLayout.LayoutParams btnLayoutParams = (RelativeLayout.LayoutParams) btnLogon.getLayoutParams();
            btnLayoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
            btnLogon.setLayoutParams(btnLayoutParams);

            ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
            va.setInterpolator(GravityInterpolator.getInstance(true));
            va.setDuration(Constants.ANIM_COMMON_DURATION);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (targetHeight - oriHeight) * (float) animation.getAnimatedValue();
                    containerLayoutParams.height = (int) (oriHeight + value);
                    container.setLayoutParams(containerLayoutParams);

                    buttonsContainer.setY(containerLayoutParams.height - buttonsContainerHeight - paddingBottom);

                    btnLogon.setWidth((int) (oriBtnWidth + (targetBtnWidth - oriBtnWidth) * (float) animation.getAnimatedValue()));
                    btnLogin.setAlpha(1 - (float) animation.getAnimatedValue());
                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    etName.setVisibility(View.VISIBLE);
                    etPasswordConfirm.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(etName, InterfaceUtils.AnimPropertyName.ALPHA, 0f, 1f).setDuration(Constants.ANIM_COMMON_DURATION).start();
                    ObjectAnimator.ofFloat(etPasswordConfirm, InterfaceUtils.AnimPropertyName.ALPHA, 0f, 1f).setDuration(Constants.ANIM_COMMON_DURATION).start();

                    btnLogin.setVisibility(View.GONE);
                    btnLogon.setTextColor(getResources().getColor(R.color.green_primary));

                    etPasswordConfirm.requestFocus();
                }
            });
            va.start();
        }
    }

    /**
     * 获取输入结果，请先使用 {@link #checkInput()} 检查输入合法性
     *
     * @return 保存有帐号、密码（如果注册的话还保存有名字）的 {@link HashMap}
     */
    private Map<String, String> getInput() {
        HashMap<String, String> map = new HashMap<>();
        map.put(HttpUtils.Params.ACCOUNT, etAccount.getText().toString().trim());
        map.put(HttpUtils.Params.PASSWORD, etPassword.getText().toString());
        if (!etName.getText().toString().trim().isEmpty())
            map.put(HttpUtils.Params.NAME, etName.getText().toString().trim());

        return map;
    }

    /**
     * 检查输入并摇动存在不合法输入的 {@link EditText}
     * @return {@code true} 为全部输入正确， {@code false} 为有输入错误
     */
    private boolean checkInput() {
        if (!etAccount.getText().toString().trim().isEmpty() && !etPassword.getText().toString().trim().isEmpty()
                && (etPasswordConfirm.getVisibility() == View.GONE || (etPasswordConfirm.getVisibility() == View.VISIBLE && etPasswordConfirm.getText().toString().trim().equals(etPassword.getText().toString().trim())))
                && (etName.getVisibility() == View.GONE || (etName.getVisibility() == View.VISIBLE && !etName.getText().toString().trim().isEmpty()))) {
            return true;
        } else {
            if (etAccount.getText().toString().trim().isEmpty())
                InterfaceUtils.shakeEditText(etAccount, false);

            if (etPassword.getText().toString().trim().isEmpty())
                InterfaceUtils.shakeEditText(etPassword, false);

            if (!etPasswordConfirm.getText().toString().trim().equals(etPassword.getText().toString().trim()))
                InterfaceUtils.shakeEditText(etPasswordConfirm, true);

            if (etName.getText().toString().trim().isEmpty())
                InterfaceUtils.shakeEditText(etName, false);

            return false;
        }
    }

    private void setButtonStatus(boolean status) {
        setButtonStatus(null, status);
    }

    private void setButtonStatus(Button animBtn, boolean status) {
        if (status) {
            InterfaceUtils.blinkView(animBtn, false);

            btnLogon.setClickable(true);
            btnLogin.setClickable(true);

        } else {

            InterfaceUtils.blinkView(animBtn, true);

            btnLogon.setClickable(false);
            btnLogin.setClickable(false);
        }
    }
}
