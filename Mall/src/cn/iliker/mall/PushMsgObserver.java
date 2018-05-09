package cn.iliker.mall;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;

import static iliker.utils.DBHelper.getDB;

public class PushMsgObserver extends ContentObserver {
    private Handler handler;
    private String receiver;
    private int messageCode;

    public PushMsgObserver(Handler handler, String receiver, int messageCode) {
        super(handler);
        this.handler = handler;
        this.receiver = receiver;
        this.messageCode = messageCode;
    }

    @Override
    public void onChange(boolean selfChange) {
        Message message = new Message();
        message.what = messageCode;
        message.arg1 = getDB().getUnReadCount(new String[]{receiver});
        handler.sendMessage(message);
    }

}
