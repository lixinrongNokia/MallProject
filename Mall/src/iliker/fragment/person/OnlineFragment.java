package iliker.fragment.person;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.readystatesoftware.viewbadger.BadgeView;
import iliker.entity.UserInfo;
import iliker.fragment.BaseFragment;
import iliker.mall.MainActivity;
import iliker.mall.MyCollActivity;
import iliker.utils.GeneralUtil;
import iliker.utils.QRUtils;
import org.xutils.image.ImageOptions;

import java.util.Timer;
import java.util.TimerTask;

import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.ViewUtils.removeParent;

/**
 * 在线状态
 *
 * @author Administrator
 */
@SuppressLint("InflateParams")
public class OnlineFragment extends BaseFragment implements View.OnClickListener {
    private TextView textview;// 用户名
    private ImageView headImg, pushIM;// 用户头像
    private RelativeLayout coll;//folldeme, fansdeme, ;
    private ImageView QRCode;
    private BadgeView badgeView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.online, null);
            findViews();
            setListener();
            badgeView = new BadgeView(context, pushIM);
        }
        removeParent(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
        initData();
//        registerPushMsgChange();
    }

    /*private void registerPushMsgChange() {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://cn.iliker.mall.aliPushMsg/getUnMsg"), null, null, new String[]{customApplication.getUserinfo().getNickName()}, null);
        while (cursor.moveToNext()) {
            MessageEntity entity = new MessageEntity(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
            Log.i("Push",entity.getMessageContent()+":=======:"+entity.getReceiver());
        }
    }*/

    /*定时器定时查询未读信息数量*/
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(unRunnable);
                }
            };
        }
        mTimer.schedule(mTimerTask, 0, 500);
    }

    private final Runnable unRunnable = new Runnable() {
        @Override
        public void run() {
            int unreadNum = MainActivity.unreadmsg;
            if (unreadNum > 0) {
                if (unreadNum > 100) {
                    badgeView.setText(String.valueOf("99+"));
                } else {
                    badgeView.setText(String.valueOf(unreadNum));
                }
                badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// 设置徽章位置为右上角
                badgeView.show();
            } else {
                badgeView.hide();// 将徽章隐藏
            }
        }
    };

    private void findViews() {
        textview = (TextView) view.findViewById(R.id.nickname);
        headImg = (ImageView) view.findViewById(R.id.userhead);
        QRCode = (ImageView) view.findViewById(R.id.qrcode);
        pushIM = (ImageView) view.findViewById(R.id.pushim);
        coll = (RelativeLayout) view.findViewById(R.id.coll);
    }

    private void initData() {
        UserInfo userInfo = customApplication.getUserinfo();
        if (userInfo != null) {
            textview.setText(userInfo.getNickName());
            ImageOptions imageOptions = new ImageOptions.Builder().setFailureDrawableId(R.drawable.ic_public_mr_headpicture).build();
            getBitmapUtils().bind(headImg, GeneralUtil.SHAREPATH + userInfo.getHeadImg(), imageOptions);
        }
    }

    private void setListener() {
        coll.setOnClickListener(this);
        pushIM.setOnClickListener(this);
        QRCode.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(unRunnable);
        stopTimer();
        super.onPause();
    }

    /*停止定时器*/
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coll:
                Intent intent = new Intent(context, MyCollActivity.class);
                context.startActivity(intent);
                break;
            case R.id.pushim:
                Intent intent2 = new Intent(context, IMListActivity.class);
                context.startActivity(intent2);
                break;
            case R.id.qrcode:
                Bitmap bitmap = QRUtils.createQRBitmap("{\"phone\":\"" + customApplication.getUserinfo().getPhone() + "\"}");
                // 显示QRCode
                if (null != bitmap) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View cardview = View.inflate(context, R.layout.card_layout, null);
                    ImageView imageView = (ImageView) cardview.findViewById(R.id.cardView);
                    imageView.setImageBitmap(bitmap);
                    builder.setView(cardview).show();
                }
                break;
        }
    }

}
