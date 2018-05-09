package iliker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.WindowManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class GetBitMapUtil {
    /**
     * 压缩图片
     */
    @SuppressWarnings("deprecation")
    private static int getScare(Activity activity, String imageUrl) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imageUrl);
            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();

            if (200 == code) {
                InputStream is = response.getEntity().getContent();
                BitmapFactory.Options opts = new Options();
                // 不读取像素数组到内存中，仅读取图片的信息
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, opts);

                int imageHeight = opts.outHeight;
                int imageWidth = opts.outWidth;

                // 获取Android屏幕的服务
                WindowManager wm = (WindowManager) activity
                        .getSystemService(Context.WINDOW_SERVICE);
                // 获取屏幕的分辨率，getHeight()、getWidth已经被废弃掉了
                // 应该使用getSize()，但是这里为了向下兼容所以依然使用它们
                int windowHeight = wm.getDefaultDisplay().getHeight();
                int windowWidth = wm.getDefaultDisplay().getWidth();

                // 计算采样率
                int scaleX = imageWidth / windowWidth;
                int scaleY = imageHeight / windowHeight;
                int scale = 1;
                // 采样率依照最大的方向为准
                if (scaleX > scaleY && scaleY >= 1) {
                    scale = scaleX;
                }
                if (scaleX < scaleY && scaleX >= 1) {
                    scale = scaleY;
                }
                return scale;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;// 网络连接失败时默认返回1
    }

    @SuppressWarnings("deprecation")
    public static Bitmap getNetImage(Activity activity, String imageUrl) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imageUrl);
            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();

            if (200 == code) {
                InputStream is = response.getEntity().getContent();

                BitmapFactory.Options opts = new Options();

                // 根据计算出的比例进行缩放
                opts.inSampleSize = getScare(activity, imageUrl);

                return BitmapFactory.decodeStream(is, null, opts);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static Bitmap compression(Context context, String imgPath) {

        BitmapFactory.Options opts = new Options();
        // 不读取像素数组到内存中，仅读取图片的信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        // 从Options中获取图片的分辨率
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;

        // 获取Android屏幕的服务
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // 获取屏幕的分辨率，getHeight()、getWidth已经被废弃掉了
        // 应该使用getSize()，但是这里为了向下兼容所以依然使用它们
        int windowHeight = wm.getDefaultDisplay().getHeight();
        int windowWidth = wm.getDefaultDisplay().getWidth();

        // 计算采样率
        int scaleX = imageWidth / windowWidth;
        int scaleY = imageHeight / windowHeight;
        int scale = 1;
        // 采样率依照最大的方向为准
        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleX < scaleY && scaleX >= 1) {
            scale = scaleY;
        }

        // false表示读取图片像素数组到内存中，依照设定的采样率
        opts.inJustDecodeBounds = false;
        // 采样率
        opts.inSampleSize = scale;
        return BitmapFactory.decodeFile(imgPath, opts);

    }

    /**
     * 根据网络地址获取真实图片，并转换成位图图片
     */
    public static Bitmap getBitmap(String imgurl) {
        Bitmap bitmap;
        try {
            URL url = new URL(imgurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @SuppressWarnings("deprecation")
    public static Bitmap compression2(Context context, Resources res, int id) {

        BitmapFactory.Options opts = new Options();
        // 不读取像素数组到内存中，仅读取图片的信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, opts);
        // 从Options中获取图片的分辨率
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;

        // 获取Android屏幕的服务
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // 获取屏幕的分辨率，getHeight()、getWidth已经被废弃掉了
        // 应该使用getSize()，但是这里为了向下兼容所以依然使用它们
        int windowHeight = wm.getDefaultDisplay().getHeight();
        int windowWidth = wm.getDefaultDisplay().getWidth();

        // 计算采样率
        int scaleX = imageWidth / windowWidth;
        int scaleY = imageHeight / windowHeight;
        int scale = 1;
        // 采样率依照最大的方向为准
        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleX < scaleY && scaleX >= 1) {
            scale = scaleY;
        }

        // false表示读取图片像素数组到内存中，依照设定的采样率
        opts.inJustDecodeBounds = false;
        // 采样率
        opts.inSampleSize = scale;
        return BitmapFactory.decodeResource(res, id, opts);

    }

    @SuppressWarnings("deprecation")
    public static Bitmap compression(Activity activity, int id) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = activity.getResources().openRawResource(id);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
