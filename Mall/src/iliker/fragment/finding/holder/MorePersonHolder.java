package iliker.fragment.finding.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;

import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * Created by WDHTC on 2016/5/12.
 */
public class MorePersonHolder {
    final private View view;
    final private Context context;

    private ImageView headImg;
    private TextView nickName;
    private TextView thumb;

    public MorePersonHolder(Context context) {
        this.context = context;
        this.view = loadViews();
        this.view.setTag(this);
        getPraiseCount();
    }

    private void getPraiseCount() {
        RequestParams params = new RequestParams(GeneralUtil.GETPRAISEBYTOUID);
        params.addBodyParameter("toUID", String.valueOf(CustomApplication.customApplication.getUserinfo().getuID()));
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                thumb.setText(responseInfo);
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

    private View loadViews() {
        View view = View.inflate(context, R.layout.more_person_layout, null);
        this.headImg = (ImageView) view.findViewById(R.id.headImg);
        this.nickName = (TextView) view.findViewById(R.id.nickName);
        this.thumb = (TextView) view.findViewById(R.id.thumb);
        return view;
    }

    public View getConvertView() {
        return view;
    }

    public void setData() {
        String nickname = "";
        String url = "";
        UserInfo userInfo = CustomApplication.customApplication.getUserinfo();
        if (userInfo != null) {
            nickname = userInfo.getNickName();
            url = userInfo.getHeadImg();
        }
        this.nickName.setText(nickname);
        BitmapHelp.getBitmapUtils().bind(this.headImg, GeneralUtil.SHAREPATH + url);
    }
}
