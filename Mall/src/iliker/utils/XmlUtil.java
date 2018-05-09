package iliker.utils;

import android.content.Context;
import android.util.Xml;
import com.iliker.application.CustomApplication;
import iliker.entity.StoreInfo;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XmlUtil {

    public static void Object2XML(List<StoreInfo> list) {
        File file = new File(CustomApplication.cacheDir, "stores.xml");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            // 获取xml序列化器
            XmlSerializer xs = Xml.newSerializer();
            xs.setOutput(fos, "utf-8");
            //生成xml头
            xs.startDocument("utf-8", true);
            //添加xml根节点
            xs.startTag(null, "stores");
            for (StoreInfo store : list) {
                xs.startTag(null, "store");
                xs.attribute(null, "id", String.valueOf(store.getId()));

                xs.startTag(null, "storeName");
                xs.text(store.getStoreName());
                xs.endTag(null, "storeName");

                xs.startTag(null, "faceIcon");
                xs.text(String.valueOf(store.getFaceIcon()));
                xs.endTag(null, "faceIcon");

                xs.startTag(null, "latitude");
                xs.text(String.valueOf(store.getLatitude()));
                xs.endTag(null, "latitude");

                xs.startTag(null, "longitude");
                xs.text(String.valueOf(store.getLongitude()));
                xs.endTag(null, "longitude");


                xs.startTag(null, "tell");
                xs.text(store.getTell());
                xs.endTag(null, "tell");

                xs.startTag(null, "contacts");
                xs.text(store.getContacts());
                xs.endTag(null, "contacts");

                xs.startTag(null, "address");
                xs.text(store.getAddress());
                xs.endTag(null, "address");

                xs.startTag(null, "regTime");
                xs.text(store.getRegTime());
                xs.endTag(null, "regTime");

                xs.startTag(null, "distance");
                xs.text(String.valueOf(store.getDistance()));
                xs.endTag(null, "distance");

                xs.endTag(null, "store");
            }
            xs.endTag(null, "stores");
            //生成xml
            xs.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<StoreInfo> xml2Object() {
        List<StoreInfo> stores = null;
        StoreInfo store = null;
        XmlPullParser parser = Xml.newPullParser();
        File file = new File(CustomApplication.cacheDir, "stores.xml");
        try {
            InputStream inputStream = new FileInputStream(file);
            parser.setInput(inputStream, "UTF-8");
            int event = parser.getEventType();//产生第一个事件
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT://判断当前事件是否是文档开始事件
                        stores = new ArrayList<>();//初始化stores集合
                        break;
                    case XmlPullParser.START_TAG://判断当前事件是否是标签元素开始事件
                        if ("store".equals(parser.getName())) {//判断开始标签元素是否是store
                            store = new StoreInfo();
                            store.setId(Integer.parseInt(parser.getAttributeValue(0)));//得到store标签的属性值，并设置store的id
                        }
                        if (store != null) {
                            if ("storeName".equals(parser.getName())) {
                                store.setStoreName(parser.nextText());
                            }
                            if ("faceIcon".equals(parser.getName())) {
                                store.setFaceIcon(parser.nextText());
                            }
                            if ("latitude".equals(parser.getName())) {
                                store.setLatitude(Double.parseDouble(parser.nextText()));
                            }
                            if ("longitude".equals(parser.getName())) {
                                store.setLongitude(Double.parseDouble(parser.nextText()));
                            }
                            if ("tell".equals(parser.getName())) {
                                store.setTell(parser.nextText());
                            }
                            if ("contacts".equals(parser.getName())) {
                                store.setContacts(parser.nextText());
                            }
                            if ("address".equals(parser.getName())) {
                                store.setAddress(parser.nextText());
                            }
                            if ("regTime".equals(parser.getName())) {
                                store.setRegTime(parser.nextText());
                            }
                            if ("distance".equals(parser.getName())) {
                                store.setDistance(Double.parseDouble(parser.nextText()));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG://判断当前事件是否是标签元素结束事件
                        if ("store".equals(parser.getName())) {//判断结束标签元素是否是store
                            if (stores != null)
                                stores.add(store);//将store添加到books集合
                            store = null;
                        }
                        break;
                }
                event = parser.next();//进入下一个元素并触发相应事件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stores;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                int readLength = is.read(buffer);
                if (readLength == -1) break;
                arrayOutputStream.write(buffer, 0, readLength);
            }
            is.close();
            arrayOutputStream.close();
            content = new String(arrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    public static boolean isDecimal(String str) {
        boolean flag = false;
        Pattern pattern = Pattern.compile("\\d+(.\\d+)$");
        // 字符串不为空,不排除0,如果把0除外,后面不要加?,用"\\d+(.\\d+)$"就可以了;
        if (str.length() > 0) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                flag = true;
                // 多于两个字符时,小数最多以一个0开头;
                if (str.length() > 1) {
                    if ((str.charAt(0) == '0') && (str.charAt(1) == '0')) {
                        flag = false;
                    }
                }
            }
        }
        return flag;
    }
}
