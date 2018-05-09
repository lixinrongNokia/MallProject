package iliker.entity;

/**
 * 套装搭配
 * 
 * @author Administrator
 * 
 */
public class Fashion extends BaseRec {

	private int id;
	private String name;
	private int category;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getOpenHref() {
		return openHref;
	}

	public void setOpenHref(String openHref) {
		this.openHref = openHref;
	}

}
