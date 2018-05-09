package iliker.fragment.faxian;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.AutoScrollViewPager;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;

import org.xutils.ImageManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import cn.iliker.mall.R;
import cn.sharesdk.onekeyshare.OnekeyShare;
import iliker.entity.Share;
import iliker.fragment.faxian.RecordButton.OnFinishedRecordListener;
import iliker.fragment.type.GoodsPDIMGActivity;
import iliker.utils.FormatDateTo;
import iliker.utils.GeneralUtil;
import iliker.utils.ParsJsonUtil;

import static com.fjl.widget.DialogFactory.initDialog;
import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 分享详情与评论列表
 *
 * @author Administrator
 */
public class ShareDetailsActivity extends Activity implements OnClickListener,
        OnFinishedRecordListener {
    private ImageView headImg, ibtn_sharenum;// 分享图片，头像，分享按钮
    private TextView nickName, tv_maketime, sharecontent;// 昵称，发布时间，内容，评论数量
    private TextView tv_commentCount;// 显示评论数量文本
    private Button btn_follow, btn_commenttype, btn_publish;// 取消关注,输入类型，发送
    private RecordButton btn_recordbutton;// 录音按钮
    private EditText et_textcontent;// 输入评论内容框
    private Share share;// 分享序列化实体上个页面传过来
    private Dialog progressDialog;// 等待弹出框
    private InnerListView comments;// 评论列表listview
    private DevAdapter devAdapter;// 评论适配器
    private File audioFile;// 上传的音频文件
    private List<Map<String, String>> records;// 所有的评论数据
    private static final String RECORD_DIR = "/sound";
    // 获得存储在外部的路径
    private static File dir;
    private static MediaPlayer mp;
    private int isok = 0;
    @ViewInject(R.id.poster_pager)
    private AutoScrollViewPager mPosterPager;
    @ViewInject(R.id.points)
    private LinearLayout pointsLayout;
    private ImageManager bitmapUtils = null;
    private int index = 0;
    private ScrollView scrollView;
    private AlertDialog.Builder builder;
    private final Map<Integer, Button> btnmap = new LinkedHashMap<>();// 记录点击过的语音按钮
    private List<A> listCheck;// 订单项目勾选状态
    private ImageButton backbtn;
    /**
     * 广告图片
     */
    private List<String> posterImage = null;
    /**
     * 标记点集合
     */
    private List<ImageView> points = null;
    /**
     * 广告个数
     */
    private static int count;
    /**
     * 循环间隔
     */
    private static String unaem;

    private String[] imgPath;

    private float density;
    private final Mhandler mhandler = new Mhandler(this);
    private final int REQUEST_CODE_RECORD_AUDIO = 115;
    private String soundName;
    private Button btn_report;

    class Mhandler extends Handler {
        final WeakReference<Context> weakReference;

        Mhandler(Context context) {
            this.weakReference = new WeakReference<>(context);
        }

        public void handleMessage(android.os.Message msg) {
            ShareDetailsActivity activity = (ShareDetailsActivity) weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 7:
                        devAdapter = new DevAdapter(activity, records);
                        comments.setAdapter(activity.devAdapter);
                        comments.setParentScrollView(scrollView);
                        comments.setMaxHeight((int) (400 * density + 0.5f));
                        devAdapter.notifyDataSetChanged();
                        break;
                    case 3:
                    case 5:
                        new AsyncGetComm().execute(share.getShareID() + "");
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playaudio);
        share = getIntent().getParcelableExtra("share");
        dir = new File(Environment.getExternalStorageDirectory(),
                "Android/data/" + this.getPackageName() + RECORD_DIR);
        density = getResources().getDisplayMetrics().density;
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) dir = getCacheDir();
        }

        count = share.getPic().split("#").length;
        unaem = CustomApplication.customApplication.getUname();
        findViews();
        setListener();
        init();
        initDate();
        initPoints();
        initPoster();
    }

    private void findViews() {
        mPosterPager = (AutoScrollViewPager) findViewById(R.id.poster_pager);
        comments = (InnerListView) findViewById(R.id.lv_audioplaylistview);
        btn_recordbutton = (RecordButton) findViewById(R.id.btn_recordbutton);
        headImg = (ImageView) findViewById(R.id.headImg);
        ibtn_sharenum = (ImageView) findViewById(R.id.ibtn_sharenum);
        nickName = (TextView) findViewById(R.id.nickName);
        tv_maketime = (TextView) findViewById(R.id.tv_maketime);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        tv_commentCount = (TextView) findViewById(R.id.tv_commentCount);
        sharecontent = (TextView) findViewById(R.id.sharecontent);
        btn_follow = (Button) findViewById(R.id.btn_follow);
        if (share.getNickName().equals(unaem)) {
            btn_follow.setVisibility(View.INVISIBLE);
        }
        et_textcontent = (EditText) findViewById(R.id.et_textcontent);
        btn_publish = (Button) findViewById(R.id.btn_publish);
        btn_commenttype = (Button) findViewById(R.id.btn_commenttype);
        btn_report = (Button) findViewById(R.id.btn_requestnum);
        backbtn = (ImageButton) findViewById(R.id.backbtn);
    }

    /**
     * 初始化分享的视图
     */
    private void initDate() {
        nickName.setText(share.getNickName());
        tv_maketime.setText(share.getReleaseTime());
        try {
            sharecontent.setText(URLDecoder.decode(share.getContent(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        bitmapUtils.bind(headImg, GeneralUtil.SHAREPATH + share.getHeadImg());
        new AsyncGetComm().execute(share.getShareID() + "");
    }

    private void setListener() {
        ibtn_sharenum.setOnClickListener(this);
        btn_commenttype.setOnClickListener(this);
        btn_publish.setOnClickListener(this);
        btn_follow.setOnClickListener(this);
        askRECORD_AUDIO_Permission();
        btn_recordbutton.setSavePath(getPath());
        btn_recordbutton.setOnFinishedRecordListener(this);
        backbtn.setOnClickListener(this);
        btn_report.setOnClickListener(this);
    }

    // 图片轮播
    private void init() {
        x.view().inject(this);
        points = new LinkedList<>();
        posterImage = new LinkedList<>();
        bitmapUtils = getBitmapUtils();
        imgPath = share.getPic().split("#");
        for (String img : imgPath) {
            posterImage.add(GeneralUtil.SHAREPATH + img);
        }
    }

    private void initPoints() {
        // 设置 ViewPager的高度为屏幕宽度的1/5
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, getScreen(this).heightPixels / 2);
        mPosterPager.setLayoutParams(params);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 15, 8, 15);

        for (int i = 0; i < count; i++) {
            // 添加标记点
            ImageView point = new ImageView(this);

            if (i == index % count) {
                point.setBackgroundResource(R.drawable.feature_point_cur);
            } else {
                point.setBackgroundResource(R.drawable.feature_point);
            }
            point.setLayoutParams(lp);

            points.add(point);
            pointsLayout.addView(point);
        }
    }

    private void initPoster() {
        mPosterPager.setAdapter(new PosterPagerAdapter());
        mPosterPager.setCurrentItem(count * 500);
        mPosterPager.setInterval(3000);
        mPosterPager.addOnPageChangeListener(new PosterPageChange());
        mPosterPager
                .setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mPosterPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosterPager.stopAutoScroll();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mPosterPager.startAutoScroll();
                        break;
                    case MotionEvent.ACTION_UP:
                        mPosterPager.startAutoScroll();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 获取屏幕宽高
     */
    private DisplayMetrics getScreen(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (count > 1) {
            mPosterPager.startAutoScroll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPosterPager.stopAutoScroll();
    }

    private class PosterPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (count > 1)
                return Integer.MAX_VALUE;
            return 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(ShareDetailsActivity.this);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            bitmapUtils.bind(imageView, posterImage.get(position % count));
            container.addView(imageView);
            imageView.setOnClickListener(new PosterClickListener(position % count));
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    private class PosterPageChange implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            index = position;
            for (int i = 0; i < count; i++) {
                points.get(i).setBackgroundResource(R.drawable.feature_point);
            }
            points.get(position % count).setBackgroundResource(
                    R.drawable.feature_point_cur);
        }

    }

    private class PosterClickListener implements OnClickListener {

        private final int position;

        PosterClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            mPosterPager.stopAutoScroll();
            Intent intent = new Intent(ShareDetailsActivity.this,
                    ImageActivity.class);
            String[] strings = new String[posterImage.size()];
            intent.putExtra("imgpaths", posterImage.toArray(strings));
            intent.putExtra(GoodsPDIMGActivity.CURRENT_INDEX, position);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_sharenum:// 弹出分享控件
                OnekeyShare oks = new OnekeyShare();
                // 关闭sso授权
                oks.disableSSOWhenAuthorize();
                oks.setTitle(share.getNickName());
                oks.setTitleUrl("http://www.iliker.cn/");
                oks.setText(share.getContent());
                oks.setImageUrl(posterImage.get(0));
                // 启动分享GUI
                oks.show(this);
                break;
            case R.id.btn_commenttype:// 改变底部评论类型样式
                if (et_textcontent.getVisibility() == View.GONE
                        && btn_publish.getVisibility() == View.GONE) {
                    et_textcontent.setVisibility(View.VISIBLE);
                    btn_publish.setVisibility(View.VISIBLE);
                    btn_recordbutton.setVisibility(View.GONE);
                    btn_commenttype
                            .setBackgroundResource(R.drawable.btnbg_play_comment_audio);
                } else {
                    btn_recordbutton.setVisibility(View.VISIBLE);
                    et_textcontent.setVisibility(View.GONE);
                    btn_publish.setVisibility(View.GONE);
                    btn_commenttype
                            .setBackgroundResource(R.drawable.btnbg_play_comment_text);
                }
                break;
            case R.id.btn_publish:
                String comm = et_textcontent.getText().toString().trim();
                if (TextUtils.isEmpty(comm)) {
                    Animation shake = AnimationUtils.loadAnimation(this,
                            R.anim.animlayout);
                    et_textcontent.startAnimation(shake);
                } else {
                    asyncUpLoad(comm);
                    InputMethodManager inputMethodManager = (InputMethodManager) this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    View view = getCurrentFocus();
                    if (view != null) {
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    et_textcontent.setText(null);
                    mhandler.sendEmptyMessageDelayed(3, 1000);
                }
                break;

            case R.id.btn_follow:
                RequestParams params = new RequestParams(GeneralUtil.FOLL);
                params.addBodyParameter("form", unaem);
                params.addBodyParameter("to", share.getNickName());
                getHttpUtils().post(params,
                        new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Toast.makeText(ShareDetailsActivity.this, result, Toast.LENGTH_SHORT).show();
                                btn_follow.setBackgroundResource(R.drawable.btnbg_otherpersonal_areducefollow);
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
                break;
            case R.id.btn_requestnum:
                RequestParams reportParams = new RequestParams(GeneralUtil.REPORTSHARE);
                reportParams.addBodyParameter("reportShare.mark", 3 + "");
                reportParams.addBodyParameter("reportShare.userinfo.uid", CustomApplication.customApplication.getUserinfo().getuID() + "");
                reportParams.addBodyParameter("reportShare.share.shareId", share.getShareID() + "");
                getHttpUtils().post(reportParams,
                        new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                JSONObject jsonObject = JSON.parseObject(result);
                                if (jsonObject.getBooleanValue("success")) {
                                    ToastFactory.getMyToast("举报成功").show();
                                } else {
                                    ToastFactory.getMyToast("举报失败").show();
                                }
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
                break;
            case R.id.backbtn:
                finish();
                break;
        }
    }

    private void askRECORD_AUDIO_Permission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int checkCallPhonePermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
            } else {
                btn_recordbutton.setSavePath(getPath());
                btn_recordbutton.setOnFinishedRecordListener(this);
            }
        } else {
            btn_recordbutton.setSavePath(getPath());
            btn_recordbutton.setOnFinishedRecordListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btn_recordbutton.setSavePath(getPath());
                    btn_recordbutton.setOnFinishedRecordListener(this);
                } else {
                    if (et_textcontent.getVisibility() == View.GONE
                            && btn_publish.getVisibility() == View.GONE) {
                        et_textcontent.setVisibility(View.VISIBLE);
                        btn_publish.setVisibility(View.VISIBLE);
                        btn_recordbutton.setVisibility(View.GONE);
                        btn_commenttype
                                .setBackgroundResource(R.drawable.btnbg_noplay_comment_audio);
                        btn_commenttype.setEnabled(false);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class AsyncGetComm extends
            AsyncTask<String, String, List<Map<String, String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null)
                progressDialog = initDialog(ShareDetailsActivity.this);
            progressDialog.show();
        }

        @Override
        protected List<Map<String, String>> doInBackground(String... params) {
            return ParsJsonUtil.getComments(params[0],
                    new iliker.utils.ParsJsonUtil.HttpDataUtilCallback() {
                        @Override
                        public void httpRequestTimeOutCallback() {
                            ShareDetailsActivity.this
                                    .runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            loadNotework();
                                        }

                                    });
                        }
                    });
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (records != null) {
                records.clear();
            }
            records = result;
            tv_commentCount.setText(records == null ? "0条评论" : String
                    .valueOf(records.size()) + "条评论");
            if (records != null) {
                mhandler.sendEmptyMessage(7);
            }
        }
    }

    private void loadNotework() {
        if (builder == null) {
            builder = new Builder(ShareDetailsActivity.this);
        }
        builder.setMessage("连接服务器失败");
        builder.setPositiveButton("刷新",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new AsyncGetComm().execute(share.getShareID() + "");
                    }

                }).show();
    }

    private void asyncUpLoad(String str) {
        RequestParams params = new RequestParams(GeneralUtil.ADDCOMM);
        params.addBodyParameter("shareid", share.getShareID() + "");
        params.addBodyParameter("nickname", unaem);
        if (!TextUtils.isEmpty(str)) {
            params.addBodyParameter("commtext", str);
        }
        if (audioFile != null) {
            params.addBodyParameter("commaudio", audioFile);
        }
        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {


                    @Override
                    public void onSuccess(String result) {
                        isok = Integer.valueOf(result);
                        if (isok == 1) {
                            if (audioFile != null) {
                                audioFile.delete();
                            }
                        }
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

    /**
     * 获取音频文件存放路径
     */
    @SuppressLint("SimpleDateFormat")
    private String getPath() {
        soundName = new SimpleDateFormat("yyyyMMdd_HHmmss_SS", Locale.CHINA).format(new Date());
        File path = new File(dir, soundName + ".mp3");
        return path.getAbsolutePath();
    }

    /*
     * 重新设置播放文件路径可以重复录音 重新计算ListView高度更新ListView条目
     *
     */
    @Override
    public void onFinishedRecord(String audioPath) {
        audioFile = new File(dir, soundName + ".mp3");
        try {
            InputStream inputStream = new FileInputStream(audioFile);
            if (inputStream.available() > 0) {
                asyncUpLoad(null);
                mhandler.sendEmptyMessageDelayed(5, 1000);
            } else {
                audioFile.delete();
                audioFile = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        btn_recordbutton.setSavePath(getPath());
    }

    /**
     * 评论列表适配器
     *
     * @author Administrator
     */

    private class DevAdapter extends BaseAdapter {
        final private List<Map<String, String>> list;
        final private LayoutInflater inflater;
        int selectid;
        boolean isfriest = true;

        DevAdapter(Activity activity, List<Map<String, String>> list) {
            this.list = list;
            inflater = LayoutInflater.from(activity);
            listCheck = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                A a = new A(A.TYPE_NOCHECKED);
                listCheck.add(a);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            Holder holder;
            if (arg1 == null) {
                arg1 = inflater.inflate(R.layout.list_cell, null);
                holder = new Holder();
                holder.headimg = (ImageView) arg1.findViewById(R.id.headimg2);
                holder.nickname = (TextView) arg1.findViewById(R.id.nickname2);
                holder.pingtext = (TextView) arg1.findViewById(R.id.cell_tv);
                holder.audiobtn = (Button) arg1.findViewById(R.id.bofan);
                holder.time = (TextView) arg1.findViewById(R.id.fabutime);
                arg1.setTag(holder);
            }
            holder = (Holder) arg1.getTag();
            final Map<String, String> map = list.get(arg0);
            String headImg = map.get("headImg");
            if (!TextUtils.isEmpty(headImg)) {
                String url = GeneralUtil.SHAREPATH + headImg;
                bitmapUtils.bind(holder.headimg, url);
            }
            holder.nickname.setText(map.get("nickname"));
            final Button btn = holder.audiobtn;

            holder.audiobtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isfriest) {// 第一次点击播放
                        btn.setBackgroundResource(R.drawable.btnbg_commentaudio_playing);
                        listCheck.get(arg0).type = A.TYPE_CHECKED;
                        selectid = arg0;// 记录当前被点击的按钮的位置
                        btnmap.put(selectid, (Button) v);// 当前被点击的按钮
                        isfriest = false;
                    } else {
                        if (btn != btnmap.get(selectid)) {
                            for (A nocheck : listCheck) {
                                nocheck.type = A.TYPE_NOCHECKED;
                            }
                            listCheck.get(arg0).type = A.TYPE_CHECKED;
                            btn.setBackgroundResource(R.drawable.btnbg_commentaudio_playing);
                            btnmap.get(selectid).setBackgroundResource(
                                    R.drawable.btnbg_commentaudio_normal);
                            selectid = arg0;
                            btnmap.put(selectid, (Button) v);// 当前被点击的按钮
                        } else {
                            btn.setBackgroundResource(R.drawable.btnbg_commentaudio_normal);
                            listCheck.get(arg0).type = A.TYPE_NOCHECKED;
                            stop();
                            isfriest = true;// 如果点击的是同一个按钮就停止播放重新来
                            return;
                        }
                    }
                    try {
                        mp = createMediaPlayer(GeneralUtil.SOUNDURL + map.get("commAudioPath"));
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();

                            }
                        });
                        mp.setOnCompletionListener(new OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                btn.setBackgroundResource(R.drawable.btnbg_commentaudio_normal);
                                listCheck.get(arg0).type = A.TYPE_NOCHECKED;
                                DevAdapter.this.notifyDataSetChanged();
                            }

                        });
                        mp.setOnErrorListener(new OnErrorListener() {

                            @Override
                            public boolean onError(MediaPlayer mp, int what,
                                                   int extra) {
                                btn.setBackgroundResource(R.drawable.btnbg_commentaudio_normal);
                                return true;
                            }

                        });

                    } catch (Exception e) {
                    }

                }

            });
            if (map.get("commText").length() == 0) {
                holder.pingtext.setVisibility(View.GONE);
                holder.audiobtn.setVisibility(View.VISIBLE);
                if (listCheck.get(arg0).type == A.TYPE_NOCHECKED) {
                    holder.audiobtn
                            .setBackgroundResource(R.drawable.btnbg_commentaudio_normal);
                } else {
                    holder.audiobtn
                            .setBackgroundResource(R.drawable.btnbg_commentaudio_playing);
                }
                holder.audiobtn.setText(String.valueOf(map.get("audioLen") + "\""));

            } else {
                holder.audiobtn.setVisibility(View.GONE);
                holder.pingtext.setVisibility(View.VISIBLE);
                holder.pingtext.setText(map.get("commText"));
            }
            String str = map.get("commTime");
            String commtime = str.substring(0, str.lastIndexOf("."));
            holder.time.setText(FormatDateTo.format(FormatDateTo
                    .StrToDate(commtime)));
            return arg1;
        }

    }

    /**
     * 评论条目视图类重用
     *
     * @author Administrator
     */
    private final class Holder {
        private ImageView headimg;
        private TextView nickname;
        private TextView pingtext;
        private Button audiobtn;
        private TextView time;
    }

    class A {// 内部类标识checkBox状态

        static final int TYPE_CHECKED = 1;
        static final int TYPE_NOCHECKED = 0;

        int type;

        A(int type) {
            this.type = type;
        }
    }

    /**
     * 返回播放对象MediaPlayer
     */
    private static MediaPlayer createMediaPlayer(String mediapath)
            throws IOException {
        if (mp != null) {
            stop();
        }
        if (mp == null) {
            mp = new MediaPlayer();
        }
        mp.setDataSource(mediapath);
        mp.prepareAsync();
        return mp;
    }

    /**
     * 置空MediaPlayer
     */
    private static void stop() {
        mp.stop();
        mp.release();
        mp = null;
    }

    @Override
    protected void onDestroy() {
        if (listCheck != null) {
            listCheck.clear();
            listCheck = null;
        }
        if (!btnmap.isEmpty()) {
            btnmap.clear();
        }
        if (mp != null) {
            mp.release();
            mp = null;
        }
        delFileUnRoot(dir);
        mhandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    static private void delFileUnRoot(File f) {
        if (null == f || !f.exists()) {
            return;
        }
        Stack<File> tmpFileStack = new Stack<>();
        tmpFileStack.push(f);
        try {
            while (!tmpFileStack.isEmpty()) {
                File curFile = tmpFileStack.pop();
                if (null == curFile) {
                    continue;
                }
                if (curFile.isFile()) {
                    curFile.delete();
                } else {
                    File[] tmpSubFileList = curFile.listFiles();
                    if (null == tmpSubFileList || 0 == tmpSubFileList.length) {
                        curFile.delete();
                    } else {
                        tmpFileStack.push(curFile); // !!!
                        for (File item : tmpSubFileList) {
                            tmpFileStack.push(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
