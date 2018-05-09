package iliker.fragment.type;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.DialogFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.ShipAddress;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;

public class NewAddressActivity extends Activity implements OnClickListener {
    private EditText consignee, phone, street;
    private Button save_btn;
    private CustomApplication cap;
    private ShipAddress shipaddress = null;
    private TextView titlet;

    private Spinner province_spinner;
    private Spinner city_spinner;
    private Spinner county_spinner;
    private Integer provinceId, cityId;
    private String strProvince, strCity, strCounty;
    private Dialog dialog;

    private final int[] city = {R.array.beijin_province_item,
            R.array.tianjin_province_item, R.array.heibei_province_item,
            R.array.shanxi1_province_item, R.array.neimenggu_province_item,
            R.array.liaoning_province_item, R.array.jilin_province_item,
            R.array.heilongjiang_province_item, R.array.shanghai_province_item,
            R.array.jiangsu_province_item, R.array.zhejiang_province_item,
            R.array.anhui_province_item, R.array.fujian_province_item,
            R.array.jiangxi_province_item, R.array.shandong_province_item,
            R.array.henan_province_item, R.array.hubei_province_item,
            R.array.hunan_province_item, R.array.guangdong_province_item,
            R.array.guangxi_province_item, R.array.hainan_province_item,
            R.array.chongqing_province_item, R.array.sichuan_province_item,
            R.array.guizhou_province_item, R.array.yunnan_province_item,
            R.array.xizang_province_item, R.array.shanxi2_province_item,
            R.array.gansu_province_item, R.array.qinghai_province_item,
            R.array.linxia_province_item, R.array.xinjiang_province_item,
            R.array.taiwan_province_item, R.array.hongkong_province_item};
    private final int[] countyOfBeiJing = {R.array.beijin_city_item};
    private final int[] countyOfTianJing = {R.array.tianjin_city_item};
    private final int[] countyOfHeBei = {R.array.shijiazhuang_city_item,
            R.array.tangshan_city_item, R.array.qinghuangdao_city_item,
            R.array.handan_city_item, R.array.xingtai_city_item,
            R.array.baoding_city_item, R.array.zhangjiakou_city_item,
            R.array.chengde_city_item, R.array.cangzhou_city_item,
            R.array.langfang_city_item, R.array.hengshui_city_item};
    private final int[] countyOfShanXi1 = {R.array.taiyuan_city_item,
            R.array.datong_city_item, R.array.yangquan_city_item,
            R.array.changzhi_city_item, R.array.jincheng_city_item,
            R.array.shuozhou_city_item, R.array.jinzhong_city_item,
            R.array.yuncheng_city_item, R.array.xinzhou_city_item,
            R.array.linfen_city_item, R.array.lvliang_city_item};
    private final int[] countyOfNeiMengGu = {R.array.huhehaote_city_item,
            R.array.baotou_city_item, R.array.wuhai_city_item,
            R.array.chifeng_city_item, R.array.tongliao_city_item,
            R.array.eerduosi_city_item, R.array.hulunbeier_city_item,
            R.array.bayannaoer_city_item, R.array.wulanchabu_city_item,
            R.array.xinganmeng_city_item, R.array.xilinguolemeng_city_item,
            R.array.alashanmeng_city_item};
    private final int[] countyOfLiaoNing = {R.array.shenyang_city_item,
            R.array.dalian_city_item, R.array.anshan_city_item,
            R.array.wushun_city_item, R.array.benxi_city_item,
            R.array.dandong_city_item, R.array.liaoning_jinzhou_city_item,
            R.array.yingkou_city_item, R.array.fuxin_city_item,
            R.array.liaoyang_city_item, R.array.panjin_city_item,
            R.array.tieling_city_item, R.array.zhaoyang_city_item,
            R.array.huludao_city_item};
    private final int[] countyOfJiLin = {R.array.changchun_city_item,
            R.array.jilin_city_item, R.array.siping_city_item,
            R.array.liaoyuan_city_item, R.array.tonghua_city_item,
            R.array.baishan_city_item, R.array.songyuan_city_item,
            R.array.baicheng_city_item, R.array.yanbian_city_item};
    private final int[] countyOfHeiLongJiang = {R.array.haerbing_city_item,
            R.array.qiqihaer_city_item, R.array.jixi_city_item,
            R.array.hegang_city_item, R.array.shuangyashan_city_item,
            R.array.daqing_city_item, R.array.heilongjiang_yichun_city_item,
            R.array.jiamusi_city_item, R.array.qitaihe_city_item,
            R.array.mudanjiang_city_item, R.array.heihe_city_item,
            R.array.suihua_city_item, R.array.daxinganling_city_item};
    private final int[] countyOfShangHai = {R.array.shanghai_city_item};

    private final int[] countyOfJiangSu = {R.array.nanjing_city_item,
            R.array.wuxi_city_item, R.array.xuzhou_city_item,
            R.array.changzhou_city_item, R.array.nanjing_suzhou_city_item,
            R.array.nantong_city_item, R.array.lianyungang_city_item,
            R.array.huaian_city_item, R.array.yancheng_city_item,
            R.array.yangzhou_city_item, R.array.zhenjiang_city_item,
            R.array.jiangsu_taizhou_city_item, R.array.suqian_city_item};
    private final int[] countyOfZheJiang = {R.array.hangzhou_city_item,
            R.array.ningbo_city_item, R.array.wenzhou_city_item,
            R.array.jiaxing_city_item, R.array.huzhou_city_item,
            R.array.shaoxing_city_item, R.array.jinhua_city_item,
            R.array.quzhou_city_item, R.array.zhoushan_city_item,
            R.array.zejiang_huzhou_city_item, R.array.lishui_city_item};
    private final int[] countyOfAnHui = {R.array.hefei_city_item,
            R.array.wuhu_city_item, R.array.bengbu_city_item,
            R.array.huainan_city_item, R.array.maanshan_city_item,
            R.array.huaibei_city_item, R.array.tongling_city_item,
            R.array.anqing_city_item, R.array.huangshan_city_item,
            R.array.chuzhou_city_item, R.array.fuyang_city_item,
            R.array.anhui_suzhou_city_item, R.array.chaohu_city_item,
            R.array.luan_city_item, R.array.haozhou_city_item,
            R.array.chizhou_city_item, R.array.xuancheng_city_item};
    private final int[] countyOfFuJian = {R.array.huzhou_city_item,
            R.array.xiamen_city_item, R.array.putian_city_item,
            R.array.sanming_city_item, R.array.quanzhou_city_item,
            R.array.zhangzhou_city_item, R.array.nanp_city_item,
            R.array.longyan_city_item, R.array.ningde_city_item};
    private final int[] countyOfJiangXi = {R.array.nanchang_city_item,
            R.array.jingdezhen_city_item, R.array.pingxiang_city_item,
            R.array.jiujiang_city_item, R.array.xinyu_city_item,
            R.array.yingtan_city_item, R.array.ganzhou_city_item,
            R.array.jian_city_item, R.array.jiangxi_yichun_city_item,
            R.array.jiangxi_wuzhou_city_item, R.array.shangrao_city_item};
    private final int[] countyOfShanDong = {R.array.jinan_city_item,
            R.array.qingdao_city_item, R.array.zaobo_city_item,
            R.array.zaozhuang_city_item, R.array.dongying_city_item,
            R.array.yantai_city_item, R.array.weifang_city_item,
            R.array.jining_city_item, R.array.taian_city_item,
            R.array.weihai_city_item, R.array.rizhao_city_item,
            R.array.laiwu_city_item, R.array.linxi_city_item,
            R.array.dezhou_city_item, R.array.liaocheng_city_item,
            R.array.shandong_bingzhou_city_item, R.array.heze_city_item};
    private final int[] countyOfHeNan = {R.array.zhenshou_city_item,
            R.array.kaifang_city_item, R.array.luoyang_city_item,
            R.array.kaipingshan_city_item, R.array.anyang_city_item,
            R.array.hebi_city_item, R.array.xinxiang_city_item,
            R.array.jiaozuo_city_item, R.array.buyang_city_item,
            R.array.xuchang_city_item, R.array.leihe_city_item,
            R.array.sanmenxia_city_item, R.array.nanyang_city_item,
            R.array.shangqiu_city_item, R.array.xinyang_city_item,
            R.array.zhoukou_city_item, R.array.zhumadian_city_item};
    private final int[] countyOfHuBei = {R.array.wuhan_city_item,
            R.array.huangshi_city_item, R.array.shiyan_city_item,
            R.array.yichang_city_item, R.array.xiangpan_city_item,
            R.array.erzhou_city_item, R.array.jinmen_city_item,
            R.array.xiaogan_city_item, R.array.hubei_jinzhou_city_item,
            R.array.huanggang_city_item, R.array.xianning_city_item,
            R.array.suizhou_city_item, R.array.enshi_city_item,
            R.array.shenglongjia_city_item};

    private final int[] countyOfHuNan = {R.array.changsha_city_item,
            R.array.zhuzhou_city_item, R.array.xiangtan_city_item,
            R.array.hengyang_city_item, R.array.shaoyang_city_item,
            R.array.yueyang_city_item, R.array.changde_city_item,
            R.array.zhangjiajie_city_item, R.array.yiyang_city_item,
            R.array.hunan_bingzhou_city_item, R.array.yongzhou_city_item,
            R.array.huaihua_city_item, R.array.loudi_city_item,
            R.array.xiangxi_city_item};
    private final int[] countyOfGuangDong = {R.array.guangzhou_city_item,
            R.array.shaoguan_city_item, R.array.shenzhen_city_item,
            R.array.zhuhai_city_item, R.array.shantou_city_item,
            R.array.foshan_city_item, R.array.jiangmen_city_item,
            R.array.zhangjiang_city_item, R.array.maoming_city_item,
            R.array.zhaoqing_city_item, R.array.huizhou_city_item,
            R.array.meizhou_city_item, R.array.shanwei_city_item,
            R.array.heyuan_city_item, R.array.yangjiang_city_item,
            R.array.qingyuan_city_item, R.array.dongguan_city_item,
            R.array.zhongshan_city_item, R.array.chaozhou_city_item,
            R.array.jiyang_city_item, R.array.yunfu_city_item};
    private final int[] countyOfGuangXi = {R.array.nanning_city_item,
            R.array.liuzhou_city_item, R.array.guilin_city_item,
            R.array.guangxi_wuzhou_city_item, R.array.beihai_city_item,
            R.array.fangchenggang_city_item, R.array.qinzhou_city_item,
            R.array.guigang_city_item, R.array.yuelin_city_item,
            R.array.baise_city_item, R.array.hezhou_city_item,
            R.array.hechi_city_item, R.array.laibing_city_item,
            R.array.chuangzuo_city_item};
    private final int[] countyOfHaiNan = {R.array.haikou_city_item,
            R.array.sanya_city_item};
    private final int[] countyOfChongQing = {R.array.chongqing_city_item};
    private final int[] countyOfSiChuan = {R.array.chengdu_city_item,
            R.array.zigong_city_item, R.array.panzhihua_city_item,
            R.array.luzhou_city_item, R.array.deyang_city_item,
            R.array.mianyang_city_item, R.array.guangyuan_city_item,
            R.array.suining_city_item, R.array.neijiang_city_item,
            R.array.leshan_city_item, R.array.nanchong_city_item,
            R.array.meishan_city_item, R.array.yibing_city_item,
            R.array.guangan_city_item, R.array.dazhou_city_item,
            R.array.yaan_city_item, R.array.bazhong_city_item,
            R.array.ziyang_city_item, R.array.abei_city_item,
            R.array.ganmu_city_item, R.array.liangshan_city_item};
    private final int[] countyOfGuiZhou = {R.array.guiyang_city_item,
            R.array.lupanshui_city_item, R.array.zhunyi_city_item,
            R.array.anshun_city_item, R.array.tongren_city_item,
            R.array.qingxinan_city_item, R.array.biji_city_item,
            R.array.qingdongnan_city_item, R.array.qingnan_city_item};
    private final int[] countyOfYunNan = {R.array.kunming_city_item,
            R.array.qujing_city_item, R.array.yuexi_city_item,
            R.array.baoshan_city_item, R.array.zhaotong_city_item,
            R.array.lijiang_city_item, R.array.simao_city_item,
            R.array.lingcang_city_item, R.array.chuxiong_city_item,
            R.array.honghe_city_item, R.array.wenshan_city_item,
            R.array.xishuangbanna_city_item, R.array.dali_city_item,
            R.array.dehuang_city_item, R.array.nujiang_city_item,
            R.array.diqing_city_item};
    private final int[] countyOfXiZang = {R.array.lasa_city_item,
            R.array.changdu_city_item, R.array.shannan_city_item,
            R.array.rgeze_city_item, R.array.naqu_city_item,
            R.array.ali_city_item, R.array.linzhi_city_item};

    private final int[] countyOfShanXi2 = {R.array.xian_city_item,
            R.array.tongchuan_city_item, R.array.baoji_city_item,
            R.array.xianyang_city_item, R.array.weinan_city_item,
            R.array.yanan_city_item, R.array.hanzhong_city_item,
            R.array.yulin_city_item, R.array.ankang_city_item,
            R.array.shangluo_city_item};
    private final int[] countyOfGanSu = {R.array.lanzhou_city_item,
            R.array.jiayuguan_city_item, R.array.jinchang_city_item,
            R.array.baiyin_city_item, R.array.tianshui_city_item,
            R.array.wuwei_city_item, R.array.zhangyue_city_item,
            R.array.pingliang_city_item, R.array.jiuquan_city_item,
            R.array.qingyang_city_item, R.array.dingxi_city_item,
            R.array.longnan_city_item, R.array.linxia_city_item,
            R.array.gannan_city_item};
    private final int[] countyOfQingHai = {R.array.xining_city_item,
            R.array.haidong_city_item, R.array.haibai_city_item,
            R.array.huangnan_city_item, R.array.hainan_city_item,
            R.array.guluo_city_item, R.array.yushu_city_item,
            R.array.haixi_city_item};
    private final int[] countyOfNingXia = {R.array.yinchuan_city_item,
            R.array.shizuishan_city_item, R.array.wuzhong_city_item,
            R.array.guyuan_city_item, R.array.zhongwei_city_item};
    private final int[] countyOfXinJiang = {R.array.wulumuqi_city_item,
            R.array.kelamayi_city_item, R.array.tulyfan_city_item,
            R.array.hami_city_item, R.array.changji_city_item,
            R.array.boertala_city_item, R.array.bayinguolen_city_item,
            R.array.akesu_city_item, R.array.kemuleisu_city_item,
            R.array.geshen_city_item, R.array.hetian_city_item,
            R.array.yili_city_item, R.array.tacheng_city_item,
            R.array.aleitai_city_item, R.array.shihezi_city_item,
            R.array.alaer_city_item, R.array.tumushihe_city_item,
            R.array.wujiaqu_city_item};
    private final int[] countyOfHongKong = {R.array.hongkong_city_item,
            R.array.aomen_city_item};
    private final int[] countyOfTaiWan = {R.array.taibei_city_item,
            R.array.xinbei_city_item, R.array.taizhong_city_item,
            R.array.tainan_city_item, R.array.gaoxiong_city_item};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newaddress);
        shipaddress = (ShipAddress) getIntent().getSerializableExtra(
                "shipaddress");
        cap = (CustomApplication) getApplication();
        dialog= DialogFactory.initDialog(this);
        findViews();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSpinner();
        if (shipaddress != null) {
            titlet.setText("编辑收货地址");
            consignee.setText(shipaddress.getConsigneeName());
            phone.setText(shipaddress.getPhone());
        } else {
            titlet.setText("新建收货地址");
        }
    }

    private void findViews() {
        consignee = (EditText) findViewById(R.id.consignee);
        phone = (EditText) findViewById(R.id.phone);
        street = (EditText) findViewById(R.id.street);
        save_btn = (Button) findViewById(R.id.save_btn);
        titlet = (TextView) findViewById(R.id.titlet);
        findViewById(R.id.backbtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setListener() {
        save_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        dialog.show();
        String consigneer = consignee.getText().toString().trim();
        String phonestr = phone.getText().toString().trim();
        String streetstr = street.getText().toString().trim();
        if (TextUtils.isEmpty(consigneer) || TextUtils.isEmpty(phonestr)
                || TextUtils.isEmpty(streetstr)) {
            getMyToast("请填写完整信息").show();
            return;
        }
        String status = titlet.getText().toString();
        RequestParams params = new RequestParams(GeneralUtil.EDITADDRESS);
        params.addBodyParameter("shippingaddress.consigneeName", consigneer);
        params.addBodyParameter("shippingaddress.phone", phonestr);
        params.addBodyParameter("shippingaddress.address", strProvince + strCity + strCounty + streetstr);
        if ("编辑收货地址".equals(status)) {
            params.addBodyParameter("shippingaddress.id", shipaddress.getId() + "");
        }
        params.addBodyParameter("shippingaddress.userinfo.uid", cap.getUserinfo().getuID() + "");
        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        JSONObject jsonObject=JSON.parseObject(result);
                        if(jsonObject.getBoolean("success")){
                            getMyToast("操作成功").show();
                            NewAddressActivity.this.finish();
                        }else {
                            getMyToast("姿势不对").show();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {
                        dialog.dismiss();
                    }

                });
    }

    private void loadSpinner() {
        province_spinner = (Spinner) findViewById(R.id.province_spinner);
        province_spinner.setPrompt("请选择省份");
        ArrayAdapter<CharSequence> province_adapter = ArrayAdapter.createFromResource(this,
                R.array.province_item, android.R.layout.simple_spinner_item);
        province_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province_spinner.setAdapter(province_adapter);
        // select(province_spinner, province_adapter, R.array.province_item);
        province_spinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        provinceId = province_spinner.getSelectedItemPosition();
                        strProvince = province_spinner.getSelectedItem()
                                .toString();
                        city_spinner = (Spinner) findViewById(R.id.city_spinner);
                        county_spinner = (Spinner) findViewById(R.id.county_spinner);
                        city_spinner = (Spinner) findViewById(R.id.city_spinner);
                        city_spinner.setPrompt("请选择城市");
                        select(city_spinner, city[provinceId]);
                        city_spinner
                                .setOnItemSelectedListener(new OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(
                                            AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                                        cityId = city_spinner
                                                .getSelectedItemPosition();
                                        strCity = city_spinner
                                                .getSelectedItem()
                                                .toString();
                                        county_spinner = (Spinner) findViewById(R.id.county_spinner);
                                        county_spinner
                                                .setPrompt("请选择县区");
                                        switch (provinceId) {
                                            case 0:
                                                select(county_spinner,
                                                        countyOfBeiJing[cityId]);
                                                break;
                                            case 1:
                                                select(county_spinner,
                                                        countyOfTianJing[cityId]);
                                                break;
                                            case 2:
                                                select(county_spinner,
                                                        countyOfHeBei[cityId]);
                                                break;
                                            case 3:
                                                select(county_spinner,
                                                        countyOfShanXi1[cityId]);
                                                break;
                                            case 4:
                                                select(county_spinner,
                                                        countyOfNeiMengGu[cityId]);
                                                break;
                                            case 5:
                                                select(county_spinner,
                                                        countyOfLiaoNing[cityId]);
                                                break;
                                            case 6:
                                                select(county_spinner,
                                                        countyOfJiLin[cityId]);
                                                break;
                                            case 7:
                                                select(county_spinner,
                                                        countyOfHeiLongJiang[cityId]);
                                                break;
                                            case 8:
                                                select(county_spinner,
                                                        countyOfShangHai[cityId]);
                                                break;
                                            case 9:
                                                select(county_spinner,
                                                        countyOfJiangSu[cityId]);
                                                break;
                                            case 10:
                                                select(county_spinner,
                                                        countyOfZheJiang[cityId]);
                                                break;
                                            case 11:
                                                select(county_spinner,
                                                        countyOfAnHui[cityId]);
                                                break;
                                            case 12:
                                                select(county_spinner,
                                                        countyOfFuJian[cityId]);
                                                break;
                                            case 13:
                                                select(county_spinner,
                                                        countyOfJiangXi[cityId]);
                                                break;
                                            case 14:
                                                select(county_spinner,
                                                        countyOfShanDong[cityId]);
                                                break;
                                            case 15:
                                                select(county_spinner,
                                                        countyOfHeNan[cityId]);
                                                break;
                                            case 16:
                                                select(county_spinner,
                                                        countyOfHuBei[cityId]);
                                                break;
                                            case 17:
                                                select(county_spinner,
                                                        countyOfHuNan[cityId]);
                                                break;
                                            case 18:
                                                select(county_spinner,
                                                        countyOfGuangDong[cityId]);
                                                break;
                                            case 19:
                                                select(county_spinner,
                                                        countyOfGuangXi[cityId]);
                                                break;
                                            case 20:
                                                select(county_spinner,
                                                        countyOfHaiNan[cityId]);
                                                break;
                                            case 21:
                                                select(county_spinner,
                                                        countyOfChongQing[cityId]);
                                                break;
                                            case 22:
                                                select(county_spinner,
                                                        countyOfSiChuan[cityId]);
                                                break;
                                            case 23:
                                                select(county_spinner,
                                                        countyOfGuiZhou[cityId]);
                                                break;
                                            case 24:
                                                select(county_spinner,
                                                        countyOfYunNan[cityId]);
                                                break;
                                            case 25:
                                                select(county_spinner,
                                                        countyOfXiZang[cityId]);
                                                break;
                                            case 26:
                                                select(county_spinner,
                                                        countyOfShanXi2[cityId]);
                                                break;
                                            case 27:
                                                select(county_spinner,
                                                        countyOfGanSu[cityId]);
                                                break;
                                            case 28:
                                                select(county_spinner,
                                                        countyOfQingHai[cityId]);
                                                break;
                                            case 29:
                                                select(county_spinner,
                                                        countyOfNingXia[cityId]);
                                                break;
                                            case 30:
                                                select(county_spinner,
                                                        countyOfXinJiang[cityId]);
                                                break;
                                            case 31:
                                                select(county_spinner,
                                                        countyOfTaiWan[cityId]);
                                                break;
                                            case 32:
                                                select(county_spinner,
                                                        countyOfHongKong[cityId]);
                                                break;

                                            default:
                                                break;
                                        }

                                        county_spinner
                                                .setOnItemSelectedListener(new OnItemSelectedListener() {

                                                    @Override
                                                    public void onItemSelected(
                                                            AdapterView<?> arg0,
                                                            View arg1,
                                                            int arg2,
                                                            long arg3) {
                                                        strCounty = county_spinner
                                                                .getSelectedItem()
                                                                .toString();
                                                    }

                                                    @Override
                                                    public void onNothingSelected(
                                                            AdapterView<?> arg0) {
                                                    }

                                                });
                                    }

                                    @Override
                                    public void onNothingSelected(
                                            AdapterView<?> arg0) {
                                    }
                                });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
    }

    private void select(Spinner spin, int arry) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arry,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }

}
