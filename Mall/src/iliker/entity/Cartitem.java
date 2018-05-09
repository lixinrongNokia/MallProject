package iliker.entity;

import java.io.Serializable;

//商品项目
public class Cartitem implements Serializable {
    private double productPrice;//销售单价(防止商品价格变动)
    private Order order;// 商品
    private int itemCount;// 商品项目数量

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public double getItemprice() {
        return productPrice * itemCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

}
