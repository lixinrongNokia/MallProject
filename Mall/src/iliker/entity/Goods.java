package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Goods implements Parcelable {
    private int id;
    private String goodCode;
    private String goodName;
    private String goodsDesc;
    private double price;
    private String imgpath;
    private String illustrations;
    private int sales;// 库存
    private double divided_into;//分成比例
    private String colors;
    private String sizes;
    private int stock;
    private List<StockInfo> stockInfoSet = new ArrayList<>();
    private boolean visible;

    public Goods() {
    }

    public Goods(int id, String goodCode, String goodName, String goodsDesc, double price, String imgpath, String illustrations, String colors, String sizes) {
        this.id = id;
        this.goodCode = goodCode;
        this.goodName = goodName;
        this.price = price;
        this.goodsDesc = goodsDesc;
        this.imgpath = imgpath;
        this.illustrations = illustrations;
        this.colors = colors;
        this.sizes = sizes;
    }

    public Goods(Parcel parcel) {
        this.id = parcel.readInt();
        this.goodCode = parcel.readString();
        this.goodName = parcel.readString();
        this.goodsDesc = parcel.readString();
        this.price = parcel.readDouble();
        this.imgpath = parcel.readString();
        this.illustrations = parcel.readString();
        this.sales = parcel.readInt();
        this.divided_into = parcel.readDouble();
        this.colors = parcel.readString();
        this.sizes = parcel.readString();
        this.stock = parcel.readInt();
        this.stockInfoSet = parcel.createTypedArrayList(StockInfo.CREATOR);
        this.visible = parcel.readByte() != 0;
    }

    public List<StockInfo> getStockInfoSet() {
        return stockInfoSet;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setStockInfoSet(List<StockInfo> stockInfoSet) {
        this.stockInfoSet = stockInfoSet;
    }

    public double getDivided_into() {
        return divided_into;
    }

    public void setDivided_into(double divided_into) {
        this.divided_into = divided_into;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoodCode() {
        return goodCode;
    }

    public void setGoodCode(String goodCode) {
        this.goodCode = goodCode;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getIllustrations() {
        return illustrations;
    }

    public void setIllustrations(String illustrations) {
        this.illustrations = illustrations;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(goodCode);
        dest.writeString(goodName);
        dest.writeString(goodsDesc);
        dest.writeDouble(price);
        dest.writeString(imgpath);
        dest.writeString(illustrations);
        dest.writeInt(sales);
        dest.writeDouble(divided_into);
        dest.writeString(colors);
        dest.writeString(sizes);
        dest.writeInt(stock);
        dest.writeTypedList(stockInfoSet);
        dest.writeByte((byte) (visible ? 1 : 0));
    }

    public static final Parcelable.Creator<Goods> CREATOR = new Parcelable.Creator<Goods>() {

        @Override
        public Goods createFromParcel(Parcel arg0) {
            return new Goods(arg0);
        }

        @Override
        public Goods[] newArray(int arg0) {
            return new Goods[arg0];
        }

    };

}
