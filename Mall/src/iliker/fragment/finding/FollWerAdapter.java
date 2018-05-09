package iliker.fragment.finding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fjl.widget.ToastFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;

import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.HttpHelp.getHttpUtils;

class FollWerAdapter extends BaseAdapter {
    private final List<Map<String, String>> data;
    private final LayoutInflater inflater;
    private String status = null;

    FollWerAdapter(String status, Context context,
                   List<Map<String, String>> data) {
        super();
        this.status = status;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.follweritem, null);
            holder = new ViewHolder();
            holder.headimg = (ImageView) convertView.findViewById(R.id.headimg);
            holder.nickname = (TextView) convertView
                    .findViewById(R.id.nickname2);
            holder.signature = (TextView) convertView.findViewById(R.id.desc);
            holder.follwering = (ImageButton) convertView
                    .findViewById(R.id.follwering);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        final Map<String, String> map = data.get(position);
        holder.nickname.setText(map.get("nickname"));
        holder.signature.setText(map.get("signature"));
        String url = GeneralUtil.HOSTURL + map.get("headimg");
        BitmapHelp.getBitmapUtils().bind(holder.headimg, url);
        if ("1".equals(status))
            holder.follwering
                    .setBackgroundResource(R.drawable.btnbg_otherpersonal_areducefollow);
        else
            holder.follwering
                    .setBackgroundResource(R.drawable.btnbg_otherpersonal_addfollow);

        holder.follwering.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams("1".equals(status) ? GeneralUtil.DELFOLL : GeneralUtil.FOLL);
                if (status.equals("0")) {
                    params.addBodyParameter("form", customApplication.getUserinfo().getNickName());
                    params.addBodyParameter("to", map.get("nickname"));
                } else {
                    params.addBodyParameter("fid", map.get("fid"));
                }
                getHttpUtils().post(params,
                        new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                ToastFactory.getMyToast(result).show();
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {

                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
            }
        });
        return convertView;
    }

    final class ViewHolder {
        public ImageView headimg;
        public TextView nickname;
        public TextView signature;
        ImageButton follwering;
    }

}
