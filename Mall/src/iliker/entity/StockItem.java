package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class StockItem implements Parcelable {
    private int stockItemId;
    private String size;//尺寸
    private int stockCount = 0;//库存数量

    public StockItem() {
    }

    public StockItem(Parcel in) {
        stockItemId = in.readInt();
        size = in.readString();
        stockCount = in.readInt();
    }

    public int getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(int stockItemId) {
        this.stockItemId = stockItemId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public static final Creator<StockItem> CREATOR = new Creator<StockItem>() {
        @Override
        public StockItem createFromParcel(Parcel in) {
            return new StockItem(in);
        }

        @Override
        public StockItem[] newArray(int size) {
            return new StockItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stockItemId);
        dest.writeString(size);
        dest.writeInt(stockCount);
    }
}
