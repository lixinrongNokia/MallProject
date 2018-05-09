package iliker.fragment.finding.city;

import java.util.List;

/**
 * 方便封装map对象
 * Created by WDHTC on 2016/7/1.
 */
class CityItem {
    private String provinceNO;
    private List<Cityinfo> cities;

    public String getProvinceNO() {
        return provinceNO;
    }

    public void setProvinceNO(String provinceNO) {
        this.provinceNO = provinceNO;
    }

    public List<Cityinfo> getCities() {
        return cities;
    }

    public void setCities(List<Cityinfo> cities) {
        this.cities = cities;
    }
}
