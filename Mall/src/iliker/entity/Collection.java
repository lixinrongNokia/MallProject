package iliker.entity;

/**
 * Collection entity. @author MyEclipse Persistence Tools
 */

public class Collection {

    // Fields

    private Integer id;
    private Goods goods;
    private String colltime;
    private String color;
    private String size;

    // Constructors

    /**
     * default constructor
     */
    public Collection() {
    }

    /**
     * minimal constructor
     */
    public Collection(String colltime, String color, String size) {
        this.colltime = colltime;
        this.color = color;
        this.size = size;
    }

    /**
     * full constructor
     */
    public Collection(Goods goods, String colltime,
                      String color, String size) {
        this.goods = goods;
        this.colltime = colltime;
        this.color = color;
        this.size = size;
    }

    // Property accessors

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Goods getGoods() {
        return this.goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getColltime() {
        return this.colltime;
    }

    public void setColltime(String colltime) {
        this.colltime = colltime;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

}