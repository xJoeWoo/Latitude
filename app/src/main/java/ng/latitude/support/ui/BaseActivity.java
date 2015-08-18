package ng.latitude.support.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ng on 15/7/3
 * <p/>
 * All Rights Reserved by Ng<br>
 * Copyright © 2015
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract void findViews();

    protected abstract void setListeners();

    protected void getData() {

    }

    protected void showContent() {

    }

    private void init() {
        findViews();
        setListeners();
        getData();
        showContent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    protected void releaseResources() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseResources();
    }
}
