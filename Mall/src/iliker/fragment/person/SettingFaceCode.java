package iliker.fragment.person;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.iliker.mall.R;
import com.fjl.widget.ToastFactory;
import com.iflytek.cloud.*;
import com.iliker.application.CustomApplication;
import iliker.db.DatabaseService;
import iliker.utils.FaceUtil;
import iliker.utils.GeneralUtil;
import iliker.utils.HttpHelp;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static iliker.utils.FaceUtil.REQUEST_CROP_IMAGE;
import static iliker.utils.FaceUtil.getImagePath;
import static iliker.utils.HttpHelp.getHttpUtils;

public class SettingFaceCode extends Activity implements OnClickListener {
    private byte[] mImageData = null;
    private String gid;
    // 进度对话框
    private ProgressDialog mProDialog;
    // 拍照得到的照片文件
    private File mPictureFile;
    // FaceRequest对象，集成了人脸识别的各种功能
    private FaceRequest mFaceRequest;
    private CustomApplication cap;
    private DatabaseService ds;
    private Bitmap mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingface);
        cap = (CustomApplication) getApplication();
        findViewById(R.id.wfc_reg).setOnClickListener(this);
        findViewById(R.id.wfc_camera).setOnClickListener(this);
        ds = new DatabaseService(this);
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

    private void register(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret == 0 && "success".equals(obj.get("rst"))) {
            gid = obj.getString("gid");
            RequestParams params = new RequestParams(GeneralUtil.UPDATEFACESVC);
            params.addBodyParameter("nickname", cap.getUname());
            params.addBodyParameter("gid", gid);
            getHttpUtils().post(params,
                    new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            if ("1".equals(result)) {
                                int i = (int) ds.updateGid(cap.getUserinfo().getuID(), gid);
                                if (i > 0) {
                                    ToastFactory.getMyToast("录入成功").show();
                                    SettingFaceCode.this.finish();
                                }
                            } else
                                ToastFactory.getMyToast("录入失败").show();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {

                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
        } else {
            ToastFactory.getMyToast("录入失败").show();
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
                if ("reg".equals(type)) {
                    register(object);
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
                        ToastFactory.getMyToast("你的资料已经录入").show();
                        break;

                    default:
                        ToastFactory.getMyToast(error.getPlainDescription(true)).show();
                        break;
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wfc_reg:
                if (null != mImageData) {
                    mProDialog.setMessage("录入中...");
                    mProDialog.show();
                    // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
                    // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
                    mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
                    mFaceRequest.setParameter("property", "del");
                    mFaceRequest.sendRequest(mImageData, mRequestListener);
                } else {
                    ToastFactory.getMyToast("请选择图片后再录入").show();
                }
                break;
            case R.id.wfc_camera:
                int asPermission = ContextCompat.checkSelfPermission(SettingFaceCode.this, android.Manifest.permission.CAMERA);
                if (asPermission == PackageManager.PERMISSION_GRANTED) {
                    mPictureFile = new File(CustomApplication.cacheDir,
                            "picture" + System.currentTimeMillis() / 1000 + ".jpg");
                    // 设置相机拍照后照片保存路径
                    /*try {
                        mPictureFile = new File(CustomApplication.cacheDir,
                                "picture" + System.currentTimeMillis() / 1000 + ".jpg");
                        // 启动拍照,并保存到临时文件
                        Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                HttpHelp.getUriForFile(this, mPictureFile));
                        mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                        startActivityForResult(mIntent, FaceUtil.REQUEST_CAMERA_IMAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Android7.0以上URI
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //添加这一句表示对目标应用临时授权该Uri所代表的文件
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //通过FileProvider创建一个content类型的Uri
                        Uri uri = FileProvider.getUriForFile(this, getPackageName()+".provider", mPictureFile);
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
        if (resultCode == RESULT_OK) {
            String fileSrc;
            if (requestCode == FaceUtil.REQUEST_CAMERA_IMAGE) {
                if (null == mPictureFile) {
                    ToastFactory.getMyToast("拍照失败，请重试").show();
                    return;
                }
                fileSrc = mPictureFile.getAbsolutePath();
                updateGallery(fileSrc);
                // 跳转到图片裁剪页面
                FaceUtil.cropPicture(this,fileSrc);
//            cropPicture(this,fileSrc);
            } else if (requestCode == REQUEST_CROP_IMAGE) {
                // 获取返回数据
                Bitmap bmp = data.getParcelableExtra("data");
                // 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
                if (null != bmp) {
                    FaceUtil.saveBitmapToFile(SettingFaceCode.this, bmp);
                }
                // 获取图片保存路径
                fileSrc = getImagePath(SettingFaceCode.this);
                // 获取图片的宽和高
                Options options = new Options();
                options.inJustDecodeBounds = true;

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

    }

    /**
     * 调用系统剪裁功能
     */
   /* public void cropPicture(Activity activity, String path)
    {
        File file = new File(path);
        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();
        }
        Uri imageUri;
        Uri outputUri;

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(activity, getPackageName()+".provider", file);
            outputUri = Uri.fromFile(new File(getImagePath(activity.getApplicationContext())));
            //TODO:outputUri不需要ContentUri,否则失败
            //outputUri = FileProvider.getUriForFile(activity, getPackageName()+".provider", new File(crop_image));
        } else
        {
            imageUri = Uri.fromFile(file);
            outputUri = Uri.fromFile(new File(getImagePath(activity.getApplicationContext())));
        }
        intent.setDataAndType(imageUri, "image*//*");
        intent.putExtra("crop", "true");
        //设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, REQUEST_CROP_IMAGE);
    }*/

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
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(uri);
                        SettingFaceCode.this.sendBroadcast(mediaScanIntent);
                    }
                });
    }
}
