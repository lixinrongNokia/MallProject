package iliker.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fjl.widget.ToastFactory;

import cn.iliker.mall.R;
import iliker.fragment.mystore.AccountActivity;
import iliker.fragment.mystore.AnalysisActivity;
import iliker.fragment.mystore.MyGridAdapter;
import iliker.fragment.mystore.MyGridView;
import iliker.fragment.mystore.PartnersActivity;
import iliker.fragment.mystore.PromoteActivity;
import iliker.fragment.mystore.Question_Center;
import iliker.fragment.mystore.RecommendedActivity;
import iliker.mall.RegisterActivity;

import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.ViewUtils.removeParent;

public class MyStoreFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private MyGridView gridview;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.mystore_layout, null);
            initView();
            setListener();
        }
        removeParent(view);
        return view;
    }

    private void initView() {
        gridview = (MyGridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new MyGridAdapter(context));

    }

    private void setListener() {
        gridview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView) view.findViewById(R.id.tv_item);
        String moduleName = tv.getText().toString();
        /*
        {"成为会员","成为推客","金牌推客", "账户管理", "邀请合伙人", "分销管理","收益分析","答疑中心"}
         */
        if ("成为会员".equals(moduleName)) {
            if (customApplication.getUserinfo() == null) {
                Intent intent = new Intent(context, RegisterActivity.class);
                context.startActivity(intent);
            } else {
                ToastFactory.getMyToast("你已经是会员！").show();
            }
            return;
        }
        if ("收益分析".equals(moduleName)) {
            Intent intent = new Intent(context, AnalysisActivity.class);
            context.startActivity(intent);
            return;
        }
        if ("答疑中心".equals(moduleName)) {
            Intent intent = new Intent(context, Question_Center.class);
            context.startActivity(intent);
            return;
        }
        if (customApplication.getUserinfo() == null) {
            ToastFactory.getMyToast("登陆后操作").show();
            return;
        }
        if ("成为推客".equals(moduleName)) {
            Intent intent = new Intent(context, PromoteActivity.class);
            intent.putExtra("goldtwitter", false);
            startActivity(intent);
            return;
        }
        if ("金牌推客".equals(moduleName)) {
            Intent intent = new Intent(context, PromoteActivity.class);
            intent.putExtra("goldtwitter", true);
            startActivity(intent);
            return;
        }
        if ("账户管理".equals(moduleName)) {
            Intent accountIntent = new Intent(context, AccountActivity.class);
            context.startActivity(accountIntent);
            return;
        }
        if ("邀请合伙人".equals(moduleName)) {
            Intent intent = new Intent(context, PartnersActivity.class);
            context.startActivity(intent);
            return;
        }
        if ("分销管理".equals(moduleName)) {
            Intent intent = new Intent(context, RecommendedActivity.class);
            context.startActivity(intent);
        }

    }
}
