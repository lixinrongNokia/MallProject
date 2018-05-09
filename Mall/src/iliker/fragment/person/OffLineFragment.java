package iliker.fragment.person;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import cn.iliker.mall.R;
import iliker.fragment.BaseFragment;
import iliker.mall.LoginActivity;

import static iliker.utils.ViewUtils.removeParent;

/**
 * 没有登录的状态
 *
 * @author Administrator
 */
@SuppressLint("InflateParams")
public class OffLineFragment extends BaseFragment implements OnClickListener {

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.offline, null);
            view.findViewById(R.id.offlinebtn).setOnClickListener(this);
        }
        removeParent(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        Editor editor = context.getSharedPreferences("applogin",
                Context.MODE_PRIVATE).edit();
        editor.putBoolean("isperson", true);
        editor.apply();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

}
