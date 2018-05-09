package iliker.entity;

import android.os.Parcel;

public class StoreStockItem implements android.os.Parcelable {
    private int stockItemId;
    private String size;//尺寸
    private int stockCount;//库存数量

    public StoreStockItem() {
    }

    private StoreStockItem(Parcel in) {
        size = in.readString();
        stockItemId = in.readInt();
        stockCount = in.readInt();
    }

    public static final Creator<StoreStockItem> CREATOR = new Creator<StoreStockItem>() {
        @Override
        public StoreStockItem createFromParcel(Parcel in) {
            return new StoreStockItem(in);
        }

        @Override
        public StoreStockItem[] newArray(int size) {
            return new StoreStockItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(size);
        dest.writeInt(stockItemId);
        dest.writeInt(stockCount);
    }
}
