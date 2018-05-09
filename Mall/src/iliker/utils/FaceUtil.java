package iliker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.*;
import java.util.regex.Pattern;

public class FaceUtil {
    public final static int REQUEST_PICTURE_CHOOSE = 1;
    public final static int REQUEST_CAMERA_IMAGE = 2;
    public final static int REQUEST_CROP_IMAGE = 3;

    /***
     * 裁剪图片
     *
     * @param activity Activity
     */
    public static void cropPicture(Activity activity, String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri;
        Uri outputUri;

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
            outputUri = Uri.fromFile(new File(getImagePath(activity.getApplicationContext())));
        } else {
            imageUri = Uri.fromFile(file);
            outputUri = Uri.fromFile(new File(getImagePath(activity.getApplicationContext())));
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        //设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, REQUEST_CROP_IMAGE);
    }

    /**
     * 保存裁剪的图片的路径
     *
     * @return
     */
    public static String getImagePath(Context context) {
        String path;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = context.getFilesDir().getAbsolutePath();
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/";
        }

        if (!path.endsWith("/")) {
            path += "/";
        }

        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        path += "ifd.jpg";
        return path;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree 旋转角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 原图
     * @return bitmap 旋转后的图片
     */
    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        // 图片旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 得到旋转后的图片
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String getImagePath(Context context, Intent data) {
        if ("file".equals(data.getData().getScheme())) {
            // 有些低版本机型返回的Uri模式为file
            return data.getData().getPath();
        } else {
            // Uri模型为content
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(data.getData(), proj, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.close();
            return cursor.getString(idx);
        }
    }

    /**
     * 在指定画布上将人脸框出来
     *
     * @param canvas      给定的画布
     * @param face        需要绘制的人脸信息
     * @param width       原图宽
     * @param height      原图高
     * @param frontCamera 是否为前置摄像头，如为前置摄像头需左右对称
     * @param DrawOriRect 可绘制原始框，也可以只画四个角
     */
    static public void drawFaceRect(Canvas canvas, FaceRect face, int width, int height, boolean frontCamera, boolean DrawOriRect) {
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 203, 15));
        int len = (face.bound.bottom - face.bound.top) / 8;
        if (len / 8 >= 2) paint.setStrokeWidth(len / 8);
        else paint.setStrokeWidth(2);

        Rect rect = face.bound;

        if (frontCamera) {
            int top = rect.top;
            rect.top = width - rect.bottom;
            rect.bottom = width - top;
        }

        if (DrawOriRect) {
            paint.setStyle(Style.STROKE);
            canvas.drawRect(rect, paint);
        } else {
            int drawl = rect.left - len;
            int drawr = rect.right + len;
            int drawu = rect.top - len;
            int drawd = rect.bottom + len;

            canvas.drawLine(drawl, drawd, drawl, drawd - len, paint);
            canvas.drawLine(drawl, drawd, drawl + len, drawd, paint);
            canvas.drawLine(drawr, drawd, drawr, drawd - len, paint);
            canvas.drawLine(drawr, drawd, drawr - len, drawd, paint);
            canvas.drawLine(drawl, drawu, drawl, drawu + len, paint);
            canvas.drawLine(drawl, drawu, drawl + len, drawu, paint);
            canvas.drawLine(drawr, drawu, drawr, drawu + len, paint);
            canvas.drawLine(drawr, drawu, drawr - len, drawu, paint);
        }

        if (face.point != null) {
            for (Point p : face.point) {
                if (frontCamera) {
                    p.y = width - p.y;
                }
                canvas.drawPoint(p.x, p.y, paint);
            }
        }
    }

    /**
     * 将矩形随原图顺时针旋转90度
     *
     * @param r      待旋转的矩形
     * @param width  输入矩形对应的原图宽
     * @param height 输入矩形对应的原图高
     * @return 旋转后的矩形
     */
    static public Rect RotateDeg90(Rect r, int width, int height) {
        int left = r.left;
        r.left = height - r.bottom;
        r.bottom = r.right;
        r.right = height - r.top;
        r.top = left;
        return r;
    }

    /**
     * 将点随原图顺时针旋转90度
     *
     * @param p      待旋转的点
     * @param width  输入点对应的原图宽
     * @param height 输入点对应的原图宽
     * @return 旋转后的点
     */
    static public Point RotateDeg90(Point p, int width, int height) {
        int x = p.x;
        p.x = height - p.y;
        p.y = x;
        return p;
    }

    public static int getNumCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 保存Bitmap至本地
     */
    public static void saveBitmapToFile(Context context, Bitmap bmp) {
        String file_path = getImagePath(context);
        File file = new File(file_path);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
