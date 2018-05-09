package iliker.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MyOrderInfo implements java.io.Serializable {
    private int id;
    private String orderid;//订单号
    private String phone;//订购人电话
    private String user_nickName;//订购人昵称
    private String orderdate;//下单时间
    private String postmethod;//发货方式
    private String paymethod;//付款方式
    private String orderstate;//发货状态

    private String point;//自提地点
    private List<OrderItem> orderItems = new ArrayList<>();//商品项目

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser_nickName() {
        return user_nickName;
    }

    public void setUser_nickName(String user_nickName) {
        this.user_nickName = user_nickName;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
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

    public String getOrderstate() {
        return orderstate;
    }

    public void setOrderstate(String orderstate) {
        this.orderstate = orderstate;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
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

    public double getService_fee() {
        return new BigDecimal(getTotalPrice() * 0.2 * 0.1).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
