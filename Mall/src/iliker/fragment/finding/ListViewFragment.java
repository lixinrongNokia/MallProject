package iliker.fragment.finding;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.achep.header2actionbar.HeaderFragment;
import com.alibaba.fastjson.JSON;

import com.jaeger.library.StatusBarUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.iliker.mall.R;
import iliker.entity.Social;
import iliker.entity.UserInfo;
import iliker.fragment.finding.holder.Person_Item_Holder;
import iliker.fragment.finding.holder.Person_sond_Holder;
import iliker.utils.GeneralUtil;
import iliker.utils.ReadStreamUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.FormatDateTo.getAge;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * create by auto
 */
public abstract class ListViewFragment extends HeaderFragment implements View.OnClickListener {

    private ListView mListView;
    private String[] mListViewTitles;
    private boolean mLoaded;

    private FrameLayout mContentOverlay;
    ShowPersonality activity;
    private Person_sond_Holder sond_holder;
    private ImageView ipersonImage;
    private TextView tnickName;
    private TextView tsex_age;
    TextView thumbup;//点赞
    TextView tAnimation;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    android.view.animation.Animation animation;
    UserInfo webuser;

    private String[] photoAlbumArray = null;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        activity = (ShowPersonality) context;
        Bundle bundle = getArguments();
        webuser = (UserInfo) bundle.getSerializable("userinfo");
        setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_PARALLAX);
        setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
            @Override
            public void onHeaderScrollChanged(float progress, int height, int scroll) {
                ActionBar acctionBar = activity.getActionBar();
                if (acctionBar != null) {
                    height -= acctionBar.getHeight();
                    progress = (float) scroll / height;
                    if (progress > 1f) progress = 1f;
                    progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;
                    activity.getFadingActionBarHelper()
                            .setActionBarAlpha((int) (255 * progress));
                }
            }
        });
        new AsyncLoadSomething(this).execute(webuser.getuID());
        animation = AnimationUtils.loadAnimation(activity, R.anim.applaud_animation);
    }

    @Override
    public void onResume() {
        getPraiseCount();
        super.onResume();
    }

    private void getPraiseCount() {
        RequestParams params = new RequestParams(GeneralUtil.GETPRAISEBYTOUID);
        params.addBodyParameter("toUID", String.valueOf(webuser.getuID()));
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                thumbup.setText(responseInfo);
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    protected abstract void initDate();


    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.showperson_head_layout, container, false);
        tnickName = (TextView) view.findViewById(R.id.nickName);
        tsex_age = (TextView) view.findViewById(R.id.sex_age);
        thumbup = (TextView) view.findViewById(R.id.thumbup);
        tAnimation = (TextView) view.findViewById(R.id.tAnimation);
        ipersonImage = (ImageView) view.findViewById(R.id.personImage);
        ipersonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoAlbumArray != null) {
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    Uri mUri = Uri.parse(GeneralUtil.HEADURL + photoAlbumArray[0]);
                    it.setDataAndType(mUri, "image/*");
                    activity.startActivity(it);
                }
            }
        });
        thumbup.setOnClickListener(this);
        initDate();
        return view;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        tnickName.setText(webuser.getNickName());
        int age = 0;
        try {
            String birthday = webuser.getBirthday();
            age = getAge(format.parse(TextUtils.isEmpty(birthday) ? "1997-01-01" : birthday));
        } catch (ParseException e) {
        }
        tsex_age.setText(webuser.getSex() + "\t" + age);
        mListView = new ListView(activity);
        mListView.setSelector(android.R.color.transparent);
        if (mLoaded) setListViewTitles(mListViewTitles);
        return mListView;
    }

    @Override
    public View onCreateContentOverlayView(LayoutInflater inflater, ViewGroup container) {
        ProgressBar progressBar = new ProgressBar(getActivity());
        mContentOverlay = new FrameLayout(getActivity());
        mContentOverlay.addView(progressBar, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        if (mLoaded) mContentOverlay.setVisibility(View.GONE);
        return mContentOverlay;
    }

    private void setListViewTitles(String[] titles) {

        mLoaded = true;
        mListViewTitles = titles;
        if (mListView == null) return;

        mListView.setVisibility(View.VISIBLE);
        setListViewAdapter(mListView, new ShowPersonAdapter(titles));
    }

    /*protected void cancelAsyncTask(AsyncTask task) {
        if (task != null) task.cancel(false);
    }*/

    // //////////////////////////////////////////
    // ///////////// -- LOADER -- ///////////////
    // //////////////////////////////////////////

    class AsyncLoadSomething extends AsyncTask<Integer, Void, Social> {

        protected static final String TAG = "AsyncLoadSomething";

        final WeakReference<ListViewFragment> weakFragment;

        AsyncLoadSomething(ListViewFragment fragment) {
            this.weakFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final ListViewFragment audioFragment = weakFragment.get();
            if (audioFragment.mListView != null)
                audioFragment.mListView.setVisibility(View.INVISIBLE);
            if (audioFragment.mContentOverlay != null)
                audioFragment.mContentOverlay.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected Social doInBackground(Integer... args) {
            Social social = null;
            try {
                List<NameValuePair> pairList = new ArrayList<>();
                NameValuePair nameValuePair = new BasicNameValuePair("uid", args[0] + "");
                pairList.add(nameValuePair);
                HttpPost post = new HttpPost(GeneralUtil.GETSOCIALPROPER);
                HttpEntity entity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
                post.setEntity(entity);
                HttpClient client = new DefaultHttpClient();
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
                org.apache.http.HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String result = new String(ReadStreamUtil.readStream(response.getEntity().getContent()));
                    if (!"0".equals(result))
                        social = JSON.parseObject(result, Social.class);
                }
            } catch (IOException e) {
                e.printStackTrace();//连接异常
            }
            return social;
        }

        @Override
        protected void onPostExecute(Social social) {

            final ListViewFragment audioFragment = weakFragment.get();
            if (audioFragment == null) {
                return;
            }
            String[] strings;
            if (audioFragment.mContentOverlay != null)
                audioFragment.mContentOverlay.setVisibility(View.GONE);
            if (social != null) {
                String photoAlbum = social.getPhotoAlbum();
                if (!TextUtils.isEmpty(photoAlbum)) {

                    if (photoAlbum.contains("#")) {
                        photoAlbumArray = photoAlbum.split("#");
                    } else {
                        photoAlbumArray = new String[]{photoAlbum};
                    }
                    getBitmapUtils().bind(ipersonImage, GeneralUtil.HEADURL + photoAlbumArray[0]);
                    if (photoAlbumArray.length > 1) {
                        if (sond_holder == null)
                            sond_holder = new Person_sond_Holder(photoAlbumArray, activity);
                    }
                } else {
                    ipersonImage.setImageResource(R.drawable.sex);
                }
                strings = new String[]{"情感状态#" + social.getEmotionalState(), "魅力值#100", "职业#" + social.getJob(), "家乡#" + social.getHometown()};
                audioFragment.setListViewTitles(strings);
            } else {
                strings = new String[]{"情感状态#未知", "魅力值#100", "职业#未知", "家乡#未知"};
                audioFragment.setListViewTitles(strings);
                ipersonImage.setImageResource(R.drawable.sex);
            }
        }
    }

    public class ShowPersonAdapter extends BaseAdapter {
        final String[] strings;

        ShowPersonAdapter(String[] strings) {
            this.strings = strings;
        }

        @Override
        public int getCount() {
            if (sond_holder == null)
                return strings.length;
            return strings.length + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (sond_holder != null)
                return position > 0 ? 1 : 0;
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            if (sond_holder != null) {
                return super.getViewTypeCount() + 1;
            }
            return super.getViewTypeCount();
        }

        @Override
        public String getItem(int position) {
            if (sond_holder == null) {
                return strings[position];
            }
            int id = position - 1;
            return strings[id < 0 ? 0 : id];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                sond_holder.setDate();
                return sond_holder.getConvertView();
            }
            if (getItemViewType(position) == 1) {
                Person_Item_Holder personHolder;
                if (convertView == null) {
                    personHolder = new Person_Item_Holder(activity);
                } else {
                    personHolder = (Person_Item_Holder) convertView.getTag();
                }
                personHolder.setDate(getItem(position));
                return personHolder.getConvertView();
            }

            return null;
        }


    }

}
