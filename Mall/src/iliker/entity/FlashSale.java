package iliker.entity;

import java.io.Serializable;

public class FlashSale implements Serializable {
    /*{"pageCount":1,"totalSize":2,"dataSet":[{"illustrations":null,"number":50,"STATUS":true,"goodName":"fghrf","price":15.0,"imgpath":"32aabcba000000c0.png#32aabcba000001c0.png","discount":0.9,"id":178,"goodCode":"qreter33","sales":0,"goodsDesc":"343dgdd"},{"illustrations":null,"number":50,"STATUS":true,"goodName":"fghrf","price":15.0,"imgpath":"32aabcba000000c0.png#32aabcba000001c0.png","discount":0.9,"id":178,"goodCode":"qreter33","sales":0,"goodsDesc":"343dgdd"}]}*/
    private int fid;
    private Goods goods;
    private int number;
    private int STATUS;
    private double discount;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
