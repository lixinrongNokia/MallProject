package cn.iliker.mall;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cn.iliker.mall.alipush.MessageEntity;
import iliker.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.Date;

public class MessagePanelAtc extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private ListView msgLv;
    private Uri uri = Uri.parse("content://cn.iliker.mall.aliPushMsg/getUnMsg");
    private Uri delUri = Uri.parse("content://cn.iliker.mall.aliPushMsg/del");
    private Uri modifyUri = Uri.parse("content://cn.iliker.mall.aliPushMsg/update");
    private String receiver;
    private ArrayList<MessageEntity> list;
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sysmsg_layout);
        receiver = getIntent().getStringExtra("receiver");
        msgLv = (ListView) findViewById(R.id.msgLv);
        msgLv.setOnItemLongClickListener(this);
        msgLv.setOnItemClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (!TextUtils.isEmpty(receiver)) {
            Cursor cursor = getContentResolver().query(uri, null, null, new String[]{receiver}, null);
            list = new ArrayList<>();
            while (cursor.moveToNext()) {
                MessageEntity entity = new MessageEntity(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                list.add(entity);
            }
            cursor.close();
            if (!list.isEmpty()) {
                messagesAdapter = new MessagesAdapter(list, this);
                msgLv.setAdapter(messagesAdapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final MessageEntity entity = list.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("操作提醒");
        builder.setMessage("你要删除这条信息吗？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(delUri, null, new String[]{entity.getId() + ""});
                list.remove(position);
                messagesAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("取消", null).create().show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageEntity entity = list.get(position);
        if (!TextUtils.isEmpty(entity.getTargetActivity())) {
            ContentValues values = new ContentValues();
            values.put("modifyTime", GeneralUtil.SDF.format(new Date()));
            getContentResolver().update(modifyUri, values, "id=?", new String[]{entity.getId() + ""});
            try {
                Intent intent = new Intent(this, Class.forName(entity.getTargetActivity()));
                intent.putExtra("unPackOrderID", entity.getUnPackOrderID());
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                Log.d("error", e.getMessage());
            }
        }
    }
}
