package iliker.fragment.finding;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import cn.iliker.mall.R;
import iliker.entity.ActionSwitch;
import iliker.entity.Goddess;
import iliker.entity.UserInfo;
import iliker.utils.GeneralUtil;

import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 显示全国排名
 * Created by WDHTC on 2016/6/14.
 */
public class GoddessListActivity extends AppCompatActivity implements GoddessAdapter.OnRecyclerViewItemClickListener {
    private RecyclerView goddessLv;
    private GoddessAdapter goddessAdapter;

    private PopupWindow popupWindow;
    private UserInfo userInfo;
    private int themeID;
    private Goddess goddess;
    private Dialog dialog;
    private static String actionName;
    private AlertDialog builder;
    private Button actionbtn;
    private RecyclerView.LayoutManager manager;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goddesslist_layout);
        themeID = getIntent().getIntExtra("themeID", 7);
        position = getIntent().getIntExtra("position", 0);
        CustomApplication cap = (CustomApplication) getApplication();
        dialog = DialogFactory.initDialog(this);
        userInfo = cap.getUserinfo();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle("排行榜");
        }
        getGoddess();
        findViews();
        initData();
    }

    private void getGoddess() {
        dialog.show();
        RequestParams params = new RequestParams(GeneralUtil.GETGODDESS);
        params.addBodyParameter("themeid", themeID + "");
        params.addBodyParameter("fromUID", userInfo.getuID() + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                goddess = JSON.parseObject(responseInfo, Goddess.class);
                goddessAdapter = new GoddessAdapter(goddess, GoddessListActivity.this);
                goddessAdapter.setOnRecyclerViewItemClickListener(GoddessListActivity.this);
                goddessLv.setAdapter(goddessAdapter);

                manager.scrollToPosition(position);
                dialog.dismiss();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                dialog.dismiss();
            }
        });
    }

    private void initData() {
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.offsetChildrenHorizontal(LinearLayoutManager.HORIZONTAL);
        goddessLv.setLayoutManager(manager);
        goddessLv.setItemAnimator(new DefaultItemAnimator());
        popupWindow = new PopupWindow(this);
        Goddess_pop_Hodler goddess_pop_hodler = new Goddess_pop_Hodler(this);
        popupWindow.setContentView(goddess_pop_hodler.getConvertView());
        popupWindow.setFocusable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });
    }

    private void findViews() {
        goddessLv = (RecyclerView) findViewById(R.id.goddessLv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuitem = menu.add(0, 1, 0, "");
        menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuitem.setIcon(android.R.drawable.ic_menu_send);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return true;
                }
                this.finish();
                break;
            case 1:
                popupWindow.showAtLocation(findViewById(R.id.goddesslayout), Gravity.BOTTOM, 0, 0);
                backgroundAlpha(0.5f);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void onItemClick(int position) {
        UserInfo webuserInfo = goddess.getUsers().get(position);
        Intent intent;
        boolean personCenter = false;
        if (webuserInfo.getNickName().equals(userInfo.getNickName())) {
            intent = new Intent(this, InShowPersonAlity.class);
            personCenter = true;
        } else {
            intent = new Intent(this, OutShowPersonAlity.class);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("userinfo", webuserInfo);
        bundle.putBoolean("personCenter", personCenter);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    public class Goddess_pop_Hodler implements OnClickListener {
        private final View convertView;
        private final Context context;

        private TextView action_Desc;
        private TextView actionSeting;
        private TextView close;

        public Goddess_pop_Hodler(Context context) {
            this.context = context;
            this.convertView = initViews();
            this.convertView.setTag(this);
        }

        public View getConvertView() {
            return convertView;
        }

        public View initViews() {
            View view = View.inflate(context, R.layout.popuplayout_view, null);
            this.action_Desc = (TextView) view.findViewById(R.id.action_Desc);
            this.actionSeting = (TextView) view.findViewById(R.id.actionSeting);
            this.close = (TextView) view.findViewById(R.id.close);
            setListener();
            return view;
        }

        private void setListener() {
            this.action_Desc.setOnClickListener(this);
            this.actionSeting.setOnClickListener(this);
            this.close.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_Desc:
                    break;
                case R.id.actionSeting:
                    createAlertView();
                case R.id.close:
                    popupWindow.dismiss();
                    break;
            }
        }

        private void createAlertView() {
            if (builder == null) {
                builder = new AlertDialog.Builder(GoddessListActivity.this).create();
                View actiondialog_layout = View.inflate(GoddessListActivity.this, R.layout.actiondialog, null);
                actionbtn = (Button) actiondialog_layout.findViewById(R.id.actionbtn);
                actionbtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        respClick();
                    }
                });
                actiondialog_layout.findViewById(R.id.closebtn).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
                builder.setView(actiondialog_layout);
            }
            actionName = ActionSwitch.JOIN.getName();
            for (UserInfo atThemeuser : goddess.getUsers()) {
                if (userInfo.getuID() == atThemeuser.getuID()) {
                    actionName = ActionSwitch.EXIT.getName();
                    break;
                }
            }
            actionbtn.setText(actionName);
            builder.show();

           /* builder.setPositiveButton(actionName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    respClick();
                }
            }).setNegativeButton("关闭", null).show();*/
        }

        private void respClick() {
            int ationType;
            if (actionName.equals(ActionSwitch.JOIN.getName())) {
                ationType = 1;
            } else {
                ationType = 0;
            }

            RequestParams params = new RequestParams(GeneralUtil.ADDTHEME);
            params.addBodyParameter("uID", userInfo.getuID() + "");
            params.addBodyParameter("themeID", themeID + "");
            params.addBodyParameter("actionType", ationType + "");//1代表添加 0代表删除
            getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String responseInfo) {
                    int code = Integer.parseInt(responseInfo);
                    if (code < 1) respClick();
                    else {
                        ToastFactory.getMyToast("操作成功!").show();
                        builder.dismiss();
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ToastFactory.getMyToast("网络异常").show();
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

}
