package ng.latitude.support.conf;

import ng.latitude.support.bean.LoginBean;

/**
 * Created by Ng on 15/6/15.
 */
public class UserInfo {

    private int id;
    private String account;
    private String token;
    private String name;
    private int force;
    private int forceScore;
    private int playerScore;

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

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public int getForce() {
        return force;
    }
}
