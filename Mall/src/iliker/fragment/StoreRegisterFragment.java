package iliker.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.Store_MainActivity;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.StoreInfo;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import iliker.utils.ParsJsonUtil;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static iliker.utils.CheckUtil.isMobileNO;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

public class StoreRegisterFragment extends BaseFragment implements OnClickListener {
    private View view;
    private EditText email, password, storeName, address, addressDesc, contacts, tell;
    private Button submitBtn;
    private String tellVal;
    private String pwd;
    private String contactsVal;
    private String addrsDescVal;
    private String addsVal;
    private String storeVal;
    private Dialog dialog;
    private String emailAddress;
    private RecyclerView recyclerView;
    private static PhotoAdapter photoAdapter;
    private static ArrayList<String> selectedPhotos = new ArrayList<>();
    private Button addPIC;
    private String latitude;
    private String longitude;

    @Override
    protected View initSubclassView() {
        view = View.inflate(context, R.layout.store_register, null);
        findChlidViews();
        initData();
        setLitener();
        return view;
    }

    private void findChlidViews() {
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        addPIC = (Button) view.findViewById(R.id.addPIC);
        storeName = (EditText) view.findViewById(R.id.storeName);
        address = (EditText) view.findViewById(R.id.address);
        addressDesc = (EditText) view.findViewById(R.id.addressDesc);
        contacts = (EditText) view.findViewById(R.id.contacts);
        tell = (EditText) view.findViewById(R.id.tell);
        submitBtn = (Button) view.findViewById(R.id.submitBtn);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    private void initData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("locationinfo", Context.MODE_PRIVATE);
        String provinces = sharedPreferences.getString("provinces", "");
        String ncity = sharedPreferences.getString("ncity", "");
        String district = sharedPreferences.getString("district", "");
        String street = sharedPreferences.getString("street", "");
        latitude = sharedPreferences.getString("mylat", "");
        longitude = sharedPreferences.getString("mylong", "");
        address.setText(String.valueOf(provinces + ncity));
        addressDesc.setText(String.valueOf(district + street));
        dialog = DialogFactory.initDialog(context);
        photoAdapter = new PhotoAdapter(context, selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(5, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);
    }

    private void setLitener() {
        addPIC.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(context);
            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPIC:
                PhotoPicker.builder()
                        .setPhotoCount(5)
                        .setGridColumnCount(3)
                        .start(context);
                break;
            case R.id.submitBtn:
                if (regProperties()) {
                    dialog.show();
                    pushData();
                } else ToastFactory.getMyToast("你没有准备好所有的资料").show();
                break;
        }
    }

    private void pushData() {
        RequestParams params = new RequestParams(GeneralUtil.REGISTERSTORE);
        final String newPwd = MD5Util.getMD5Str(pwd);
        params.addBodyParameter("storeVal", storeVal);
        params.addBodyParameter("password", newPwd);
        params.addBodyParameter("addsVal", addsVal + addrsDescVal);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("contactsVal", contactsVal);
        params.addBodyParameter("tellVal", tellVal);
        params.addBodyParameter("emailAddress", emailAddress);
        for (int i = 0; i < selectedPhotos.size(); i++)
            params.addBodyParameter("storePIC" + i, new File(selectedPhotos.get(i)));
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);

                    if (jsonObject != null) {
                        int valID = jsonObject.getInt("valID");
                        String faceIcon = jsonObject.getString("faceIcon");
                        switch (valID) {
                            case -1:
                                ToastFactory.getMyToast("注册邮箱被使用").show();
                                break;
                            case 0:
                                ToastFactory.getMyToast("注册失败").show();
                                break;
                            default:
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                                long saveOk = getDB().saveStoreInfo(new StoreInfo(valID, emailAddress, newPwd, storeVal, faceIcon, tellVal, contactsVal,
                                        addsVal + addrsDescVal, 0, sdf.format(new Date())));
                                if (saveOk > 0) {
                                    context.getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 0).apply();
                                    startActivity(new Intent(context, Store_MainActivity.class));
                                    for (Activity activity : CustomApplication.actlist) {
                                        activity.finish();
                                    }
                                    context.finish();
                                    CustomApplication.actlist.clear();
                                }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                dialog.dismiss();
            }

            @Override
            public void onFinished() {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            if (data != null) {
                selectedPhotos.clear();
                selectedPhotos.addAll(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS));
                photoAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean regProperties() {
        storeVal = storeName.getText().toString().trim();
        pwd = password.getText().toString().trim();
        addsVal = address.getText().toString().trim();
        addrsDescVal = addressDesc.getText().toString().trim();
        contactsVal = contacts.getText().toString().trim();
        tellVal = tell.getText().toString().trim();
        emailAddress = email.getText().toString().trim();
        return !TextUtils.isEmpty(storeVal) && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(addsVal) && !TextUtils.isEmpty(addrsDescVal) && !TextUtils.isEmpty(contactsVal) && isMobileNO(tellVal) && ParsJsonUtil.isEmail(emailAddress) && !selectedPhotos.isEmpty();
    }
}
