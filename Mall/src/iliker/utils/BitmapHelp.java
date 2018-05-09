package iliker.utils;

import android.widget.ImageView;

import org.xutils.ImageManager;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Author: wyouflf
 * Date: 13-11-12
 * Time: 上午10:24
 */
public final class BitmapHelp {
    private BitmapHelp() {
    }

    private final static ImageOptions imageOptions = new ImageOptions.Builder()
            // 加载中或错误图片的ScaleType
            .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
            // 默认自动适应大小
            //.setSize(...)
            .setIgnoreGif(true)
            // 如果使用本地文件url, 添加这个设置可以在本地文件更新后刷新立即生效.
            //.setUseMemCache(false)
            .setImageScaleType(ImageView.ScaleType.CENTER).build();


    private static ImageManager bitmapUtils;

    /**
     * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
     */
    public static ImageManager getBitmapUtils() {
        if (bitmapUtils == null) {
            bitmapUtils = x.image();
        }
        return bitmapUtils;
    }

    public static ImageOptions getImageOptions() {
        return imageOptions;
    }
}
