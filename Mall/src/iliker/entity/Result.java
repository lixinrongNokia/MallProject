package iliker.entity;

//身形自测结果
public class Result extends BaseRec {
	private String typename;
	private String bmi;
	private String cupType;
	private String uw;
	private String imgpath;
	private String desc;
	private String tags;

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public String getBmi() {
		return bmi;
	}

	public void setBmi(String bmi) {
		this.bmi = bmi;
	}

	public String getCupType() {
		return cupType;
	}

	public void setCupType(String cupType) {
		this.cupType = cupType;
	}

	public String getUw() {
		return uw;
	}

	public void setUw(String uw) {
		this.uw = uw;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
