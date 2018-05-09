package iliker.fragment.finding;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import id.zelory.compressor.Compressor;
import iliker.fragment.PhotoAdapter;
import iliker.fragment.RecyclerItemClickListener;
import iliker.utils.GeneralUtil;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.fjl.widget.DialogFactory.initDialog;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 用户上传分享多幅图片操作 我爱我秀
 *
 * @author Administrator
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class SelectImgActivity extends Activity implements OnClickListener {

    private int uid;
    private Button uploadSub;
    private EditText suosuo;
    private Dialog progressDialog;
    private static String path;
    private String district, street;
    private static final int REQUEST_CODE_ASK_MUTI_PERMISSIONS = 222;//请求多个权限
    private final String[] MUTIPERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    private PhotoAdapter photoAdapter;
    private RecyclerView recycler_view;
    private final static List<File> list = new ArrayList<>();
    private static ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectimg);
        SharedPreferences sf = getSharedPreferences("locationinfo",
                Context.MODE_PRIVATE);
        district = sf.getString("district", "");
        street = sf.getString("street", "");
        uid = CustomApplication.customApplication.getUserinfo().getuID();
        path = CustomApplication.cacheDir.getPath();
        uploadSub = (Button) findViewById(R.id.uploadSub);
        suosuo = (EditText) findViewById(R.id.suosuo);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        findViewById(R.id.addPIC).setOnClickListener(this);
        uploadSub.setOnClickListener(this);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recycler_view.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        recycler_view.setAdapter(photoAdapter);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(SelectImgActivity.this);
            }
        }));
    }


    private void requestMutiPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            //需要请求授权的权限
            ArrayList<String> needRequest = new ArrayList<>();

            //遍历 过滤已授权的权限，防止重复申请
            for (String permission : MUTIPERMISSIONS) {
                int check = checkSelfPermission(permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    needRequest.add(permission);
                }
            }
            //如果没有全部授权
            if (needRequest.size() > 0) {
                //请求权限，此方法异步执行，会弹出权限请求对话框，让用户授权，并回调 onRequestPermissionsResult 来告知授权结果
                requestPermissions(needRequest.toArray(new String[needRequest.size()]), REQUEST_CODE_ASK_MUTI_PERMISSIONS);

            } else {//已经全部授权过
                //做一些你想做的事情，即原来不需要动态授权时做的操作
                //TODO
            }
        } else {
            //TODO
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MUTI_PERMISSIONS://请求多个权限
                if (grantResults.length > 0) {
                    //被拒绝的权限列表
                    ArrayList<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permissions[i]);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {//已全部授权
                        //TODO
                    } else {//没有全部授权
                        Toast.makeText(SelectImgActivity.this, "缺少部分权限", Toast.LENGTH_SHORT).show();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                            //需要引导用户手动开启的权限列表
                            ArrayList<String> needShow = new ArrayList<>();

                            //从没有授权的权限中判断是否需要引导用户
                            for (int i = 0; i < deniedPermissions.size(); i++) {
                                String permission = deniedPermissions.get(i);
                                if (!shouldShowRequestPermissionRationale(permission)) {// !false
                                    needShow.add(permission);
                                }
                            }
                            //需要引导用户
                            if (needShow.size() > 0) {
                                //需要弹出自定义对话框，引导用户去应用的设置界面手动开启权限
                                showMissingPermissionDialog();
                            }
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 需要手动开启缺失的权限对话框
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用权限不足。\n\n可点击\"设置\"-\"权限\"-打开所需权限。\n\n最后点击两次后退按钮，即可返回。");
        builder.setNegativeButton("知道了", null);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置 来手动开启权限
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 处理其他页面返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            if (data != null) {
                selectedPhotos.clear();
                selectedPhotos.addAll(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS));
                photoAdapter.notifyDataSetChanged();
                uploadSub.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(!list.isEmpty()){
            for (File file : list) {
                file.delete();
            }
        }
        selectedPhotos.clear();
        photoAdapter.notifyDataSetChanged();
        list.clear();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadSub:
                String str = ((Button) v).getText().toString();
                switch (str) {
                    case "完成":
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        boolean isOpen = imm.isActive();
                        if (isOpen) {
                            View view = SelectImgActivity.this
                                    .getCurrentFocus();
                            if (view != null) {
                                imm.hideSoftInputFromWindow(view.getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }

                        Thread thread = new Thread(sendable);// .start();
                        thread.start();
                        try {
                            thread.join();
                            uploadSub.setText("上传");
                        } catch (InterruptedException e) {
                        }
                        break;
                    case "上传":
                        uploadSub.setText("上传中...");
                        uploadSub.setEnabled(false);
                        progressDialog = initDialog(this);
                        progressDialog.show();

                        RequestParams params = new RequestParams(GeneralUtil.UPLOADURL);
                        String suotext = suosuo.getText().toString();
                        if (TextUtils.isEmpty(suotext)) {
                            suotext = "这个家伙很懒，什么也没说！";
                        }
                        params.addBodyParameter("share.location", district + street);
                        params.addBodyParameter("share.userinfo.uid", uid + "");
                        params.addBodyParameter("share.content", suotext);

                        for (int i = 0; i < list.size(); i++) {
                            params.addBodyParameter("sharePortrait[" + i + "]", list.get(i));
                            params.addBodyParameter("sharePortraitFileName[" + i + "]", list.get(i).getName(),"image/webp");
                        }
                        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String responseString) {
                                progressDialog.dismiss();
                                JSONObject jsonObject = JSON.parseObject(responseString);
                                if (!jsonObject.isEmpty()) {
                                    if (jsonObject.getBooleanValue("success")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (File file : list) {
                                                    file.delete();
                                                }
                                                list.clear();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        uploadSub.setVisibility(View.VISIBLE);
                                                        uploadSub.setText("完成");
                                                        selectedPhotos.clear();
                                                        photoAdapter.notifyDataSetChanged();
                                                        uploadSub.setEnabled(true);
                                                        uploadSub.setVisibility(View.GONE);
                                                        suosuo.setText(null);
                                                    }
                                                });
                                            }
                                        }).start();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                SelectImgActivity.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("上传成功!");
                                        builder.setPositiveButton("去查看",
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface arg0,
                                                            int arg1) {
                                                        getSharedPreferences("urlfaxia", Context.MODE_PRIVATE).edit().putBoolean("isFaxia", true).apply();
                                                        SelectImgActivity.this.finish();
                                                    }

                                                })
                                                .setNegativeButton(
                                                        "继续上传",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface arg0,
                                                                    int arg1) {
                                                                arg0.dismiss();
                                                            }
                                                        }).show();
                                    } else {
                                        ToastFactory.getMyToast("上传失败").show();
                                        uploadSub.setVisibility(View.VISIBLE);
                                        uploadSub.setText("上传");
                                    }
                                } else {
                                    ToastFactory.getMyToast("上传的图片超过限制大小").show();
                                    uploadSub.setVisibility(View.VISIBLE);
                                    uploadSub.setText("上传");
                                }
                            }

                            @Override
                            public void onError(Throwable throwable, boolean b) {
                                progressDialog.dismiss();
                                uploadSub.setEnabled(true);
                                ToastFactory.getMyToast("上传失败").show();
                            }

                            @Override
                            public void onCancelled(CancelledException e) {

                            }

                            @Override
                            public void onFinished() {
                            }
                        });
                        break;
                }
                break;
            case R.id.addPIC:
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(3)
                        .start(this);
                break;
        }
    }

    private final Runnable sendable = new Runnable() {
        @Override
        public void run() {
            getSdcardFile();
        }
    };

    private static String getPhotoFileName() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss_SS", Locale.CHINA).format(new Date());
        return name + ".webp";
    }

    private void getSdcardFile() {
        for (String imgPath : selectedPhotos) {
            File file = new Compressor.Builder(this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToFile(new File(imgPath));
            FileOutputStream b = null;
            String fileName = path + "/" + getPhotoFileName();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (b != null) {
                        b.flush();
                        b.close();
                    }
                } catch (IOException e) {
                }
            }
            list.add(new File(fileName));
        }
    }
}
