package ng.latitude.support.bean;

import com.google.gson.annotations.SerializedName;

import ng.latitude.support.network.HttpUtils;

/**
 * Created by Ng on 15/6/20
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class GetScoreBean {

    @SerializedName(HttpUtils.Params.SCORE_FORCE)
    private int forceScore;
    @SerializedName(HttpUtils.Params.SCORE_PLAYER)
    private int playerScore;

    public int getForceScore() {
        return forceScore;
    }

    public void setForceScore(int forceScore) {
        this.forceScore = forceScore;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }
}
