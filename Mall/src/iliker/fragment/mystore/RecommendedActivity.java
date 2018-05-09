package iliker.fragment.mystore;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.mobileim.YWIMKit;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import iliker.entity.Partners;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static com.iliker.application.CustomApplication.getmIMKit;
import static iliker.utils.HttpHelp.getHttpUtils;
import static iliker.utils.ParsJsonUtil.parsPartnersJSON;

public class RecommendedActivity extends BaseStoreActivity implements TabHost.OnTabChangeListener, AdapterView.OnItemClickListener {
    private TabHost tabhost;// 选项卡主控件
    private ListView listView;
    private View view;
    private TextView superior;
    private TextView account;
    private Dialog progressDialog;
    private List<Partners> list;
    private PartnersAdapter partnersAdapter;

    @Override
    protected void initMyViews() {
        title.setText("团队风采");
        view = View.inflate(this, R.layout.recommended_layout, null);
        storeContent.addView(view);
        progressDialog = DialogFactory.initDialog(this);
        findChildViews();
        initdata();
        if (!TextUtils.isEmpty(phone)) {
            String sql = "select phone,headimg,registered,level from userinfo where superiornum in" +
                    "(select phone from userinfo where superiornum='" + phone + "')";
            asyncGetCloudData(sql);
        }
    }

    private void findChildViews() {
        // 获取TabHost对象
        tabhost = (TabHost) view.findViewById(R.id.tabhost);
        listView = (ListView) view.findViewById(R.id.view1);
        account = (TextView) view.findViewById(R.id.accountnum);
        superior = (TextView) view.findViewById(R.id.superior);
        superior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YWIMKit ywimKit = getmIMKit();
                if (!TextUtils.isEmpty(superiornum) && ywimKit != null) {
                    Intent intent = ywimKit.getChattingActivityIntent(superiornum, getString(R.string.openIMKey));
                    startActivity(intent);
                }
            }
        });
        listView.setOnItemClickListener(this);
    }

    private void initdata() {
        // 如果没有继承TabActivity时，通过该种方法加载启动tabHost
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("直属部门")
                .setContent(R.id.view1));
        tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("间属部门")
                .setContent(R.id.view1));
       /* tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("三阶店")
                .setContent(R.id.view1));*/
        tabhost.setOnTabChangedListener(this);
        tabhost.setCurrentTab(1);
        account.setText(phone);
        superior.setText(superiornum);
    }

    @Override
    public void onTabChanged(String tabId) {
        //TODO 移除sql
        if (!TextUtils.isEmpty(phone)) {
            String sql = null;
            if (tabId.equals("tab1")) {
                sql = "select phone,headimg,registered,level from userinfo where superiornum='" + phone + "'";
            }
            if (tabId.equals("tab2")) {
                sql = "select phone,headimg,registered,level from userinfo where superiornum in" +
                        "(select phone from userinfo where superiornum='" + phone + "')";
            }
            /*if (tabId.equals("tab3")) {
                sql = "select phone,headimg,registered,level from userinfo where superiornum in" +
                        "(select phone from userinfo where superiornum='" + phone + "')";
            }*/
            asyncGetCloudData(sql);
        } else
            ToastFactory.getMyToast("登陆后继续").show();
    }

    private void asyncGetCloudData(String sql) {
        progressDialog.show();
        RequestParams params = new RequestParams(GeneralUtil.GETPARTNER);
        params.addBodyParameter("sql", sql);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                if (!"0".equals(result)) {
                    list = parsPartnersJSON(result);
                    partnersAdapter = new PartnersAdapter(RecommendedActivity.this, list);
                    listView.setAdapter(partnersAdapter);
                } else {
                    ToastFactory.getMyToast("当前没有数据").show();
                    if (list != null) {
                        list.clear();
                        partnersAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                progressDialog.dismiss();
                ToastFactory.getMyToast("网络故障").show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list != null && !list.isEmpty()) {
            Partners partners = list.get(position);
            YWIMKit ywimKit = getmIMKit();
            if (ywimKit != null) {
                Intent intent = ywimKit.getChattingActivityIntent(partners.getPhone(), getString(R.string.openIMKey));
                startActivity(intent);
            }
        }
    }
}
