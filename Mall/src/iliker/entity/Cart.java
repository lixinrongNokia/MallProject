package iliker.entity;

import java.util.LinkedHashMap;
import java.util.Map;

//购物车
public class Cart {
    private final Map<Integer, Cartitem> map = new LinkedHashMap<>();// 商品项目集合

    public int getTotalcount() {
        int totalcount = 0;
        for (Map.Entry<Integer, Cartitem> entry : map.entrySet()) {
            Cartitem item = entry.getValue();
            totalcount += item.getItemCount();
        }
        return totalcount;
    }

    public double getTotalprice() {
        double totalprice = 0;
        for (Map.Entry<Integer, Cartitem> entry : map.entrySet()) {
            Cartitem item = entry.getValue();
            totalprice += item.getItemprice();
        }
        return totalprice;
    }

    public Map<Integer, Cartitem> getMap() {
        return map;
    }

}
