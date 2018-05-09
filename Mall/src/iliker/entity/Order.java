package iliker.entity;

import java.io.Serializable;

/**
 * 项目名称：Mall 类 名 称：Commodity 类 描 述： 数据库封装数据用实体 创 建 人：lxr 创建时间：2015-04-8
 * 下午2:20:15 Copyright (c) lxr-版权所有
 */
@SuppressWarnings("serial")
public class Order implements Serializable {
    private Integer cid;
    private Integer goodid;
    private String clothestypeid;
    private String goodCode;
    private String goodName;
    private String goodsDesc;
    private Double price;
    private String imgpath;
    private String color;
    private String size;
    private Integer count;
    private String illustrations;
    private double divided_into;

    public Order() {
    }

    public Order(Integer cid, Integer count) {
        this.cid = cid;
        this.count = count;
    }

    public Order(Integer cid, Integer goodid, String clothestypeid,
                 String goodCode, String goodName, String goodsDesc, Double price,
                 String imgpath, String color, String size, Integer count, Double divided_into) {
        this.cid = cid;
        this.goodid = goodid;
        this.clothestypeid = clothestypeid;
        this.goodCode = goodCode;
        this.goodName = goodName;
        this.goodsDesc = goodsDesc;
        this.price = price;
        this.imgpath = imgpath;
        this.color = color;
        this.size = size;
        this.count = count;
        this.divided_into=divided_into;
    }

    public double getDivided_into() {
        return divided_into;
    }

    public void setDivided_into(double divided_into) {
        this.divided_into = divided_into;
    }

    public String getIllustrations() {
        return illustrations;
    }

    public void setIllustrations(String illustrations) {
        this.illustrations = illustrations;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getGoodid() {
        return goodid;
    }

    public void setGoodid(Integer goodid) {
        this.goodid = goodid;
    }

    public String getClothestypeid() {
        return clothestypeid;
    }

    public void setClothestypeid(String clothestypeid) {
        this.clothestypeid = clothestypeid;
    }

    public String getGoodCode() {
        return goodCode;
    }

    public void setGoodCode(String goodCode) {
        this.goodCode = goodCode;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
