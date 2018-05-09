package iliker.utils;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import iliker.entity.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 专门解析json的工具
 *
 * @author Administrator
 */
@SuppressWarnings("deprecation")
public final class ParsJsonUtil {
    public static List<Goods> parseJSON(String responseString) {
        return JSON.parseArray(responseString, Goods.class);
    }


    /**
     * 获取share分享表的JSON数据
     */
    public static List<Share> getShares(String responseString) {
        List<Share> list = null;

        if (responseString != null) {
            list = JSON.parseArray(responseString, Share.class);
        }
        return list;
    }

    public static List<Map<String, String>> getComments(String shareid,
                                                        HttpDataUtilCallback httpdatautilcallback) {
        List<Map<String, String>> list = null;
        List<NameValuePair> pairs = new ArrayList<>();// 存放请求参数
        pairs.add(new BasicNameValuePair("shareid", shareid));
        HttpClient client = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,
                    "utf-8");
            HttpPost httpPost = new HttpPost(GeneralUtil.GETCOMM);
            httpPost.setEntity(entity);
            // 第2步：使用execute方法发送HTTP POST请求，并返回HttpResponse对象
            client = new DefaultHttpClient();
            // 请求超时
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    5000);
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                list = new ArrayList<>();
                HttpEntity httpEntity = response.getEntity();
                byte[] data = ReadStreamUtil
                        .readStream(httpEntity.getContent());
                String str = new String(data);
                Map<String, String> map;
                JSONArray jsonarray = new JSONArray(str);
                int arraylen = jsonarray.length();// json数组里的对象总个数
                for (int i = 0; i < arraylen; i++) {
                    JSONObject jo = jsonarray.getJSONObject(i);
                    map = new LinkedHashMap<>();
                    map.put("id", jo.getString("id"));
                    map.put("nickname", jo.getString("nickname"));
                    map.put("headImg", jo.getString("headImg"));
                    map.put("commText", jo.getString("commText"));
                    map.put("commAudioPath", jo.getString("commAudioPath"));
                    map.put("commTime", jo.getString("commTime"));
                    map.put("audioLen", jo.getString("audioLen"));
                    list.add(map);
                }
            }
        } catch (IOException e) {
            httpdatautilcallback.httpRequestTimeOutCallback();
        } catch (Exception e) {
        } finally {
            if (client != null) {
                client.getConnectionManager().closeExpiredConnections();
            }
        }
        return list;
    }

    /*public static byte[] AsyncDownLoad(String path) {
        byte[] in = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                in = ReadStreamUtil.readStream(conn.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return in;
    }*/

    public static List<Map<String, String>> getFolle(String status,
                                                     String nickname) {
        List<Map<String, String>> list = null;
        DefaultHttpClient client = null;
        List<NameValuePair> pairs = new ArrayList<>();// 存放请求参数
        try {
            pairs.add(new BasicNameValuePair("status", status));
            pairs.add(new BasicNameValuePair("nickname", nickname));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,
                    "utf-8");
            HttpPost httpPost = new HttpPost(GeneralUtil.GETFOLLWER);
            httpPost.setEntity(entity);
            client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                list = new ArrayList<>();
                HttpEntity httpEntity = response.getEntity();
                byte[] data = ReadStreamUtil
                        .readStream(httpEntity.getContent());
                String str = new String(data);
                Map<String, String> map;
                JSONArray jsonarray = new JSONArray(str);
                int arraylen = jsonarray.length();// json数组里的对象总个数
                for (int i = 0; i < arraylen; i++) {
                    JSONObject jo = jsonarray.getJSONObject(i);
                    map = new LinkedHashMap<>();
                    map.put("fid", jo.getString("fid"));
                    map.put("uid", jo.getString("uid"));
                    map.put("nickname", jo.getString("nickname"));
                    map.put("headimg", jo.getString("headimg"));
                    map.put("signature", jo.getString("signature"));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (client != null) {
                client.getConnectionManager().closeExpiredConnections();
            }
        }
        return list;
    }

    /*public static List<FlashSale> parsFlashSaleJSON(String responseString) {
        List<FlashSale> items;
        *//*{"pageCount":1,"totalSize":2,"dataSet":[{"illustrations":null,"number":50,"STATUS":true,"goodName":"fghrf","price":15.0,"imgpath":"32aabcba000000c0.png#32aabcba000001c0.png","discount":0.9,"id":178,"goodCode":"qreter33","sales":0,"goodsDesc":"343dgdd"},{"illustrations":null,"number":50,"STATUS":true,"goodName":"fghrf","price":15.0,"imgpath":"32aabcba000000c0.png#32aabcba000001c0.png","discount":0.9,"id":178,"goodCode":"qreter33","sales":0,"goodsDesc":"343dgdd"}]}*//*
        try {
            JSON.parseObject(responseString);
            JSONArray jsonArray = new JSONArray(responseString);// 获得json数组
            items = new ArrayList<>();
            int arraylen = jsonArray.length();// json数组里的对象总个数
            if (arraylen > 0) {
                for (int i = 0; i < arraylen; i++) {
                    FlashSale fs = new FlashSale();
                    Goods good = new Goods();
                    JSONArray jsonArray1 = jsonArray.getJSONArray(i);// 取出第I个对象
                    fs.setId(jsonArray1.getInt(0));// 获得第i个code
                    good.setId(jsonArray1.getInt(1));
                    good.setGoodCode(jsonArray1.getString(2));// 获得第i个title
                    good.setGoodName(jsonArray1.getString(3));// 获得第i个price
                    good.setGoodsDesc(jsonArray1.getString(4));
                    good.setPrice(jsonArray1.getDouble(5));
                    good.setImgpath(jsonArray1.getString(6));
                    good.setIllustrations(jsonArray1.getString(7));
                    good.setSales(jsonArray1.getInt(8));
                    fs.setNumber(jsonArray1.getInt(9));
                    fs.setSTATUS(jsonArray1.getInt(10));
                    fs.setDiscount(jsonArray1.getDouble(11));
                    fs.setGoods(good);
                    items.add(fs);
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return items;
    }*/


   /* public static List<Integer> parsBrandJSONID(String responseString) {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        try {
            JSONArray jsonArray = new JSONArray(responseString);
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                list.add(jsonArray1.getInt(0));
            }
        } catch (JSONException e) {
        }
        return list;
    }*/

    public static List<Partners> parsPartnersJSON(String responseString) {
        return JSON.parseArray(responseString, Partners.class);
    }

    /*public static List<String> parsBrandNameJSON(String responseString) {
        List<String> list = new ArrayList<>();
        list.add("全部");
        try {
            JSONArray jsonArray = new JSONArray(responseString);
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                list.add(jsonArray1.getString(1));
            }
        } catch (JSONException e) {
        }
        return list;
    }*/

    /*public static SerializableList parsOrderToGoods(String result) {
        if (!TextUtils.isEmpty(result)) {
            List<Cartitem> list = JSON.parseArray(result, Cartitem.class);
            SerializableList serializableList = new SerializableList();
            serializableList.setList(list);
            return serializableList;
        }
        return null;
    }*/

    public interface HttpDataUtilCallback {
        void httpRequestTimeOutCallback();
    }

    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /*public static List<ShipAddress> parseAddress(String responseString) {
        List<ShipAddress> saddses = null;
        try {
            saddses = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ShipAddress sds = new ShipAddress();
                sds.setId(jsonObject.getInt("id"));
                sds.setConsigneeName(jsonObject.getString("consigneeName"));
                sds.setPhone(jsonObject.getString("phone"));
                sds.setAddress(jsonObject.getString("address"));
                saddses.add(sds);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return saddses;
    }*/

}
