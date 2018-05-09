package iliker.fragment.finding.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 城市代码
 *
 * @author zd
 */
class CitycodeUtil {

    private final ArrayList<String> province_list = new ArrayList<>();
    private final ArrayList<String> city_list = new ArrayList<>();
    private final ArrayList<String> province_list_code = new ArrayList<>();
    private final ArrayList<String> city_list_code = new ArrayList<>();
    /**
     * 单例
     */
    private static CitycodeUtil model;

    ArrayList<String> getProvince_list_code() {
        return province_list_code;
    }

    /**
     * 获取单例
     */
    static CitycodeUtil getSingleton() {
        if (null == model) {
            model = new CitycodeUtil();
        }
        return model;
    }

    ArrayList<String> getProvince(List<ProvincesInfo> provice) {
        if (province_list_code.size() > 0) {
            province_list_code.clear();
        }
        if (province_list.size() > 0) {
            province_list.clear();
        }
        for (ProvincesInfo provincesInfo : provice) {
            province_list.add(provincesInfo.getProvinceName());
            province_list_code.add(provincesInfo.getProvinceNO());
        }
        return province_list;
    }

    ArrayList<String> getCity(
            HashMap<String, List<Cityinfo>> cityHashMap, String provicecode) {
        if (city_list_code.size() > 0) {
            city_list_code.clear();
        }
        if (city_list.size() > 0) {
            city_list.clear();
        }
        List<Cityinfo> city = cityHashMap.get(provicecode);
        for (Cityinfo cityinfo : city) {
            city_list.add(cityinfo.getCityName());
            city_list_code.add(cityinfo.getCityNO());
        }
        return city_list;
    }

}
