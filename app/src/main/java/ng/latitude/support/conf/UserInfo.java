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

    public static UserInfo newInstance(LoginBean bean) {
        UserInfo userInfo = new UserInfo();
        userInfo.id = bean.getId();
        userInfo.account = bean.getAccount();
        userInfo.token = bean.getToken();
        userInfo.name = bean.getName() == null ? "" : bean.getName();
        userInfo.force = bean.getForce();
        return userInfo;
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
