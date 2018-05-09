package iliker.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import cn.iliker.mall.R;
import iliker.mall.TestVersionUpdateActivity;

/**
 * 版本更新后台下载服务
 *
 * @author Administrator
 */
public class VersionService extends IntentService {
    private NotificationManager notificationMrg;
    private int old_process = 0;
    private boolean isFirstStart = false;

    public VersionService() {
        super("VersionService");
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 1为出现，2为隐藏
            if (TestVersionUpdateActivity.loading_process > 99) {
                notificationMrg.cancel(0);
                stopSelf();
                return;
            }
            if (TestVersionUpdateActivity.loading_process > old_process) {
                displayNotificationMessage(TestVersionUpdateActivity.loading_process);
            }

            new Thread() {
                public void run() {
                    isFirstStart = false;
                    Message msg = mHandler.obtainMessage();
                    mHandler.sendMessage(msg);
                }
            }.start();
            old_process = TestVersionUpdateActivity.loading_process;
        }
    };

    @SuppressWarnings("deprecation")
    private void displayNotificationMessage(int count) {

        // Notification的Intent，即点击后转向的Activity
        Intent notificationIntent1 = new Intent(this, this.getClass());
        notificationIntent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent1 = PendingIntent.getActivity(this, 0,
                notificationIntent1, 0);

        // 创建Notifcation
        Notification notification = new Notification(R.drawable.logo,
                "tickerText1", System.currentTimeMillis());// 设定Notification出现时的声音，一般不建议自定义
        if (isFirstStart || TestVersionUpdateActivity.loading_process > 97) {
            notification.defaults |= Notification.DEFAULT_SOUND;// 设定是否振动
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        // 创建RemoteViews用在Notification中
        RemoteViews contentView = new RemoteViews(getPackageName(),
                R.layout.notification_version);
        contentView.setTextViewText(R.id.n_title, "升级提示");
        contentView.setTextViewText(R.id.n_text, "当前进度：" + count + "% ");
        contentView.setProgressBar(R.id.n_progress, 100, count, false);

        notification.contentView = contentView;
        notification.contentIntent = contentIntent1;

        notificationMrg.notify(0, notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        isFirstStart = true;
        notificationMrg = (NotificationManager) this
                .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        mHandler.handleMessage(new Message());
    }
}
