package iliker.mall;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static iliker.utils.HttpHelp.getHttpUtils;

public class UnPackConfirmAcitivity extends Activity implements View.OnClickListener {
    private Dialog dialog;
    private SharedPreferences sharedPreferences;
    private Button bConfirmBtn, bCloseBtn;
    private String storeName;
    private String img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unpackconfirm_layout);
        dialog = DialogFactory.initDialog(this);
        sharedPreferences = getSharedPreferences("unPackPush", Context.MODE_PRIVATE);
        storeName = sharedPreferences.getString("storeName", "");
        img = sharedPreferences.getString("storePic", "");
        findViews();
        setListener();
    }

    private void findViews() {
        TextView tStoreName = (TextView) findViewById(R.id.storeName);
        tStoreName.setText(storeName);
        bConfirmBtn = (Button) findViewById(R.id.confirmBtn);
        bCloseBtn = (Button) findViewById(R.id.closeBtn);
        ImageView storePic = (ImageView) findViewById(R.id.storePic);
        BitmapHelp.getBitmapUtils().bind(storePic, GeneralUtil.STOREICON + img);
    }

    private void setListener() {
        bConfirmBtn.setOnClickListener(this);
        bCloseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn:
                String unPackOrderID = sharedPreferences.getString("unPackOrderID", "");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(CustomApplication.cacheDir.getAbsolutePath(), "unPackOrderID.txt"));
                    fileOutputStream.write(unPackOrderID.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String storeEmail = sharedPreferences.getString("storeEmail", "");
                if (!TextUtils.isEmpty(unPackOrderID) && !TextUtils.isEmpty(storeEmail)) {
                    dialog.show();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("unPackOrderID", unPackOrderID);
                    jsonObject.put("nickName", CustomApplication.customApplication.getUserinfo().getNickName());
                    jsonObject.put("storeEmail", storeEmail);
                    RequestParams params = new RequestParams(GeneralUtil.USERUNPACKCONFIRM);
                    params.addBodyParameter("unPackOrderInfo", jsonObject.toJSONString());
                    getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            dialog.dismiss();
                            if ("success".equals(s)) {
                                ToastFactory.getMyToast("完成提货流程").show();
                                startActivity(new Intent(UnPackConfirmAcitivity.this,MainActivity.class));
                                finish();
                            } else ToastFactory.getMyToast("系统异常").show();
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            dialog.dismiss();
                            ToastFactory.getMyToast("操作未完成").show();
                        }

                        @Override
                        public void onCancelled(CancelledException e) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }
                break;
            case R.id.closeBtn:
                finish();
                break;
        }
    }
}
