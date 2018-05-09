package iliker.fragment.person;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import iliker.db.DatabaseService;
import iliker.entity.UserInfo;
import iliker.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.FaceUtil.getImagePath;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 修改用户信息
 *
 * @author Administrator
 */
public class PersonSpace extends Activity implements OnClickListener {
    private ImageView changehead;
    private File sdcardTempFile;// 获取头像
    private Button cheangeData;// 提交修改按钮
    private Button birthdayVal;
    private Button sex, settingFaceCode;// 性别
    private EditText editxinqing, emailaddress, modifySuper;// 心情签名
    private final Calendar calendar = Calendar.getInstance();// 日期对象
    private final String[] str = new String[]{"女", "男"};
    private CustomApplication cap;
    private File imgpath;
    private String loctionImgPath;
    private String sexval;
    private String superiornum = "";
    private String emailAdds = "";
    private String birthday;// 出生日期
    private String signature = "";
    private UserInfo user;
    private JSONObject jsonObject = new JSONObject();
    private File tmp;
    private Switch st_onBindWX;
    private Dialog dialog;
    private final String GETWXCODESUCCESSFUL = "com.tencent.mm.sdk.openapi.authorization_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edituserdata);
        cap = (CustomApplication) getApplication();
        user = cap.getUserinfo();
        sdcardTempFile = CustomApplication.cacheDir;
        getSuperStatus();
        findViews();
        setListener();
        dialog = DialogFactory.initDialog(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(GETWXCODESUCCESSFUL);
        registerReceiver(mBatteryReceiver, filter);
    }

    private void getSuperStatus() {
        RequestParams params = new RequestParams(GeneralUtil.SUPERSTATUS);
        params.addBodyParameter("phone", user.getPhone());
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                int status = Integer.parseInt(s);
                if (status == 0 || TextUtils.isEmpty(user.getSuperiornum())) modifySuper.setEnabled(true);
                else modifySuper.setEnabled(false);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    private void findViews() {
        cheangeData = (Button) findViewById(R.id.cheangeData);
        birthdayVal = (Button) findViewById(R.id.birthdayVal);
        settingFaceCode = (Button) findViewById(R.id.settingFaceCode);
        sex = (Button) findViewById(R.id.sex);
        editxinqing = (EditText) findViewById(R.id.editxinqing);
        modifySuper = (EditText) findViewById(R.id.modifySuper);
        changehead = (ImageView) findViewById(R.id.changehead);
        emailaddress = (EditText) findViewById(R.id.emailaddress);
        st_onBindWX = (Switch) findViewById(R.id.onBindWX);
    }

    private void initData() {
        loctionImgPath = sdcardTempFile.getAbsolutePath() + "/" + user.getHeadImg();
        sexval = user.getSex();
        sex.setText(sexval);
        birthday = user.getBirthday();
        birthdayVal.setText(birthday);
        superiornum = user.getSuperiornum();
        modifySuper.setText(superiornum);
        signature = user.getSignature();
        editxinqing.setText(signature);
        emailAdds = user.getEmail();
        emailaddress.setText(emailAdds);
        if (user.isOnbind()) {
            st_onBindWX.setChecked(true);
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(loctionImgPath));
            changehead.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            ImageOptions imageOptions = new ImageOptions.Builder().setFailureDrawableId(R.drawable.ic_public_mr_headpicture).build();
            getBitmapUtils().bind(changehead, GeneralUtil.SHAREPATH + user.getHeadImg(), imageOptions);
        }
        settingFaceCode.setText(TextUtils.isEmpty(user.getGid()) ? "等你录入" : "已经录入");
        st_onBindWX.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (st_onBindWX.isChecked()) {
                    //绑定微信
                    IWXAPI api = WXAPIFactory.createWXAPI(PersonSpace.this, "wx82a6291f4ce0547e");
                    if (api.isWXAppInstalled()) {
                        boolean b = api.registerApp("wx82a6291f4ce0547e");
                        if (b) {
                            dialog.show();
                            SendAuth.Req req = new SendAuth.Req();
                            req.scope = "snsapi_userinfo";
                            req.state = "wechat_sdk_app_login";
                            api.sendReq(req);
                        }
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://weixin.qq.com")));
                    }
                } else {
                    //解除绑定
                    new AlertDialog.Builder(PersonSpace.this)
                            .setTitle("确认")
                            .setMessage("确认要解除绑定吗?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    unBindWX();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    st_onBindWX.setChecked(true);
                                }
                            }).setCancelable(false)
                            .show();
                }
            }
        });
    }

    /*取消绑定微信*/
    private void unBindWX() {
        RequestParams params = new RequestParams(GeneralUtil.UNBINDWX);
        params.addBodyParameter("uid", user.getuID() + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBooleanValue("success")) {
                    DBHelper.getDB().updateBind(user.getuID(), false);
                    ToastFactory.getMyToast("已解除绑定").show();
                    customApplication.resetUser();
                    user = cap.getUserinfo();
                    st_onBindWX.setChecked(false);
                } else {
                    st_onBindWX.setChecked(true);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                st_onBindWX.setChecked(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GETWXCODESUCCESSFUL.equals(intent.getAction())) {
                if (intent.getBooleanExtra("success", false)) {
                    getServerProperty(intent.getStringExtra("code"));
                } else {
                    dialog.dismiss();
                    ToastFactory.getMyToast(intent.getStringExtra("msg")).show();
                }
            }
        }
    };

    private void getServerProperty(String code) {
        RequestParams params = new RequestParams(GeneralUtil.EXISTSAPPBINDWX);
        params.addBodyParameter("code", code);
        params.addBodyParameter("device", "app");
        params.addBodyParameter("uid", user.getuID() + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBooleanValue("success")) {
                    DBHelper.getDB().updateBind(user.getuID(), true);
                    ToastFactory.getMyToast("绑定成功").show();
                    customApplication.resetUser();
                    user = cap.getUserinfo();
                    st_onBindWX.setChecked(true);
                } else {
                    st_onBindWX.setChecked(false);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                st_onBindWX.setChecked(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dialog.dismiss();
            }
        });
    }

    private void setListener() {
        cheangeData.setOnClickListener(this);
        changehead.setOnClickListener(this);
        birthdayVal.setOnClickListener(this);
        settingFaceCode.setOnClickListener(this);
        sex.setOnClickListener(this);
        modifySuper.setOnClickListener(this);
    }

    /**
     * 更新用户资料对数据库修改
     */
    @Override
    public void onClick(View v) {
        if (!cap.networkIsAvailable()) {
            Toast.makeText(this, "当前没网络", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.cheangeData:
                try {
                    regData();
                    if (jsonObject.length() > 0 || imgpath != null) {
                        RequestParams params = new RequestParams(GeneralUtil.UPDATEUSERSVT);
                        if (imgpath != null) {
                            params.addBodyParameter("headimg", imgpath);
                        }
                        if (jsonObject.length() > 0)
                            params.addBodyParameter("perprotity", jsonObject.toString());
                        params.addBodyParameter("phone", user.getPhone());
                        getHttpUtils().post(params,
                                new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        jsonObject = null;
                                        if (!"0".equals(result)) {
                                            user.setHeadImg(result);
                                            new Thread() {
                                                public void run() {
                                                    long isok = new DatabaseService(
                                                            PersonSpace.this)
                                                            .updateUserInfo(user);
                                                    if (isok > 0) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(
                                                                        getApplicationContext(),
                                                                        "修改成功！",
                                                                        Toast.LENGTH_SHORT)
                                                                        .show();
                                                                PersonSpace.this.finish();
                                                            }
                                                        });
                                                    }
                                                }
                                            }.start();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {

                                    }

                                    @Override
                                    public void onFinished() {
                                    }

                                });
                    } else this.finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.changehead:
                ShowPickDialog();
                break;
            case R.id.birthdayVal:
                setDialog().show();
                break;
            case R.id.settingFaceCode:
                Intent intent = new Intent(this, SettingFaceCode.class);
                startActivity(intent);
                break;

            case R.id.sex:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("性别选择:");
                int selectindex = 0;
                for (int i = 0; i < str.length; i++) {
                    if (str[i].equals(sexval)) {
                        selectindex = i;
                        break;
                    }
                }
                builder.setSingleChoiceItems(str, selectindex, new
                        DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                sexval = str[arg1];
                                sex.setText(sexval);
                                arg0.dismiss();
                            }

                        }).show();
                break;

        }
    }

    private void regData() throws JSONException {
        emailAdds = emailaddress.getText().toString().trim();
        signature = editxinqing.getText().toString().trim();
        if (!birthday.equals(user.getBirthday())) {
            jsonObject.put("birthday", birthday);
            user.setBirthday(birthday);
        }

        superiornum = modifySuper.getText().toString().trim();
        if (!TextUtils.isEmpty(superiornum) && CheckUtil.isMobileNO2(superiornum) && !superiornum.equals(user.getSuperiornum()) && !superiornum.equals(user.getPhone())) {
            jsonObject.put("superiornum", superiornum);
            user.setSuperiornum(superiornum);
        }

        if (!sexval.equals(user.getSex())) {
            jsonObject.put("sex", sexval);
            user.setSex(sexval);
        }

        if (!emailAdds.equals(user.getEmail())) {
            if (!TextUtils.isEmpty(emailAdds) && ParsJsonUtil.isEmail(emailAdds)) {
                jsonObject.put("emailAddr", emailAdds);
                user.setEmail(emailAdds);
            } else {
                jsonObject.put("emailAddr", "");
                user.setEmail("");
            }
        }

        if (!signature.equals(user.getSignature())) {
            if (!TextUtils.isEmpty(signature)) {
                jsonObject.put("signature", signature);
                user.setSignature(signature);
            } else {
                jsonObject.put("signature", "");
                user.setSignature("");
            }
        }
    }

    // 日期对话框
    @SuppressLint("SimpleDateFormat")
    private Dialog setDialog() {
        return new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        birthday = new SimpleDateFormat("yyyy-MM-dd")
                                .format(calendar.getTime());
                        birthdayVal.setText(birthday);
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 选择提示对话框
     */
    private void ShowPickDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像...")
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
                        int asPermission = ContextCompat.checkSelfPermission(PersonSpace.this, android.Manifest.permission.CAMERA);
                        if (asPermission == PackageManager.PERMISSION_GRANTED) {
                            tmp = new File(sdcardTempFile.getAbsolutePath(), "user_img.png");
                            Intent intent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            // 下面这句指定调用相机拍照后的照片存储的路径
                            /*intent.putExtra(MediaStore.EXTRA_OUTPUT, HttpHelp.getUriForFile(PersonSpace.this, new File(sdcardTempFile.getAbsolutePath(), "user_img.png")));
                            startActivityForResult(intent, 2);*/
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                //通过FileProvider创建一个content类型的Uri
                                Uri uri = FileProvider.getUriForFile(PersonSpace.this, getPackageName() + ".provider", tmp);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            } else {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmp));
                            }
                            startActivityForResult(intent, FaceUtil.REQUEST_CAMERA_IMAGE);
                        } else {
                            ToastFactory.getMyToast("没有获得相机权限").show();
                        }
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case 1:
                    String path = null;
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    }
                    FaceUtil.cropPicture(this, path);
                    break;
                // 如果是调用相机拍照时
                case 2:

                    /*File temp = new File(sdcardTempFile.getAbsolutePath(), "user_img.png");
                    startPhotoZoom(Uri.fromFile(temp));*/
                    FaceUtil.cropPicture(this, tmp.getAbsolutePath());

                    break;
                // 取得裁剪后的图片
                case 3:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
                default:
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     */
    /*private void startPhotoZoom(Uri uri) {
        imgpath = new File(loctionImgPath);
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
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, 3);
    }*/

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Intent picdata) {
        Bitmap bmp = picdata.getParcelableExtra("data");
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
        imgpath = new File(fileSrc);
        Bitmap mImage = BitmapFactory.decodeFile(fileSrc, options);

        // 若mImageBitmap为空则图片信息不能正常获取
        if (null == mImage) {
            ToastFactory.getMyToast("图片信息无法正常获取！").show();
            return;
        }

        // 部分手机会对图片做旋转，这里检测旋转角度
        int degree = FaceUtil.readPictureDegree(fileSrc);
        if (degree != 0) {
            // 把图片旋转为正的方向
            mImage = FaceUtil.rotateImage(degree, mImage);
        }
        changehead.setImageBitmap(mImage);
    }
}
