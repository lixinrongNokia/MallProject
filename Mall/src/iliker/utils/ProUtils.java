package iliker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by WDHTC on 2016/3/25.
 */
public final class ProUtils {
    /**
     * 缓存json数据
     *
     * @param context        上下文
     * @param fileName       文件名
     * @param url            请求地址key
     * @param responseString 远程网络返回的数据 value
     */
    public static void cecheJson(Context context, String fileName, String url, String responseString) {
        SharedPreferences sf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        sf.edit().putString(url, responseString).apply();
    }

    /**
     * @param context  上下文
     * @param fileName 文件名
     * @param url      请求路径key
     * @return 本地缓存json
     */
    public static String getCecheJson(Context context, String fileName, String url) {
        SharedPreferences sf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sf.getString(url, null);
    }
}
