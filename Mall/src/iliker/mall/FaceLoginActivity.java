package iliker.mall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fjl.widget.ToastFactory;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iliker.application.CustomApplication;

import iliker.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;

import static com.iliker.application.CustomApplication.openIMLogin;
import static iliker.utils.HttpHelp.getHttpUtils;

public class FaceLoginActivity extends Activity implements OnClickListener {
    private byte[] mImageData = null;
    // authid为6-18个字符长度，用于唯一标识用户
    private Toast mToast;
    // 进度对话框
    private ProgressDialog mProDialog;
    // 拍照得到的照片文件
    private File mPictureFile;
    // FaceRequest对象，集成了人脸识别的各种功能
    private FaceRequest mFaceRequest;
    private UserInfo userinfo;

    private final MyHandler handler = new MyHandler(this);
    private Bitmap mImage;

    class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            FaceLoginActivity activity = (FaceLoginActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        int i = (int) DBHelper.getDB().saveUserInfo(userinfo);
                        if (i > 0) {
                            showTip("通过验证，欢迎回来！");
                            getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 1).apply();
                            Intent intent = new Intent(
                                    FaceLoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facereg_online);
        CustomApplication.actlist.add(this);
        // 在程序入口处传入appid，初始化SDK
        findViewById(R.id.wfc_verify)
                .setOnClickListener(FaceLoginActivity.this);
        findViewById(R.id.wfc_camera)
                .setOnClickListener(FaceLoginActivity.this);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mProDialog = new ProgressDialog(this);
        mProDialog.setCancelable(true);
        mProDialog.setTitle("请稍后");

        mProDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // cancel进度框时,取消正在进行的操作
                if (null != mFaceRequest) {
                    mFaceRequest.cancel();
                }
            }
        });

        mFaceRequest = new FaceRequest(this);
    }

    private void verify(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret == 0 && "success".equals(obj.get("rst"))) {
            if (obj.getBoolean("verf")) {
                String gid = obj.getString("gid");
                RequestParams params = new RequestParams(GeneralUtil.FACELOGIN);
                params.addBodyParameter("gid", gid);

                getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getBoolean("success")) {
                            userinfo = JSON.parseObject(jsonObject.getJSONObject("userInfo").toJSONString(), UserInfo.class);
                            openIMLogin(userinfo.getPhone(), userinfo.getPassword());
                            CustomApplication.customApplication.bindAccount(userinfo.getNickName());
                            handler.sendEmptyMessageDelayed(0, 800);
                        } else {
                            showTip("没有你的信息");
                        }
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
            } else {
                showTip("验证不通过");
            }
        } else {
            showTip("验证失败");
        }
    }

    private final RequestListener mRequestListener = new RequestListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            try {
                String result = new String(buffer, "utf-8");
                JSONObject object = new JSONObject(result);
                String type = object.optString("sst");
                if ("verify".equals(type)) {
                    verify(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }
            if (error != null) {
                switch (error.getErrorCode()) {
                    case ErrorCode.MSP_ERROR_ALREADY_EXIST:
                        showTip("对不起，你不是机主");
                        break;

                    default:
                        showTip(error.getPlainDescription(true));
                        break;
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wfc_verify:
                if (null != mImageData) {
                    mProDialog.setMessage("验证中...");
                    mProDialog.show();
                    // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
                    // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
                    // mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
                    mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
                    mFaceRequest.sendRequest(mImageData, mRequestListener);
                } else {
                    showTip("请选择图片后再验证");
                }
                break;
            case R.id.wfc_camera:
                // 设置相机拍照后照片保存路径
                int asPermission = ContextCompat.checkSelfPermission(FaceLoginActivity.this, android.Manifest.permission.CAMERA);
                if (asPermission == PackageManager.PERMISSION_GRANTED) {
                    mPictureFile = new File(Environment.getExternalStorageDirectory(),
                            "picture" + System.currentTimeMillis() / 1000 + ".jpg");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Android7.0以上URI
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //添加这一句表示对目标应用临时授权该Uri所代表的文件
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //通过FileProvider创建一个content类型的Uri
                        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", mPictureFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
                    }
                    startActivityForResult(intent, FaceUtil.REQUEST_CAMERA_IMAGE);
                } else ToastFactory.getMyToast("没有获得相机权限").show();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        String fileSrc;
        if (requestCode == FaceUtil.REQUEST_CAMERA_IMAGE) {
            if (null == mPictureFile) {
                ToastFactory.getMyToast("拍照失败，请重试").show();
                return;
            }
            fileSrc = mPictureFile.getAbsolutePath();
            updateGallery(fileSrc);
            // 跳转到图片裁剪页面
            FaceUtil.cropPicture(this, fileSrc);
        } else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
            // 获取返回数据
            Bitmap bmp = data.getParcelableExtra("data");
            // 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
            if (null != bmp) {
                FaceUtil.saveBitmapToFile(FaceLoginActivity.this, bmp);
            }
            // 获取图片保存路径
            fileSrc = FaceUtil.getImagePath(FaceLoginActivity.this);
            // 获取图片的宽和高
            Options options = new Options();
            options.inJustDecodeBounds = true;
            mImage = BitmapFactory.decodeFile(fileSrc, options);

            // 压缩图片
            options.inSampleSize = Math.max(1,
                    (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
            options.inJustDecodeBounds = false;
            mImage = BitmapFactory.decodeFile(fileSrc, options);

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

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 可根据流量及网络状况对图片进行压缩
            mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            mImageData = baos.toByteArray();

            ((ImageView) findViewById(R.id.img)).setImageBitmap(mImage);
        }
    }

    @Override
    public void finish() {
        if (null != mProDialog) {
            mProDialog.dismiss();
        }
        super.finish();
    }

    private void updateGallery(String filename) {
        MediaScannerConnection.scanFile(this, new String[]{filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
