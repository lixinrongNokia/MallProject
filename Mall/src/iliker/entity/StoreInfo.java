package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


public class StoreInfo implements Parcelable, Comparable<StoreInfo> {
    private int id;
    private String loginEmail;
    private String loginPwd;
    private String storeName;
    private String faceIcon;
    private double latitude;
    private double longitude;
    private String tell;
    private String contacts;
    private String address;
    private int visible;
    private String regTime;
    private double distance;

    public StoreInfo() {
    }
    public StoreInfo(int id, String loginEmail, String loginPwd, String storeName, String faceIcon, String tell, String contacts, String address, int visible, String regTime) {
        this.id = id;
        this.loginEmail = loginEmail;
        this.loginPwd = loginPwd;
        this.storeName = storeName;
        this.faceIcon = faceIcon;
        this.tell = tell;
        this.contacts = contacts;
        this.address = address;
        this.visible = visible;
        this.regTime = regTime;
    }

    protected StoreInfo(Parcel in) {
        id = in.readInt();
        loginEmail = in.readString();
        loginPwd = in.readString();
        storeName = in.readString();
        faceIcon = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        tell = in.readString();
        contacts = in.readString();
        address = in.readString();
        visible = in.readInt();
        regTime = in.readString();
        distance = in.readDouble();
    }

    public static final Creator<StoreInfo> CREATOR = new Creator<StoreInfo>() {
        @Override
        public StoreInfo createFromParcel(Parcel in) {
            return new StoreInfo(in);
        }

        @Override
        public StoreInfo[] newArray(int size) {
            return new StoreInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getFaceIcon() {
        return faceIcon;
    }

    public void setFaceIcon(String faceIcon) {
        this.faceIcon = faceIcon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull StoreInfo arg0) {
        Integer them = (int) (this.getDistance() * 100);
        Integer tham = (int) (arg0.getDistance() * 100);
        return them.compareTo(tham);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(loginEmail);
        dest.writeString(loginPwd);
        dest.writeString(storeName);
        dest.writeString(faceIcon);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(tell);
        dest.writeString(contacts);
        dest.writeString(address);
        dest.writeInt(visible);
        dest.writeString(regTime);
        dest.writeDouble(distance);
    }
}
