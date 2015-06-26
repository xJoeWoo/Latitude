package ng.latitude.support.conf;

import ng.latitude.support.bean.LoginBean;

/**
 * Created by Ng on 15/6/15
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */

/**
 * 保存用户信息的类
 */
public class UserInfo {

    private int id;
    private String account;
    private String token;
    private String name;
    private int force;
    private int forceScore;
    private int playerScore;

    /**
     * 从登录返回的 {@link LoginBean} 初始化用户信息
     *
     * @param bean 登录成功返回的 {@link LoginBean}
     * @return 保存当前用户信息的 {@link UserInfo}
     */
    public static UserInfo newInstance(LoginBean bean) {
        UserInfo userInfo = new UserInfo();
        userInfo.id = bean.getId();
        userInfo.account = bean.getAccount();
        userInfo.token = bean.getToken();
        userInfo.name = bean.getName() == null ? "" : bean.getName();
        userInfo.force = bean.getForce();
        userInfo.playerScore = bean.getPlayerScore();
        userInfo.forceScore = bean.getForceScore();
        return userInfo;
    }

    /**
     * 获取当前阵营分数
     *
     * @return 当前阵营分数
     */
    public int getForceScore() {
        return forceScore;
    }

    /**
     * 设置当前阵营分数
     */
    public void setForceScore(int forceScore) {
        this.forceScore = forceScore;
    }

    /**
     * 获取当前玩家分数
     * @return 当前玩家分数
     */
    public int getPlayerScore() {
        return playerScore;
    }

    /**
     * 设置当前玩家分数
     */
    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    /**
     * 获取当前用户 {@code ID}
     * @return 当前用户 {@code ID}
     */
    public int getId() {
        return id;
    }

    /**
     * 获取当前用户帐号
     * @return 当前用户帐号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 获取当前用户 {@code Token}
     * @return 当前用户 {@code Token}
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取当前用户名称
     * @return 当前用户名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取当前用户阵营
     * @return 当前用户阵营
     */
    public int getForce() {
        return force;
    }
}
