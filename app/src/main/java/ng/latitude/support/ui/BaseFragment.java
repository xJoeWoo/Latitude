package ng.latitude.support.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ng on 15/7/3
 * <p/>
 * All Rights Reserved by Ng<br>
 * Copyright © 2015
 */
public abstract class BaseFragment extends Fragment {

    private static int LAYOUT_ID;
    private View rootView;

    /**
     * 设置布局，必须调用
     *
     * @param layoutId 布局ID
     */
    protected void setContentView(int layoutId) {
        LAYOUT_ID = layoutId;
    }

    protected abstract void findViews(View v);

    protected abstract void setListeners();

    protected void getData() {

    }

    protected void showContent() {

    }

    private void init(View v) {
        findViews(v);
        setListeners();
        getData();
        showContent();
    }

    protected void releaseResources() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(LAYOUT_ID, container, false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseResources();
    }

    public View getRootView() {
        return rootView;
    }

}
