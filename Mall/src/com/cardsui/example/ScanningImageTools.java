package com.cardsui.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanningImageTools {

    private final static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /**
     * 解析选择图片的二维码
     *
     * @param path 传入本地图片路径
     */
    public static void scanningImage(final String path,
                                     final IZCodeCallBack back) {
        if (TextUtils.isEmpty(path)) {
            back.ZCodeCallBackUi(null);
            return;

        }
        cachedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                // DecodeHintType 和EncodeHintType
                Result result = null;
                Hashtable<DecodeHintType, String> hints = new Hashtable<>();
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 先获取原大小
                options.inJustDecodeBounds = false; // 获取新的大小

                int sampleSize = (int) (options.outHeight / (float) 200);

                if (sampleSize <= 0)
                    sampleSize = 1;
                options.inSampleSize = sampleSize;
                Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);

                // int[] intArray = new
                // int[scanBitmap.getWidth()*scanBitmap.getHeight()];
                // //copy pixel data from the Bitmap into the 'intArray' array
                // scanBitmap.getPixels(intArray, 0, scanBitmap.getWidth(), 0,
                // 0, scanBitmap.getWidth(), scanBitmap.getHeight());

                RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(
                        source));
                QRCodeReader reader = new QRCodeReader();
                try {
                    result = reader.decode(bitmap1, hints);
                } catch (com.google.zxing.ReaderException e) {
                    e.printStackTrace();
                } finally {
                    back.ZCodeCallBackUi(result);
                }
            }
        });
    }

    /**
     * 解析选择图片的二维码
     *
     * @param bmap 传入bitmap
     */
    public static void scanningImage(final Bitmap bmap,
                                     final IZCodeCallBack back) {
        if (bmap == null) {
            back.ZCodeCallBackUi(null);
            return;

        }
        cachedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                // DecodeHintType 和EncodeHintType
                Result result = null;
                Hashtable<DecodeHintType, String> hints = new Hashtable<>();
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
                RGBLuminanceSource source = new RGBLuminanceSource(bmap);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(
                        source));
                QRCodeReader reader = new QRCodeReader();
                try {
                    result = reader.decode(bitmap1, hints);
                } catch (com.google.zxing.ReaderException e) {
                    e.printStackTrace();
                } finally {
                    back.ZCodeCallBackUi(result);
                }
            }
        });
    }


    /**
     * 调用recode(result.toString) 方法进行中文乱码处理
     */
    public static String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }

    public interface IZCodeCallBack {

         void ZCodeCallBackUi(Result result);
    }
}
