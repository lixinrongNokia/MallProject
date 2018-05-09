package iliker.entity;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class StoreStockInfo implements Parcelable {
    /*goods.goodCode,ssi.color,ssi.storeStockId,goods.id goodsId*/
    private int storeStockId;
    private Goods goods;
    private String color;//颜色文字
    private String addTime;
    private List<StoreStockItem> stockItems;

    public StoreStockInfo() {
    }

    private StoreStockInfo(Parcel in) {
        goods = in.readParcelable(Goods.class.getClassLoader());
        color = in.readString();
        addTime = in.readString();
        stockItems = in.createTypedArrayList(StoreStockItem.CREATOR);
    }

    public static final Creator<StoreStockInfo> CREATOR = new Creator<StoreStockInfo>() {
        @Override
        public StoreStockInfo createFromParcel(Parcel in) {
            return new StoreStockInfo(in);
        }

        @Override
        public StoreStockInfo[] newArray(int size) {
            return new StoreStockInfo[size];
        }
    };

    //goods.goodCode,ssi.color,ssi.storeStockId,goods.id
    public int getStoreStockId() {
        return storeStockId;
    }

    public void setStoreStockId(int storeStockId) {
        this.storeStockId = storeStockId;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public List<StoreStockItem> getStockItems() {
        return stockItems;
    }

    public void setStockItems(List<StoreStockItem> stockItems) {
        this.stockItems = stockItems;
    }

    public int getStockCount() {
        int stockCount = 0;
        for (StoreStockItem storeStockItem : stockItems) {
            stockCount += storeStockItem.getStockCount();
        }
        return stockCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(goods, flags);
        dest.writeString(color);
        dest.writeString(addTime);
        dest.writeTypedList(stockItems);
    }
}
