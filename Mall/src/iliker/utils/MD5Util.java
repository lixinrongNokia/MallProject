package iliker.utils;

import android.annotation.SuppressLint;

import java.security.MessageDigest;

/**
 * MD5加密工具对用户密码加密从数据库查找用户只能做字符串对比
 *
 * @author Administrator
 */
@SuppressLint("DefaultLocale")
public final class MD5Util {
    public static String getMD5Str(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));


            byte[] byteArray = messageDigest.digest();

            StringBuilder md5StrBuff = new StringBuilder();

            for (byte b : byteArray) {
                if (Integer.toHexString(0xFF & b).length() == 1)
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF & b));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & b));
            }
            // 16位加密，从第9位到25位
            return md5StrBuff.substring(8, 24).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
