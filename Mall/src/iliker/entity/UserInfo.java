package iliker.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 用户类
 *
 * @author Administrator
 */
public class UserInfo implements Serializable, Comparable<UserInfo> {
    private int uID;//用户id
    private String realName;//用户真实姓名
    private String nickName;//用户昵称
    private String email;//用户邮箱
    private String password;//密码
    private String phone;//手机号码
    private String sex;//性别
    private String headImg;//头像
    private String signature;//签名
    private String birthday;//出生日期
    private String gid;//人脸识别模型标识
    private String superiornum;//上级手机号
    private String registered;//注册时间
    private int level;
    private double distance;//地理位置
    private String photoAlbum;//多图
    private int praiseCount;
    private boolean onbind;

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public UserInfo() {
    }

    public UserInfo(int uID, String nickName, String password, String phone, String superiornum, String registered) {
        this.uID = uID;
        this.nickName = nickName;
        this.password = password;
        this.phone = phone;
        this.superiornum = superiornum;
        this.registered = registered;
    }

    public UserInfo(int uID, String nickName, String password, String phone, String superiornum, String registered, boolean onbind) {
        this.uID = uID;
        this.nickName = nickName;
        this.password = password;
        this.phone = phone;
        this.superiornum = superiornum;
        this.registered = registered;
        this.onbind = onbind;
    }

    public int getuID() {
        return uID;
    }

    public void setuID(int uID) {
        this.uID = uID;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getSuperiornum() {
        return superiornum;
    }

    public void setSuperiornum(String superiornum) {
        this.superiornum = superiornum;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPhotoAlbum() {
        return photoAlbum;
    }

    public void setPhotoAlbum(String photoAlbum) {
        this.photoAlbum = photoAlbum;
    }

    public boolean isOnbind() {
        return onbind;
    }

    public void setOnbind(boolean onbind) {
        this.onbind = onbind;
    }

    @Override
    public int compareTo(@NonNull UserInfo arg0) {
        Integer them = (int) (this.getDistance() * 100);
        Integer tham = (int) (arg0.getDistance() * 100);
        return them.compareTo(tham);
    }
}
