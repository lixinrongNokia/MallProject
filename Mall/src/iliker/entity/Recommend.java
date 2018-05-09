package iliker.entity;

/**
 * 产品推荐
 * 
 * @author Administrator
 * 
 */
public class Recommend extends BaseRec {

	private int id;
	private String seriesName;// 系列名
	private double memberPrice;// 会员价
	private double discountPrice;// 折扣价

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public double getMemberPrice() {
		return memberPrice;
	}

	public void setMemberPrice(double memberPrice) {
		this.memberPrice = memberPrice;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getOpenHref() {
		return openHref;
	}

	public void setOpenHref(String openHref) {
		this.openHref = openHref;
	}

}
