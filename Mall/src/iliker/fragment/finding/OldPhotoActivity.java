package iliker.fragment.finding;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import cn.iliker.mall.R;
import iliker.fragment.faxian.ImageActivity;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fjl.widget.DialogFactory.initDialog;
import static iliker.utils.HttpHelp.getHttpUtils;

public class OldPhotoActivity extends Activity {
    private ListView ll;
    private PhotoAdapter pa;
    private String nickname;
    private String date;
    private Dialog dailog;
    private static List<Map<String, String>> list;
    private final MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            OldPhotoActivity activity = (OldPhotoActivity) reference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.ll.setAdapter(activity.pa);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oldphoto);
        ll = (ListView) this.findViewById(R.id.oldphotoll);
        Bundle bundle = getIntent().getBundleExtra("property");
        nickname = bundle.getString("nickname");
        date = bundle.getString("date");
    }

    @Override
    protected void onResume() {
        getShareByOld();
        super.onResume();
    }

    private void getShareByOld() {
        RequestParams params = new RequestParams(GeneralUtil.GETOLDPHOTOSVC);
        params.addBodyParameter("nickname", nickname);
        params.addBodyParameter("date", date);
        dailog = initDialog(this);
        dailog.show();
        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        dailog.dismiss();
                        if (!TextUtils.isEmpty(result)) {
                            list = pareseJSON(result);
                            pa = new PhotoAdapter(list);
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        dailog.dismiss();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

    }

    private List<Map<String, String>> pareseJSON(String responseString) {
        List<Map<String, String>> list;
        try {
            list = new ArrayList<>();
            JSONArray ja = new JSONArray(responseString);
            int len = ja.length();
            for (int i = 0; i < len; i++) {
                JSONObject jo = ja.getJSONObject(i);
                Map<String, String> map = new HashMap<>();
                map.put("shareID", jo.getString("shareID"));
                map.put("content", jo.getString("content"));
                map.put("piccount", jo.getString("piccount"));
                map.put("pic", jo.getString("pic"));
                list.add(map);
            }
        } catch (JSONException e) {
            return null;
        }
        return list;
    }

    class PhotoAdapter extends BaseAdapter {
        private final List<Map<String, String>> list;

        PhotoAdapter(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Map<String, String> getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.oldphoto_item, null);
                vh = new ViewHolder();
                vh.lv = (ImageView) convertView.findViewById(R.id.shareimg);
                vh.content = (TextView) convertView.findViewById(R.id.content);
                vh.rl = (RelativeLayout) convertView.findViewById(R.id.btn);
                vh.piccount = (TextView) convertView
                        .findViewById(R.id.piccount);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            Map<String, String> map = getItem(position);
            vh.content.setText(URLDecoder.decode(map.get("content")));
            vh.piccount.setText(String.valueOf("看大图\n" + map.get("piccount")));
            final String[] imgpaths = map.get("pic").split("#");
            for (int i = 0; i < imgpaths.length; i++) {
                imgpaths[i] = GeneralUtil.SHAREPATH + imgpaths[0];
            }
            vh.rl.setAlpha(0.6f);
            vh.rl.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(OldPhotoActivity.this,
                            ImageActivity.class);
                    intent.putExtra("imgpaths", imgpaths);
                    OldPhotoActivity.this.startActivity(intent);
                }

            });
            BitmapHelp.getBitmapUtils().bind(vh.lv, imgpaths[0]);
            return convertView;
        }

        class ViewHolder {
            public ImageView lv;
            public TextView content;
            TextView piccount;
            RelativeLayout rl;
        }

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        list.clear();
        super.onDestroy();
    }

}
