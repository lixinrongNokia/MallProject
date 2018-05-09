package iliker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class WebOrder implements Parcelable {
    private int id;
    private String orderid;//订单号
    private String orderdate;//订单时间
    private int orderamount;
    private String postmethod;
    private String paymethod;
    private String recevername;
    private String receveraddr;
    private String recevertel;
    private double toalprice;//订单总价
    private double goodsTotalPrice;//商品总价
    private double deliverFee;//运费
    private String orderstate;
    private String trade_no;
    private String imgpath;
    private StoreInfo storeInfo;
    private List<OrderItem> orderItem;
    private boolean sys_flag;

    public WebOrder() {
    }

    protected WebOrder(Parcel in) {
        id = in.readInt();
        orderid = in.readString();
        orderdate = in.readString();
        orderamount = in.readInt();
        postmethod = in.readString();
        paymethod = in.readString();
        recevername = in.readString();
        receveraddr = in.readString();
        recevertel = in.readString();
        toalprice = in.readDouble();
        goodsTotalPrice = in.readDouble();
        deliverFee = in.readDouble();
        orderstate = in.readString();
        trade_no = in.readString();
        imgpath = in.readString();
        storeInfo = in.readParcelable(StoreInfo.class.getClassLoader());
        orderItem = in.createTypedArrayList(OrderItem.CREATOR);
        sys_flag = in.readByte() != 0;
    }

    public static final Creator<WebOrder> CREATOR = new Creator<WebOrder>() {
        @Override
        public WebOrder createFromParcel(Parcel in) {
            return new WebOrder(in);
        }

        @Override
        public WebOrder[] newArray(int size) {
            return new WebOrder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(orderid);
        dest.writeString(orderdate);
        dest.writeInt(orderamount);
        dest.writeString(postmethod);
        dest.writeString(paymethod);
        dest.writeString(recevername);
        dest.writeString(receveraddr);
        dest.writeString(recevertel);
        dest.writeDouble(toalprice);
        dest.writeDouble(goodsTotalPrice);
        dest.writeDouble(deliverFee);
        dest.writeString(orderstate);
        dest.writeString(trade_no);
        dest.writeString(imgpath);
        dest.writeParcelable(storeInfo, flags);
        dest.writeTypedList(orderItem);
        dest.writeByte((byte) (sys_flag ? 1 : 0));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public int getOrderamount() {
        return orderamount;
    }

    public void setOrderamount(int orderamount) {
        this.orderamount = orderamount;
    }

    public String getPostmethod() {
        return postmethod;
    }

    public void setPostmethod(String postmethod) {
        this.postmethod = postmethod;
    }

    public String getPaymethod() {
        return paymethod;
    }

    public void setPaymethod(String paymethod) {
        this.paymethod = paymethod;
    }

    public String getRecevername() {
        return recevername;
    }

    public void setRecevername(String recevername) {
        this.recevername = recevername;
    }

    public String getReceveraddr() {
        return receveraddr;
    }

    public void setReceveraddr(String receveraddr) {
        this.receveraddr = receveraddr;
    }

    public String getRecevertel() {
        return recevertel;
    }

    public void setRecevertel(String recevertel) {
        this.recevertel = recevertel;
    }

    public double getToalprice() {
        return toalprice;
    }

    public void setToalprice(double toalprice) {
        this.toalprice = toalprice;
    }

    public double getGoodsTotalPrice() {
        return goodsTotalPrice;
    }

    public void setGoodsTotalPrice(double goodsTotalPrice) {
        this.goodsTotalPrice = goodsTotalPrice;
    }

    public double getDeliverFee() {
        return deliverFee;
    }

    public void setDeliverFee(double deliverFee) {
        this.deliverFee = deliverFee;
    }

    public String getOrderstate() {
        return orderstate;
    }

    public void setOrderstate(String orderstate) {
        this.orderstate = orderstate;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public StoreInfo getStoreInfo() {
        return storeInfo;
    }

    public boolean isSys_flag() {
        return sys_flag;
    }

    public void setSys_flag(boolean sys_flag) {
        this.sys_flag = sys_flag;
    }

    public void setStoreInfo(StoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }
}
