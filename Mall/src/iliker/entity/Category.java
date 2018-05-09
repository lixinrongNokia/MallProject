package iliker.entity;

/**
 * 锦上添花
 * 
 * @author lixinrong
 * 
 */
public class Category extends BaseRec {

	private int id;
	private String icfname;
	private String icfcname;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIcfname() {
		return icfname;
	}

	public void setIcfname(String icfname) {
		this.icfname = icfname;
	}

	public String getIcfcname() {
		return icfcname;
	}

	public void setIcfcname(String icfcname) {
		this.icfcname = icfcname;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getOpenHref() {
		return openHref;
	}

	public void setOpenHref(String openHref) {
		this.openHref = openHref;
	}
}
