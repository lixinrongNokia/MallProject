package iliker.fragment.finding;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import iliker.entity.Theme;
import iliker.utils.GeneralUtil;
import me.maxwin.view.XListView;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.Date;
import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * Created by WDHTC on 2016/5/24.
 */
public class ThemeActivityPager extends BaseFindPager implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private XListView xListView;
    private TextView empty;
    private List<Theme> list;

    public ThemeActivityPager(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        View view = View.inflate(context, R.layout.notification_theme, null);
        xListView = (XListView) view.findViewById(R.id.xl);
        empty = (TextView) view.findViewById(R.id.empty);
        setListener();
        return view;
    }

    private void setListener() {
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
    }

    @Override
    public void initDate() {
        RequestParams params = new RequestParams(GeneralUtil.GETTHEME);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("0".equals(result)) {
                    xListView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    xListView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    list = JSON.parseArray(result, Theme.class);
                    xListView.setAdapter(new ThemeAdapter(context, list));
                    xListView.setPullLoadEnable(false);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    public void onRefresh() {
        onLoad();
    }

    /**
     * 停止顶部更新、停止底部加载,更新时间
     */
    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        Date date = new Date(System.currentTimeMillis());
        String currentTime = sdf.format(date);
        xListView.setRefreshTime(currentTime);// 更新时间
        initDate();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = position < 0 ? 0 : (position - 1);
        Theme theme = list.get(index);
        Intent intent = new Intent(context, LoadThemeActivity.class);
        intent.putExtra("webUrl", theme.getWebUrl());
        context.startActivity(intent);
    }
}
