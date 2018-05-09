package iliker.fragment.person;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import cn.iliker.mall.R;
import com.alibaba.mobileim.YWIMKit;
import iliker.fragment.BaseFragment;
import iliker.fragment.type.AddressListActivity;
import iliker.mall.LoadMap;
import iliker.mall.LoginActivity;
import iliker.mall.LookingOneActivity;

import static com.iliker.application.CustomApplication.customApplication;
import static com.iliker.application.CustomApplication.getmIMKit;

public class Content_OPFragent extends BaseFragment implements OnClickListener {
    private RelativeLayout ziliao, location, store, editpwd, customerservice;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.content_op, null);
        }
        findViews();
        setListener();
        return view;
    }

    private void findViews() {
        ziliao = (RelativeLayout) view.findViewById(R.id.ziliao);
        location = (RelativeLayout) view.findViewById(R.id.location);
        store = (RelativeLayout) view.findViewById(R.id.store);
        editpwd = (RelativeLayout) view.findViewById(R.id.editpwd);
        customerservice = (RelativeLayout) view.findViewById(R.id.customerservice);
    }

    private void setListener() {
        ziliao.setOnClickListener(this);
        location.setOnClickListener(this);
        store.setOnClickListener(this);
        editpwd.setOnClickListener(this);
        customerservice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (customApplication.getUserinfo() == null) {
            startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        switch (v.getId()) {
            case R.id.ziliao:
                Intent intent = new Intent();
                intent.setClass(context, PersonSpace.class);
                startActivity(intent);
                break;
            case R.id.location:
                Intent addressIn = new Intent(context, AddressListActivity.class);
                context.startActivity(addressIn);
                break;
            case R.id.store:
                Intent mapintent = new Intent(context, LoadMap.class);
                context.startActivity(mapintent);
                break;
            case R.id.editpwd:
                Intent look = new Intent(context, LookingOneActivity.class);
                startActivity(look);
                break;
            case R.id.customerservice:
                YWIMKit ywimKit = getmIMKit();
                Intent activityIntent = ywimKit.getChattingActivityIntent("alq85115612@163.com", getString(R.string.openIMKey));
                startActivity(activityIntent);
                break;
        }
    }
}
