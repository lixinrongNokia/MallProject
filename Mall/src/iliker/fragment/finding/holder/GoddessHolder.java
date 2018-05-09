package iliker.fragment.finding.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cn.iliker.mall.R;
import iliker.fragment.finding.GoddessListActivity;
import iliker.utils.GeneralUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.HttpHelp.getHttpUtils;

public class GoddessHolder implements OnClickListener {
    private final View convertView;
    private final Context context;
    private final List<ImageView> imageViewList = new ArrayList<>();

    public GoddessHolder(Context context) {
        this.context = context;
        this.convertView = initViews();
        this.convertView.setTag(this);
    }

    public View getConvertView() {
        return convertView;
    }

    private View initViews() {
        View view = View.inflate(context, R.layout.head_activities, null);
        ImageView mianimg = (ImageView) view.findViewById(R.id.mian);
        ImageView level1 = (ImageView) view.findViewById(R.id.level1);
        ImageView level2 = (ImageView) view.findViewById(R.id.level2);
        ImageView level3 = (ImageView) view.findViewById(R.id.level3);
        ImageView level4 = (ImageView) view.findViewById(R.id.level4);
        imageViewList.add(level1);
        imageViewList.add(level2);
        imageViewList.add(level3);
        imageViewList.add(level4);
        mianimg.setOnClickListener(this);
        setListener();
        return view;
    }

    private void setListener() {
        for (ImageView imageView : imageViewList) {
            imageView.setOnClickListener(this);
        }
    }

    public void setDatas() {
        RequestParams params = new RequestParams(GeneralUtil.GETTOP4GODDESS + "?themeid=7");
        getHttpUtils().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                if (!"0".equals(responseInfo)) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseInfo);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String item = jsonArray.getString(i).split("#")[0];
                            getBitmapUtils().bind(imageViewList.get(i), GeneralUtil.HEADURL + item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    public void onClick(View v) {
        int position = 0;
        switch (v.getId()) {
            case R.id.mian:
            case R.id.level1:
                position = 0;
                break;
            case R.id.level2:
                position = 1;
                break;
            case R.id.level3:
                position = 2;
                break;
            case R.id.level4:
                position = 3;
                break;
        }
        Intent intent = new Intent(context, GoddessListActivity.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }
}
