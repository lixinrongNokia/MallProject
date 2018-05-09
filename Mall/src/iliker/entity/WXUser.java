package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class WXUser implements Parcelable {
    private String nickName;
    private int sex;
    private String headImg;
    private String country;
    private String province;
    private String unionid;
    private String openid;
    private boolean onbind;

    public WXUser() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public boolean isOnbind() {
        return onbind;
    }

    public void setOnbind(boolean onbind) {
        this.onbind = onbind;
    }

    protected WXUser(Parcel in) {
        nickName = in.readString();
        sex = in.readInt();
        headImg = in.readString();
        country = in.readString();
        province = in.readString();
        unionid = in.readString();
        openid = in.readString();
        onbind = in.readByte() != 0;
    }

    public static final Creator<WXUser> CREATOR = new Creator<WXUser>() {
        @Override
        public WXUser createFromParcel(Parcel in) {
            return new WXUser(in);
        }

        @Override
        public WXUser[] newArray(int size) {
            return new WXUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickName);
        dest.writeInt(sex);
        dest.writeString(headImg);
        dest.writeString(country);
        dest.writeString(province);
        dest.writeString(unionid);
        dest.writeString(openid);
        dest.writeByte((byte) (onbind ? 1 : 0));
    }
}
