package iliker.fragment;

import android.view.View;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;
import iliker.fragment.person.OffLineFragment;
import iliker.fragment.person.OnlineFragment;

import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.ViewUtils.removeParent;

/**
 * 个人中心
 */
public class PersonFragment extends BaseFragment {
    private OnlineFragment online;
    private OffLineFragment offline;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.personview, null);
        }
        removeParent(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfo userInfo = customApplication.getUserinfo();
        if (userInfo != null) {
            if (offline != null) {
                getChildFragmentManager().beginTransaction().remove(offline).commit();
            }
            if (online == null) {
                online = new OnlineFragment();
            }
            getChildFragmentManager().beginTransaction().replace(R.id.userdemo, online).commit();
        } else {
            if (online != null) {
                getChildFragmentManager().beginTransaction().remove(online).commit();
            }
            if (offline == null) {
                offline = new OffLineFragment();
            }
            getChildFragmentManager().beginTransaction().replace(R.id.userdemo, offline).commit();
        }
    }

}
