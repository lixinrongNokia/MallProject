package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UnPackOrder implements Parcelable {
    private String unpackOrderId;
    private String clearingType;
    private String createTime;
    private Boolean user_confirm;
    private Boolean sys_flag;
    private List<OrderItem> orderItems = new ArrayList<>();//商品项目

    public UnPackOrder() {
    }

    private UnPackOrder(Parcel in) {
        unpackOrderId = in.readString();
        clearingType = in.readString();
        createTime = in.readString();
        user_confirm = in.readByte() != 0;
        orderItems = in.createTypedArrayList(OrderItem.CREATOR);
    }

    public static final Creator<UnPackOrder> CREATOR = new Creator<UnPackOrder>() {
        @Override
        public UnPackOrder createFromParcel(Parcel in) {
            return new UnPackOrder(in);
        }

        @Override
        public UnPackOrder[] newArray(int size) {
            return new UnPackOrder[size];
        }
    };

    public String getUnpackOrderId() {
        return unpackOrderId;
    }

    public void setUnpackOrderId(String unpackOrderId) {
        this.unpackOrderId = unpackOrderId;
    }

    public String getClearingType() {
        return clearingType;
    }

    public void setClearingType(String clearingType) {
        this.clearingType = clearingType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Boolean getUser_confirm() {
        return user_confirm;
    }

    public void setUser_confirm(Boolean user_confirm) {
        this.user_confirm = user_confirm;
    }

    public Boolean getSys_flag() {
        return sys_flag;
    }

    public void setSys_flag(Boolean sys_flag) {
        this.sys_flag = sys_flag;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (OrderItem item : orderItems) {
            totalPrice += item.getProductPrice() * item.getOrderamount();
        }
        return totalPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unpackOrderId);
        dest.writeString(clearingType);
        dest.writeString(createTime);
        dest.writeByte((byte) (user_confirm ? 1 : 0));
        dest.writeTypedList(orderItems);
    }
}
