package com.cardsui.example;

import java.io.Serializable;

/**
 * 描述：广告信息</br>
 */
class ADInfo implements Serializable {

    private String id = "";
    private String url = "";
    private String content = "";
    private String type = "";
    private double price;
    private double voucher_value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVoucher_value() {
        return voucher_value;
    }

    public void setVoucher_value(double voucher_value) {
        this.voucher_value = voucher_value;
    }
}
