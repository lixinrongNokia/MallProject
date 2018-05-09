package cn.iliker.mall.alipush;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import iliker.mall.StartActivity;
import iliker.utils.GeneralUtil;

import java.util.Date;
import java.util.Map;

import static iliker.utils.CheckUtil.isBackground;

/**
 * 用于接收推送的通知和消息
 */
public class MyMessageReceiver extends MessageReceiver {

    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";
    private Uri uri = Uri.parse("content://cn.iliker.mall.aliPushMsg/add");

    /**
     * 推送通知的回调方法
     */
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        //  处理推送通知
        if (null != extraMap) {
            if (extraMap.containsKey("unPackOrderID")) {
                context.getSharedPreferences("unPackPush", Context.MODE_PRIVATE).edit().putString("tagOrderId", extraMap.get("tagOrderId")).putString("unPackOrderID", extraMap.get("unPackOrderID")).putString("storeEmail", extraMap.get("storeEmail")).putString("storeName", extraMap.get("storeName")).putString("storePic",extraMap.get("storePic")).apply();
            }
        } else {
            Log.i(REC_TAG, "@收到通知 && 自定义消息为空");
        }
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.i(REC_TAG, "onNotificationReceivedInApp ： " + " : " + title + " : " + summary + "  " + extraMap + " : " + openType + " : " + openActivity + " : " + openUrl);
    }

    /**
     * 推送消息的回调方法
     */
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        try {
            // 持久化推送的消息到数据库
            String data = cPushMessage.getContent();
            JSONObject object = JSON.parseObject(data);
            ContentValues values = new ContentValues();
            values.put("messageId", cPushMessage.getMessageId());
            values.put("receiver", object.getString("receiver"));
            values.put("createTime", GeneralUtil.SDF.format(new Date()));
            values.put("messageTitle", object.getString("messageTitle"));
            values.put("messageContent", object.getString("messageContent"));
            if (object.containsKey("targetURL"))
                values.put("targetURL", object.getString("targetURL"));
            if (object.containsKey("targetActivity"))
                values.put("targetActivity", object.getString("targetActivity"));
            if (object.containsKey("unPackOrderID"))
                values.put("unPackOrderID", object.getString("unPackOrderID"));
            context.getContentResolver().insert(uri, values);
        } catch (Exception e) {
            Log.i(REC_TAG, e.toString());
        }
    }

    /**
     * 从通知栏打开通知的扩展处理
     */
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.d(REC_TAG, summary);
    }


    @Override
    public void onNotificationRemoved(Context context, String messageId) {
        Log.i(REC_TAG, "onNotificationRemoved ： " + messageId);
    }


    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG, "onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
    }
}