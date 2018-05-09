package iliker.fragment.finding.city;

import java.io.Serializable;

//城市
class Cityinfo implements Serializable {

    private String cityName;
    private String cityNO;

    String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    String getCityNO() {
        return cityNO;
    }

    public void setCityNO(String cityNO) {
        this.cityNO = cityNO;
    }
}
