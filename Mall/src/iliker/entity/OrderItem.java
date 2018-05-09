package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderItem implements Parcelable {
    private double productPrice;//销售价
    private int orderamount;
    private String color;//颜色
    private String size;//尺寸
    private String goodname;
    private String imgpath;
    private String goodCode;
    private int goodsid;
    private Goods goods;//商品

    public OrderItem() {
    }

    OrderItem(Parcel in) {
        productPrice = in.readDouble();
        orderamount = in.readInt();
        color = in.readString();
        size = in.readString();
        goodname = in.readString();
        goodCode = in.readString();
        imgpath = in.readString();
        goodsid = in.readInt();
        goods=in.readParcelable(Goods.class.getClassLoader());
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getOrderamount() {
        return orderamount;
    }

    public void setOrderamount(int orderamount) {
        this.orderamount = orderamount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGoodname() {
        return goodname;
    }

    public void setGoodname(String goodname) {
        this.goodname = goodname;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public String getGoodCode() {
        return goodCode;
    }

    public void setGoodCode(String goodCode) {
        this.goodCode = goodCode;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(productPrice);
        dest.writeInt(orderamount);
        dest.writeString(goodCode);
        dest.writeString(color);
        dest.writeString(size);
        dest.writeString(goodname);
        dest.writeString(imgpath);
        dest.writeInt(goodsid);
        dest.writeParcelable(goods,flags);
    }
}
