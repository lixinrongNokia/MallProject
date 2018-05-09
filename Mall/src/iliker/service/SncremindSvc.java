package iliker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.iliker.mall.R;
import iliker.fragment.home.BodyTestActivity;

public class SncremindSvc extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (("iliker.sncremind.userdata").equals(intent.getAction())) {
            // 获取NotificationManager的引用
            NotificationManager mNM = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            // 创建一个Notification对象
            int icon = R.drawable.logo;
            Intent notificationIntent = new Intent(context,
                    BodyTestActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);
            // 定义Notification的title、message、和pendingIntent
            CharSequence contentTitle = "爱内秀提醒";
            CharSequence contentText = "你有新的身型数据需要同步";
            Notification.Builder builder = new Notification.Builder(context)
                    .setAutoCancel(true)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(icon)
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true);
            Notification notification = builder.getNotification();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            // notification被notify的时候，触发默认声音和默认震动
            notification.defaults = Notification.DEFAULT_SOUND
                    | Notification.DEFAULT_VIBRATE
                    | Notification.DEFAULT_LIGHTS;
            // 通知状态栏显示Notification
            final int HELLO_ID = 1;
            mNM.notify(HELLO_ID, notification);
        }
    }

}
