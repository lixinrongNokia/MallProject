package iliker.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import cn.iliker.mall.R;
import com.fjl.widget.ToastFactory;
import iliker.entity.*;
import iliker.fragment.home.BodyTestActivity;
import iliker.fragment.home.HomeListAdapter;
import iliker.fragment.home.LinkActivity;
import iliker.utils.GeneralUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import static com.fjl.widget.DialogFactory.initDialog;
import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.HttpHelp.getHttpUtils;
import static iliker.utils.ViewUtils.removeParent;

/**
 * 搭配推荐
 */
public class BodyCollFragment extends BaseFragment implements OnChildClickListener {
    private static String typename;
    private ExpandableListView eplist;
    private Dialog progressDialog;
    private boolean isone = true;
    private List<List<BaseRec>> data;
    private static String bmi;
    private static String uw;
    private static String cuptype;
    private static String tags;
    private AlertDialog.Builder builder;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.homeview, null);
            findViews();
            setListener();
        }
        removeParent(view);
        return view;
    }

    private void findViews() {
        eplist = (ExpandableListView) view.findViewById(R.id.expandablelist);
    }

    private void initData() {
        if (!customApplication.networkIsAvailable()) {
            ToastFactory.getMyToast("没网络").show();
            return;
        }
        if (isone) {
            isone = false;
            if (progressDialog == null) {
                progressDialog = initDialog(context);
            }
            progressDialog.show();
        }
        RequestParams params = new RequestParams(GeneralUtil.GETREC);
        params.addBodyParameter("typename", typename);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                data = getRecommends(result);
                if (data != null && !data.isEmpty()) {
                    eplist.setAdapter(new HomeListAdapter(
                            context, data));
                    eplist.expandGroup(0);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
                loadNotework();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void loadNotework() {
        isone = true;
        if (builder == null) {
            builder = new Builder(context);
        }
        builder.setMessage("连接服务器失败");
        builder.setPositiveButton("刷新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                initData();
            }

        }).show();
    }

    /**
     * 获取所有推荐产品，上下身搭配，锦上添花JSON数据
     */
    private static List<List<BaseRec>> getRecommends(String params) {
        List<List<BaseRec>> list = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(params);
            JSONArray res = jsonobject.getJSONArray("产品推荐");
            List<BaseRec> recoommends = new ArrayList<>();
            for (int i = 0; i < res.length(); i++) {
                JSONObject object = res.getJSONObject(i);
                Recommend rec = new Recommend();
                rec.setId(object.getInt("id"));
                rec.setSeriesName(object.getString("seriesName"));
                rec.setImgPath(object.getString("imgpath"));
                rec.setMemberPrice(object.getDouble("memberPrice"));
                rec.setDiscountPrice(object.getDouble("discountPrice"));
                rec.setOpenHref(object.getString("openHref"));
                recoommends.add(rec);
            }
            JSONObject fashs = jsonobject.getJSONObject("套装搭配");
            List<BaseRec> fashdata = new ArrayList<>();
            String imgpath = fashs.getString("imgpath");
            JSONArray fashions = fashs.getJSONArray("list");
            for (int i = 0; i < fashions.length(); i++) {
                JSONObject object = fashions.getJSONObject(i);
                Fashion fa = new Fashion();
                fa.setCategory(object.getInt("category"));
                fa.setId(object.getInt("id"));
                fa.setImgPath(object.getString("imgpath"));
                fa.setName(object.getString("name"));
                fa.setOpenHref(object.getString("openHref"));
                fashdata.add(fa);
            }
            JSONArray icf = jsonobject.getJSONArray("锦上添花");
            List<BaseRec> icfs = new ArrayList<>();
            for (int i = 0; i < icf.length(); i++) {
                JSONObject object = icf.getJSONObject(i);
                Category ca = new Category();
                ca.setId(object.getInt("id"));
                ca.setIcfname(object.getString("icfname"));
                ca.setIcfcname(object.getString("icfcname"));
                ca.setImgPath(object.getString("imgpath"));
                ca.setOpenHref(object.getString("openHref"));
                icfs.add(ca);
            }
            Result result = new Result();
            result.setTypename(typename);
            result.setCupType(cuptype);
            result.setUw(uw);
            result.setImgpath(imgpath);
            result.setBmi(bmi);
            result.setTags(tags);
            double intbmi = Double.parseDouble(bmi);
            String desc = "";

            if (intbmi < 18.5) {
                desc = "过轻";
            } else if (intbmi >= 18.5 && intbmi < 24.99) {
                desc = "正常";
            } else if (intbmi >= 25) {
                desc = "过重";
            }
            result.setDesc(desc);
            List<BaseRec> results = new ArrayList<>();
            results.add(result);
            list.add(results);
            list.add(recoommends);
            list.add(fashdata);
            list.add(icfs);
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    private void setListener() {
        eplist.setOnChildClickListener(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        BaseRec rec = data.get(groupPosition).get(childPosition);
        if (rec != null) {
            if (rec instanceof Result) {
                Intent intent = new Intent(context, BodyTestActivity.class);
                startActivity(intent);
            }
            String openHref = rec.getOpenHref();
            if (!TextUtils.isEmpty(openHref)) {
                Intent intent = new Intent(context, LinkActivity.class);
                intent.putExtra("openHref", openHref);
                startActivity(intent);
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sf = context.getSharedPreferences("result", Context.MODE_PRIVATE);
        typename = sf.getString("typetext", "");
        bmi = sf.getString("bmi", "");
        uw = sf.getString("underpants", "");
        cuptype = sf.getString("cuptype", "");
        tags = sf.getString("tags", "");
        initData();
    }
}