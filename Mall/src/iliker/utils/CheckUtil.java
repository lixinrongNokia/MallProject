package iliker.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查是否是手机或固话格式
 *
 * @author Administrator
 */
public final class CheckUtil {
    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;

        Pattern p = Pattern
                .compile("^(((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8})||(((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8})$");

        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    public static boolean isUrlLink(String url) {
        Pattern p = Pattern.compile("((http[s]?|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
        Matcher m = p.matcher(url);
        return m.matches();
    }

    //检查手机号格式
    public static boolean isMobileNO2(String mobiles) {
        Pattern p = Pattern.compile("^(13[0-9]|14[579]|15[0-3,5-9]|17[0135678]|18[0-9])\\d{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    /*匹配以字母开头的字母数字下划线组合汉字昵称*/
    public static boolean StringFilter(String str) {
        return str != null && str.matches("(^[a-zA-Z]\\w{3,9})|([\\u4E00-\\u9FA5]{2,10})");
    }

    /*匹配以字母开头的字母数字下划线组合6-16位密码*/
    public static boolean pwdFilter(String str) {
        return str != null && str.matches("^[a-zA-Z](?![a-zA-Z]+$)\\w{5,15}");
    }

    public static void setListViewHeight(GridView lv) {
        ListAdapter listAdapter = lv.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int rows;
        int columns = 0;
        int horizontalBorderHeight = 0;
        Class<?> clazz = lv.getClass();
        try {
            // 利用反射，取得每行显示的个数
            Field column = clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns = (Integer) column.get(lv);
            // 利用反射，取得横向分割线高度
            Field horizontalSpacing = clazz
                    .getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight = (Integer) horizontalSpacing.get(lv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
        if (listAdapter.getCount() % columns > 0) {
            rows = listAdapter.getCount() / columns + 1;
        } else {
            rows = listAdapter.getCount() / columns;
        }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
            View listItem = listAdapter.getView(i, null, lv);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + horizontalBorderHeight * (rows - 1);// 最后加上分割线总高度
        lv.setLayoutParams(params);
    }

    public static int isBackground(Context context, String activityName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = "cn.iliker.mall";
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            ComponentName topConponent = tasksInfo.get(0).topActivity;
            if (packageName.equals(topConponent.getPackageName())) {
                if (topConponent.getClassName().equals(activityName)) {
                    // 当前正在运行的是期望的Activity
                    // ShareDetailsActivity在运行");
                    return 2;
                }
                // 当前APP在前台运行
                return 1;
            } else {
                // 当前的APP在后台运行
                return 0;
            }
        }
        return 0;
    }
}
