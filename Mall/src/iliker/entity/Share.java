package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 分享类
 * 
 * @author Administrator
 * 
 */
public class Share implements Parcelable {

	private int shareID;
	private String content;
	private String releaseTime;
	private String location;
	private String nickName;
	private String pic;
	private int piccount;

	private String headImg;

	public Share() {
	}

	public Share(Parcel parcel) {
		this.shareID = parcel.readInt();
		this.content = parcel.readString();
		this.releaseTime = parcel.readString();
		this.location = parcel.readString();
		this.nickName = parcel.readString();
		this.pic = parcel.readString();
		this.piccount = parcel.readInt();
		this.headImg = parcel.readString();
	}

	public int getShareID() {
		return shareID;
	}

	public void setShareID(int shareID) {
		this.shareID = shareID;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getPiccount() {
		return piccount;
	}

	public void setPiccount(int piccount) {
		this.piccount = piccount;
	}

	public static final Parcelable.Creator<Share> CREATOR = new Parcelable.Creator<Share>() {

		@Override
		public Share createFromParcel(Parcel arg0) {
			return new Share(arg0);
		}

		@Override
		public Share[] newArray(int arg0) {
			return new Share[arg0];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(shareID);
		dest.writeString(content);
		dest.writeString(releaseTime);
		dest.writeString(location);
		dest.writeString(nickName);
		dest.writeString(pic);
		dest.writeInt(piccount);
		dest.writeString(headImg);
	}

}
