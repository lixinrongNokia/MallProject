package iliker.fragment.finding.city;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import iliker.fragment.finding.city.ScrollerNumberPicker.OnSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static iliker.utils.XmlUtil.readAssets;

/**
 * 城市Picker
 *
 * @author zihao
 */
public class CityPicker extends LinearLayout {
    /**
     * 滑动控件
     */
    private ScrollerNumberPicker provincePicker;
    private ScrollerNumberPicker cityPicker;
    /**
     * 选择监听
     */
    private OnSelectingListener onSelectingListener;
    /**
     * 刷新界面
     */
    private static final int REFRESH_VIEW = 0x001;
    /**
     * 临时日期
     */
    private int tempProvinceIndex = -1;
    private int temCityIndex = -1;
    private final Context context;
    private List<ProvincesInfo> province_list = new ArrayList<>();
    private final HashMap<String, List<Cityinfo>> city_map = new HashMap<>();

    private CitycodeUtil citycodeUtil;
    private String city_code_string;
    private testCity city;

    public CityPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getaddressinfo();
    }

    public CityPicker(Context context) {
        super(context);
        this.context = context;
        getaddressinfo();
    }

    public void setCity(testCity testCityWbb) {
        this.city = testCityWbb;
    }

    // 获取城市信息
    private void getaddressinfo() {
        // 读取城市信息string
        String area_str = readAssets(context, "area.json");
        Data data = JSON.parseObject(area_str, Data.class);
        province_list = data.getProvinces();
        List<CityItem> cityitems = data.getCityitems();
        for (CityItem item : cityitems) {
            city_map.put(item.getProvinceNO(), item.getCities());
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.city_picker, this);
        citycodeUtil = CitycodeUtil.getSingleton();
        // 获取控件引用
        provincePicker = (ScrollerNumberPicker) findViewById(R.id.province);

        cityPicker = (ScrollerNumberPicker) findViewById(R.id.city);
        provincePicker.setData(citycodeUtil.getProvince(province_list));
        provincePicker.setDefault(1);
        cityPicker.setData(citycodeUtil.getCity(city_map, citycodeUtil
                .getProvince_list_code().get(1)));
        cityPicker.setDefault(1);
        provincePicker.setOnSelectListener(new OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {
                if (TextUtils.isEmpty(text))
                    return;
                if (tempProvinceIndex != id) {
                    String selectDay = cityPicker.getSelectedText();
                    if (selectDay == null || selectDay.equals(""))
                        return;
                    // 城市数组
                    cityPicker.setData(citycodeUtil.getCity(city_map,
                            citycodeUtil.getProvince_list_code().get(id)));
                    cityPicker.setDefault(1);
                    //得到市级
                    String shi = cityPicker.getSelectedText();
                    //设置 值
                    city.cityAll(text, shi);

                    int lastDay = provincePicker.getListSize();
                    if (id > lastDay) {
                        provincePicker.setDefault(lastDay - 1);
                    }
                }
                tempProvinceIndex = id;
                Message message = new Message();
                message.what = REFRESH_VIEW;
                handler.sendMessage(message);
            }

            @Override
            public void selecting(int id, String text) {
            }
        });
        cityPicker.setOnSelectListener(new OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {
                if (TextUtils.isEmpty(text))
                    return;
                if (temCityIndex != id) {

                    String selectDay = provincePicker.getSelectedText();
                    if (selectDay == null || selectDay.equals(""))
                        return;
                    //设置 值
                    city.cityAll(selectDay, text);

                    int lastDay = cityPicker.getListSize();
                    if (id > lastDay) {
                        cityPicker.setDefault(lastDay - 1);
                    }
                }
                temCityIndex = id;
                Message message = new Message();
                message.what = REFRESH_VIEW;
                handler.sendMessage(message);
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_VIEW:
                    if (onSelectingListener != null)
                        onSelectingListener.selected(true);
                    break;
                default:
                    break;
            }
        }

    };

    public void setOnSelectingListener(OnSelectingListener onSelectingListener) {
        this.onSelectingListener = onSelectingListener;
    }

    public String getCity_code_string() {
        return city_code_string;
    }

    public String getCity_string() {
        return provincePicker.getSelectedText() + " " + cityPicker.getSelectedText();
    }

    public interface OnSelectingListener {

        void selected(boolean selected);
    }

    public interface testCity {
        void cityAll(String sheng, String shi);
    }
}
