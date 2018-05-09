package com.jikexueyuan.tulingdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.iliker.application.CustomApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.iliker.mall.R;

import static com.fjl.widget.ToastFactory.getMyToast;

public class OnServcieActivity extends Activity implements HttpGetDataListener,
        OnClickListener {

    private List<ListData> lists;
    private EditText sendtext;
    private TextAdapter adapter;
    private CustomApplication cap;
    private double oldTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onservice);
        cap = (CustomApplication) getApplication();
        initView();
    }

    private void initView() {
        sendtext = (EditText) findViewById(R.id.sendText);
        findViewById(R.id.send_btn).setOnClickListener(this);
        lists = new ArrayList<>();
        ListData listData = new ListData(getRandomWelcomeTips(), ListData.RECEIVER, getTime(), "");
        lists.add(listData);
        adapter = new TextAdapter(this, lists);
        ((ListView) findViewById(R.id.lv)).setAdapter(adapter);
    }

    //随机问候短语
    private String getRandomWelcomeTips() {
        String welcome_tip;
        String[] welcome_array = this.getResources()
                .getStringArray(R.array.welcome_tips);
        if (!cap.networkIsAvailable()) {
            welcome_tip = welcome_array[0];
        } else {
            int index = (int) (Math.random() * (welcome_array.length - 1));
            welcome_tip = welcome_array[index];
        }
        return welcome_tip;
    }

    @Override
    public void getDataUrl(String data) {
        // System.out.println(data);
        parseText(data);
    }

    private void parseText(String str) {
        try {
            JSONObject jb = new JSONObject(str);
            ListData listData = new ListData(jb.getString("text"), ListData.RECEIVER, getTime(), "");
            lists.add(listData);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
        }
    }

    @Override
    public void onClick(View v) {
        if (!cap.networkIsAvailable()) {
            getMyToast("当前无网络").show();
            return;
        }
        getTime();
        String content_str = sendtext.getText().toString();
        sendtext.setText("");
        String dropk = content_str.replace(" ", "");
        String droph = dropk.replace("\n", "");
        ListData listData;
        listData = new ListData(content_str, ListData.SEND, getTime(), "");
        lists.add(listData);
        adapter.notifyDataSetChanged();
        new HttpData("http://www.tuling123.com/openapi/api?key=cd97322803cce08ada857e84bd5fc100&info="
                        + droph, this).execute();
    }

    private String getTime() {
        double currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.CHINA);
        Date curDate = new Date();
        String str = format.format(curDate);
        if (currentTime - oldTime >= 500) {
            oldTime = currentTime;
            return str;
        } else {
            return "";
        }

    }
}
