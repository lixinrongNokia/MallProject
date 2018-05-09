package iliker.fragment.finding.city;

import java.util.List;


class Data {
    private List<ProvincesInfo> provinces;
    private List<CityItem> cityitems;

    List<ProvincesInfo> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<ProvincesInfo> provinces) {
        this.provinces = provinces;
    }

    public List<CityItem> getCityitems() {
        return cityitems;
    }

    public void setCityitems(List<CityItem> cityitems) {
        this.cityitems = cityitems;
    }
}
