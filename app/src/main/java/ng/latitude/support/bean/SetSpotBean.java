package ng.latitude.support.bean;

/**
 * Created by Ng on 15/6/15
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class SetSpotBean {

    private int state;


    /**
     * 1 成功，2 失败，0 超过3个点
     *
     * @return 状态
     */
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
