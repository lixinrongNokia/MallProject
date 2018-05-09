package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class StockInfo implements Parcelable {
    private int stockId;
    private String color;//颜色文字
    private String img;//该颜色图片
    private List<StockItem> stockItems = new ArrayList<>();

    public StockInfo() {
    }

    public StockInfo(Parcel in) {
        stockId = in.readInt();
        color = in.readString();
        img = in.readString();
        stockItems = in.createTypedArrayList(StockItem.CREATOR);
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<StockItem> getStockItems() {
        return stockItems;
    }

    public void setStockItems(List<StockItem> stockItems) {
        this.stockItems = stockItems;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stockId);
        dest.writeString(color);
        dest.writeString(img);
        dest.writeTypedList(stockItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StockInfo> CREATOR = new Creator<StockInfo>() {
        @Override
        public StockInfo createFromParcel(Parcel in) {
            return new StockInfo(in);
        }

        @Override
        public StockInfo[] newArray(int size) {
            return new StockInfo[size];
        }
    };
}
