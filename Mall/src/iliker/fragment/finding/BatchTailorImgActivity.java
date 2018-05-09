package iliker.fragment.finding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.dpizarro.uipicker.library.picker.PickerUI;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iflytek.cloud.FaceDetector;
import com.iliker.application.CustomApplication;
import iliker.entity.Social;
import iliker.entity.UserInfo;
import iliker.fragment.finding.city.CityPicker;
import iliker.utils.FaceRect;
import iliker.utils.FaceUtil;
import iliker.utils.GeneralUtil;
import iliker.utils.ParseResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.*;

import static iliker.utils.FaceUtil.getImagePath;
import static iliker.utils.GetBitMapUtil.getNetImage;
import static iliker.utils.HttpHelp.getHttpUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BatchTailorImgActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private GridView gridView;// 显示所有上传图片
    private Bitmap addNewPic;
    private ArrayList<Bitmap> microBmList = new ArrayList<>();
    private AddImageGridAdapter imgAdapter;
    private final ArrayList<String> photoList = new ArrayList<>();
    private String loctionImgPath;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss_SS", Locale.CHINA);
    private File mCurrentPhotoFile;// 照相机拍照得到的图片
    private File shareDir;
    private PickerUI emotionalPic;
    private PickerUI professionalPic;
    private int currentPosition = -1;
    private int jobPosition = -1;
    private LinearLayout emotionalstatus;
    private LinearLayout professionalOP;
    private LinearLayout hometownlayout;
    private TextView emotional;
    private TextView job;
    private PopupWindow mCityPop;
    //显示选择城市的 实时数据
    private TextView sheng_Text;
    private TextView shi_Text;
    private TextView hometown;
    private String mSheng, mShi;
    private String city_all = "";
    private TextView imgcount;
    private final Myhandler myhandler = new Myhandler(this);
    private UserInfo userInfo;
    private String phone;

    private String emotionalVal;
    private String jobVal;
    private String hometownVal;
    private Dialog dialog;
    private AlertDialog grid_Itemdialog;
    private Button setindex;
    private Button del_item;
    private Social social;
    private String indeximg;
    private String[] oldImgpath;
    private static final int REQUEST_CODE_ASK_MUTI_PERMISSIONS = 222;//请求多个权限
    private final String[] MUTIPERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    // FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
    private FaceDetector mFaceDetector;

    @SuppressLint("HandlerLeak")
    private class Myhandler extends Handler {
        final WeakReference<Context> weakReference;

        Myhandler(Context context) {
            this.weakReference = new WeakReference<>(context);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void handleMessage(Message msg) {
            BatchTailorImgActivity imgActivity = (BatchTailorImgActivity) weakReference.get();
            if (imgActivity != null) {
                switch (msg.what) {
                    case 1:
                        sheng_Text.setText(mSheng);
                        shi_Text.setText(mShi);
                        break;
                    case 2:
                        if (detectFace(microBmList.get(0))) {
                            ToastFactory.getMyToast(Html.fromHtml("<font size=\"3\" color=\"red\">魅力值</font><font size=\"2\" color=\"green\">+2</font>").toString()).show();
                        } else
                            ToastFactory.getMyToast(Html.fromHtml("<font size=\"3\" color=\"green\">魅力值</font><font size=\"2\" color=\"red\">-2</font>").toString()).show();
                        imgActivity.finish();
                        break;
                    case 3:
                        if (!microBmList.isEmpty()) {
                            imgAdapter.notifyDataSetChanged();
                            imgcount.setText(String.valueOf(photoList.size() + "/16"));
                            dialog.dismiss();
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.batch_tailorimg_layout);
        CustomApplication customApplication = (CustomApplication) getApplication();
        userInfo = customApplication.getUserinfo();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setSubtitle("编辑资料");
        }
        addNewPic = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.add_new_pic);
        mFaceDetector = FaceDetector.createDetector(this, null);
        selectionCityPOP(R.layout.select_city_pop_main_layout);
        findViews();
        createPath();
        initviews();
        getSocialProper(userInfo.getuID());
        setListeners();
    }

    private void getSocialProper(int uID) {
        dialog.show();
        RequestParams params = new RequestParams(GeneralUtil.GETSOCIALPROPER);
        params.addBodyParameter("uid", uID + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"0".equals(result)) {
                    social = JSON.parseObject(result, Social.class);
                    emotional.setText(social.getEmotionalState());
                    job.setText(social.getJob());
                    hometown.setText(social.getHometown());
                    String photoAlbum = social.getPhotoAlbum();
                    if (!TextUtils.isEmpty(photoAlbum)) {
                        if (photoAlbum.contains("#")) {
                            oldImgpath = photoAlbum.split("#");
                        } else {
                            oldImgpath = new String[]{photoAlbum};
                        }
                        parseBitmap(BatchTailorImgActivity.this, oldImgpath);
                    } else {
                        oldImgpath = new String[]{};
                        dialog.dismiss();
                    }
                } else
                    oldImgpath = new String[]{};
                dialog.dismiss();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseBitmap(final Activity activity, final String[] strings) {
        indeximg = strings[0];
        microBmList.remove(addNewPic);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String imgPath : strings) {
                    photoList.add(imgPath);
                    microBmList.add(getNetImage(activity, GeneralUtil.HEADURL + imgPath));
                }
                microBmList.add(addNewPic);
                myhandler.sendEmptyMessage(3);
            }
        }).start();
    }

    private void createPath() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            shareDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "Android/data/" + this.getPackageName() + "/share/");
        }
        if (!shareDir.exists()) {
            boolean mkdirs = shareDir.mkdirs();
            if (!mkdirs) shareDir = this.getFilesDir();// 获取系统文件目录
        }
    }

    private void initviews() {
        dialog = DialogFactory.initDialog(this);
        phone = userInfo.getPhone();
        phone = phone.substring(7, phone.length());
        List<String> emotionaloptions = Arrays.asList(getResources().getStringArray(R.array.countries_array));
        List<String> professionaloptions = Arrays.asList(getResources().getStringArray(R.array.professional));
        professionalPic.setItems(this, professionaloptions);
        emotionalPic.setItems(this, emotionaloptions);
        microBmList.add(addNewPic);
        imgAdapter = new AddImageGridAdapter(BatchTailorImgActivity.this, microBmList);
        gridView.setAdapter(imgAdapter);
    }

    private void findViews() {
        gridView = (GridView) findViewById(R.id.allPic);
        emotionalPic = (PickerUI) findViewById(R.id.emotionalPic);
        professionalPic = (PickerUI) findViewById(R.id.professionalPic);
        emotionalstatus = (LinearLayout) findViewById(R.id.emotionalstatus);
        professionalOP = (LinearLayout) findViewById(R.id.professionalOP);
        hometownlayout = (LinearLayout) findViewById(R.id.hometownlayout);
        emotional = (TextView) findViewById(R.id.emotional);
        hometown = (TextView) findViewById(R.id.hometown);
        imgcount = (TextView) findViewById(R.id.imgcount);
        job = (TextView) findViewById(R.id.job);
    }

    private void setListeners() {
        emotionalstatus.setOnClickListener(this);
        professionalOP.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
        emotionalPic.setOnClickItemPickerUIListener(
                new PickerUI.PickerUIItemClickListener() {

                    @Override
                    public void onItemClickPickerUI(int which, int position, String valueResult) {
                        currentPosition = position;
                        emotionalVal = valueResult;
                        emotional.setText(emotionalVal);
                    }
                });
        professionalPic.setOnClickItemPickerUIListener(new PickerUI.PickerUIItemClickListener() {
            @Override
            public void onItemClickPickerUI(int which, int position, String valueResult) {
                jobPosition = position;
                jobVal = valueResult;
                job.setText(jobVal);
            }
        });
        hometownlayout.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case 1:
                    if (data != null) {
                        String path = null;
                        Uri uri = data.getData();
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        }
                        FaceUtil.cropPicture(this, path);
                    }
                    break;
                // 如果是调用相机拍照时
                case 2:
                   /* File temp = new File(CustomApplication.cacheDir, "tobe_cut.png");
                    startPhotoZoom(Uri.fromFile(temp));*/
                    FaceUtil.cropPicture(this, mCurrentPhotoFile.getAbsolutePath());
                    break;
                // 取得裁剪后的图片
                case 3:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, Menu.NONE, 1, "提交");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        Drawable drawable = getResources().getDrawable(R.drawable.checkbox_click_with_selected);
        item.setIcon(drawable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                showDialog();
                break;
            case 0:
                JSONObject jsonProperty = new JSONObject();
                try {
                    if (!TextUtils.isEmpty(emotionalVal)) {
                        jsonProperty.put("emotionalState", emotionalVal);
                    }
                    if (!TextUtils.isEmpty(jobVal)) {
                        jsonProperty.put("job", jobVal);
                    }
                    if (!TextUtils.isEmpty(hometownVal)) {
                        jsonProperty.put("hometown", hometownVal);
                    }

                    int size = photoList.size();
                    String wantdel = null;
                    if (oldImgpath.length > 0) {
                        wantdel = toCompare(oldImgpath, photoList.toArray(new String[size]));
                    }
                    if (jsonProperty.length() != 0 || !TextUtils.isEmpty(wantdel) || (photoList.size() > oldImgpath.length) || (oldImgpath.length > 0 && !indeximg.equals(oldImgpath[0]))) {
                        jsonProperty.put("uid", userInfo.getuID());
                        if (!photoList.isEmpty()) {
                            StringBuilder builder = new StringBuilder();
                            for (String s : photoList) {
                                builder.append(s);
                                builder.append("#");
                            }
                            builder.deleteCharAt(builder.length() - 1);
                            jsonProperty.put("photoAlbum", builder.toString());
                        } else {
                            jsonProperty.put("photoAlbum", "");
                        }
                        dialog.show();
                        getSharedPreferences("updateSocial", Context.MODE_PRIVATE).edit().putBoolean("isupdate", true).apply();
                        updateData(wantdel, jsonProperty.toString());
                    } else {
                        finish();
                    }

                } catch (JSONException e) {
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String toCompare(String[] oldImgpath, String[] markname) {
        StringBuilder builder = new StringBuilder();

        for (String findName : oldImgpath) {
            boolean noequals = false;
            for (String imgname : markname) {
                if (findName.equals(imgname)) {
                    noequals = true;
                    break;
                }
            }
            if (!noequals) {
                builder.append(findName);
                builder.append("#");
            }
        }
        if (!TextUtils.isEmpty(builder.toString()))
            builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("交友资料可帮助你赚取积分，要放弃编辑吗？")
                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        BatchTailorImgActivity.this.finish();
                    }
                }).setPositiveButton("继续编辑", null).create().show();
    }

    private void updateData(String wantdel, String textProperty) {
        RequestParams params = new RequestParams(GeneralUtil.UPDATESOCIALPROPER);
        if (!TextUtils.isEmpty(wantdel))
            params.addBodyParameter("delheadimg", wantdel);
        params.addBodyParameter("textProperty", textProperty);
        if (!photoList.isEmpty()) {
            int len = photoList.size();
            for (int i = 0; i < len; i++) {
                File file = new File(shareDir, photoList.get(i));
                if (file.exists())
                    params.addBodyParameter("pic" + i, file);
            }
        }
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                dialog.dismiss();
                int code = Integer.parseInt(responseInfo);
                switch (code) {
                    case 0://error
                        ToastFactory.getMyToast("添加信息失败").show();
                        break;
                    case 1://success
                        myhandler.sendEmptyMessage(2);
                        break;
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
                ToastFactory.getMyToast("错误").show();
            }

            @Override
            public void onCancelled(CancelledException e) {
                ToastFactory.getMyToast("取消").show();
            }

            @Override
            public void onFinished() {
                ToastFactory.getMyToast("完成没成功").show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (professionalPic.isPanelShown() || emotionalPic.isPanelShown()) return;
        if (position == (photoList.size())) {
            requestMutiPermission();
        } else {

            if (grid_Itemdialog == null) {
                grid_Itemdialog = new AlertDialog.Builder(this).create();
                View view1 = View.inflate(this, R.layout.grid_item_alert, null);
                setindex = (Button) view1.findViewById(R.id.setindex);

                del_item = (Button) view1.findViewById(R.id.del_item);

                (view1.findViewById(R.id.closebtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        grid_Itemdialog.dismiss();
                    }
                });
                grid_Itemdialog.setView(view1);
            }
            if (position == 0)
                setindex.setEnabled(false);
            else if (photoList.size() > 1) setindex.setEnabled(true);
            setindex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp = photoList.get(position);
                    Bitmap bitmap = microBmList.get(position);
                    microBmList.remove(position);
                    microBmList.add(0, bitmap);
                    photoList.remove(position);
                    photoList.add(0, temp);
                    indeximg = temp;
                    imgAdapter.notifyDataSetChanged();
                    grid_Itemdialog.dismiss();
                }
            });
            del_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    photoList.remove(position);
                    microBmList.remove(position);
                    imgAdapter.notifyDataSetChanged();
                    imgcount.setText(String.valueOf(photoList.size() + "/16"));
                    grid_Itemdialog.dismiss();
                }
            });
            grid_Itemdialog.show();
        }
    }

    /**
     * 选择提示对话框
     */
    private void ShowPickDialog() {
        loctionImgPath = getPhotoFileName();
        new AlertDialog.Builder(this)
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        startActivityForResult(intent, 1);
                    }
                })
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        // 创建照片的存储目录
                        mCurrentPhotoFile = new File(CustomApplication.cacheDir, loctionImgPath);// 给新照的照片文件命名
//                        final Intent intent = getTakePickIntent(mCurrentPhotoFile);
                        // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        // 下面这句指定调用相机拍照后的照片存储的路径
                            /*intent.putExtra(MediaStore.EXTRA_OUTPUT, HttpHelp.getUriForFile(PersonSpace.this, new File(sdcardTempFile.getAbsolutePath(), "user_img.png")));
                            startActivityForResult(intent, 2);*/
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //添加这一句表示对目标应用临时授权该Uri所代表的文件
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            //通过FileProvider创建一个content类型的Uri
                            Uri uri = FileProvider.getUriForFile(BatchTailorImgActivity.this, getPackageName() + ".provider", mCurrentPhotoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        } else {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
                        }
                        startActivityForResult(intent, 2);
                    }
                }).show();
    }

   /* private Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, HttpHelp.getUriForFile(BatchTailorImgActivity.this, f));
        return intent;
    }*/

    /**
     * 裁剪图片方法实现
     */
    private void startPhotoZoom(Uri uri) {
        loctionImgPath = getPhotoFileName();
        File imgpath = new File(shareDir, loctionImgPath);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", Uri.fromFile((imgpath)));
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, 3);
    }

    private boolean detectFace(Bitmap bitmap) {
        if (mFaceDetector == null) {
            /**
             * 离线人脸检测功能需要单独下载支持离线人脸的SDK
             * 请开发者前往语音云官网下载对应SDK
             */
            return false;
        }

        // 启动图片人脸检测
        String result = mFaceDetector.detectARGB(bitmap);
        // 解析人脸结果
        FaceRect[] mFaces = ParseResult.parseResult(result);
        return null != mFaces && mFaces.length > 0;

    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Intent data) {
        microBmList.remove(addNewPic);
        Bitmap bmp = data.getParcelableExtra("data");
        // 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
        if (null != bmp) {
            FaceUtil.saveBitmapToFile(this, bmp);
        }
        // 获取图片保存路径
        String fileSrc = getImagePath(this);
        // 获取图片的宽和高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // 压缩图片
        options.inSampleSize = Math.max(1,
                (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
        options.inJustDecodeBounds = false;
        Bitmap mImage = BitmapFactory.decodeFile(fileSrc);
        String imgUrl = getImagePath(this);
        File file = new File(imgUrl);
        File newFile = new File(shareDir, loctionImgPath);
        file.renameTo(newFile);
        photoList.add(loctionImgPath);
        imgcount.setText(String.valueOf(photoList.size() + "/16"));
        microBmList.add(mImage);
        if (microBmList.size() < 16)
            microBmList.add(addNewPic);
        imgAdapter.notifyDataSetChanged();
    }

    private static Bitmap rotateImage(int angle, Bitmap bitmap) {
        // 图片旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 得到旋转后的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    private String getPhotoFileName() {
        return phone + sdf.format(new Date()) + ".png";
    }

    @Override
    protected void onDestroy() {
        mFaceDetector.destroy();
        delBitmap();
        new Thread() {
            @Override
            public void run() {
                File file = new File(shareDir.getAbsolutePath());
                File[] files = file.listFiles();
                int len = files.length;
                if (len > 0) {
                    for (File file1 : files) {
                        file1.delete();
                    }
                }
            }
        }.start();
        myhandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void delBitmap() {
        if (microBmList != null && !microBmList.isEmpty()) {
            for (Bitmap bitmap : microBmList) {
                if (!bitmap.isRecycled())
                    bitmap.recycle();
            }
            microBmList.clear();
            microBmList = null;
        }
        photoList.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emotionalstatus:
                if (professionalPic.isPanelShown() || mCityPop.isShowing()) return;
                if (currentPosition == -1) {
                    emotionalPic.slide();
                } else {
                    emotionalPic.slide(currentPosition);
                }
                break;
            case R.id.professionalOP:
                if (emotionalPic.isPanelShown() || mCityPop.isShowing()) return;
                if (jobPosition == -1) {
                    professionalPic.slide();
                } else {
                    professionalPic.slide(jobPosition);
                }
                break;
            case R.id.hometownlayout:
                if (professionalPic.isPanelShown() || emotionalPic.isPanelShown()) return;
                backgroundAlpha(0.3f);
                showSelectionCityPOP(gridView);
                break;
        }

    }

    private void showSelectionCityPOP(View view) {
        if (!mCityPop.isShowing()) {
            mCityPop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 选择城市 自定义控件
     */
    private void selectionCityPOP(int resId) {
        LayoutInflater mLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cityPopView = mLayoutInflater.inflate(resId, null);
        mCityPop = new PopupWindow(cityPopView, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        // 必须设置background才能消失
        mCityPop.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.fullscreen_share_bg));
        mCityPop.setOutsideTouchable(true);
        // 自定义动画
//			mCityPop.setAnimationStyle(R.style.tudou_encrypt_dialog);
        // 使用系统动画
        mCityPop.setAnimationStyle(R.style.mypopwindow_anim_style);

        CityPicker cityPicker = (CityPicker) cityPopView.findViewById(R.id.citypicker);

        sheng_Text = (TextView) cityPopView.findViewById(R.id.get_sheng);
        shi_Text = (TextView) cityPopView.findViewById(R.id.get_shi);
        TextView Select_Ok = (TextView) cityPopView.findViewById(R.id.Select_City_Ok);
        TextView Select_Cancel = (TextView) cityPopView.findViewById(R.id.Select_City_Cancel);

        sheng_Text.setText(cityPicker.getCity_string());
        shi_Text.setText("");

        cityPicker.setCity(new CityPicker.testCity() {
            @Override
            public void cityAll(String sheng, String shi) {
                mSheng = sheng;
                mShi = shi;
                myhandler.sendEmptyMessage(1);
            }
        });

        Select_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city_all = mSheng + "/" + mShi;
                if (TextUtils.isEmpty(mSheng)) {
                    hometownVal = sheng_Text.getText().toString();
                    hometown.setText(hometownVal);
                } else {
                    hometownVal = city_all;
                    hometown.setText(hometownVal);
                }
                mCityPop.dismiss();
            }
        });

        Select_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city_all = "";
                mCityPop.dismiss();
            }
        });
        // popWindow消失监听方法
        mCityPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        mCityPop.update();
        mCityPop.setTouchable(true);
        mCityPop.setFocusable(true);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
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
                ShowPickDialog();
            }
        } else {
            ShowPickDialog();
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
                        ShowPickDialog();
                    } else {//没有全部授权
                        Toast.makeText(BatchTailorImgActivity.this, "缺少部分权限", Toast.LENGTH_SHORT).show();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                            //需要引导用户手动开启的权限列表
                            ArrayList<String> needShow = new ArrayList<>();

                            //从没有授权的权限中判断是否需要引导用户
                            for (int i = 0; i < deniedPermissions.size(); i++) {
                                String permission = deniedPermissions.get(i);
                                if (!shouldShowRequestPermissionRationale(permission)) {// !false
                                    needShow.add(permission);
                                    Log.d("RuntimePermissionDemo", "needShow: " + permission);
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
}
