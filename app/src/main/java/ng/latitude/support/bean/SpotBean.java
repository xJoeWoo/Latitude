package ng.latitude.support.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import ng.latitude.support.network.HttpUtils;

/**
 * Created by Ng on 15/6/15.
 */
public class SpotBean implements Parcelable {

    public static final Parcelable.Creator<SpotBean> CREATOR
            = new Parcelable.Creator<SpotBean>() {
        public SpotBean createFromParcel(Parcel in) {
            return new SpotBean(in);
        }

        public SpotBean[] newArray(int size) {
            return new SpotBean[size];
        }
    };
    @SerializedName(HttpUtils.Params.USER_ID)
    private String userId;
    @SerializedName(HttpUtils.Params.SPOT_TITLE)
    private String title;
    @SerializedName(HttpUtils.Params.SPOT_SNIPPET)
    private String snippet;
    @SerializedName(HttpUtils.Params.LONGITUDE)
    private double longitude;
    @SerializedName(HttpUtils.Params.LATITUDE)
    private double latitude;
    @SerializedName(HttpUtils.Params.FORCE)
    private int force;
    private int id;
    private int state;

    private SpotBean(Parcel in) {
        userId = in.readString();
        title = in.readString();
        snippet = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        force = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(title);
        dest.writeString(snippet);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(force);
    }
}
