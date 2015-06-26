package ng.latitude.support.bean;

import com.google.gson.annotations.SerializedName;

import ng.latitude.support.network.HttpUtils;

/**
 * Created by Ng on 15/5/31
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class LoginBean {

    private int id;
    private String token;
    private String name;
    private String account;
    private String password;
    @SerializedName(HttpUtils.Params.FORCE)
    private int force;
    private int state;
    @SerializedName(HttpUtils.Params.SCORE_PLAYER)
    private int playerScore;
    @SerializedName(HttpUtils.Params.SCORE_FORCE)
    private int forceScore;

    public int getPlayerScore() {
        return playerScore;
    }

    /**
     * @param playerScore
     * @hide
     */
    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getForceScore() {
        return forceScore;
    }

    public void setForceScore(int forceScore) {
        this.forceScore = forceScore;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
