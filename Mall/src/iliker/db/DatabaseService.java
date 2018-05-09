package iliker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.iliker.mall.alipush.MessageEntity;
import iliker.entity.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库方法封装，创建表，删除表，数据（增删改查）
 *
 * @author Administrator
 */
public class DatabaseService {
    private static DBOpenHelper dbOpenHelper;

    public DatabaseService(Context context) {
        if (dbOpenHelper == null) {
            dbOpenHelper = new DBOpenHelper(context);
        }
    }

    public void dropTable(String taleName) {
        dbOpenHelper.getWritableDatabase().execSQL(
                "DROP TABLE IF EXISTS " + taleName);

    }

    public void closeDatabase() {
        if (dbOpenHelper != null) {
            dbOpenHelper.getWritableDatabase().close();
        }

    }

    /**
     * 本地保存用户信息
     */
    public int register(UserInfo userInfo) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uID", userInfo.getuID());
        values.put("nickName", userInfo.getNickName());
        values.put("password", userInfo.getPassword());
        values.put("phone", userInfo.getPhone());
        values.put("superiornum", userInfo.getSuperiornum());
        values.put("registered", userInfo.getRegistered());
        values.put("onbind", userInfo.isOnbind());
        long i = db.insert("userinfo", null, values);
        db.close();
        return (int) i;
    }


    public long saveUserInfo(UserInfo userInfo) {
        String userHead = userInfo.getHeadImg();
        if (userHead != null && !userHead.isEmpty()) {
            if (userHead.startsWith("http://")) {
                userHead = userHead.substring(userHead.lastIndexOf("/") + 1, userHead.length());
            }
        }
        ContentValues values = new ContentValues();
        values.put("uID", userInfo.getuID());
        values.put("realName", userInfo.getRealName());
        values.put("nickName", userInfo.getNickName());
        values.put("email", userInfo.getEmail());
        values.put("password", userInfo.getPassword());
        values.put("phone", userInfo.getPhone());
        values.put("sex", userInfo.getSex());
        values.put("headimg", userHead);
        values.put("signature", userInfo.getSignature());
        values.put("birthday", userInfo.getBirthday());
        values.put("gid", userInfo.getGid());
        values.put("superiornum", userInfo.getSuperiornum());
        values.put("registered", userInfo.getRegistered());
        values.put("level", userInfo.getLevel());
        values.put("onbind", userInfo.isOnbind());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        long i = db.insert("userinfo", null, values);
        db.close();
        return i;
    }

    public long updateGid(int uid, String gid) {
        ContentValues values = new ContentValues();
        values.put("gid", gid);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int isok = db.update("userinfo", values, "uID=?",
                new String[]{String.valueOf(uid)});
        db.close();
        return isok;
    }

    public void updateBind(int uid, boolean onbind) {
        ContentValues values = new ContentValues();
        values.put("onbind", onbind);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.update("userinfo", values, "uID=?",
                new String[]{String.valueOf(uid)});
        db.close();
    }

    //更新身份证信息
    /*public long updateIDCard(String phone, String realName, String IDCardNum) {
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("realName", realName);
        values.put("idcard", IDCardNum);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int isok = db.update("userinfo", values, "phone=?",
                new String[]{phone});
        db.close();
        return isok;
    }*/

    //更新银行卡信息
    /*public long updateBankCard(String realName, String bankCardNum) {
        ContentValues values = new ContentValues();
        values.put("realName", realName);
        values.put("bankcard", bankCardNum);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int isok = db.update("userinfo", values, "realName=?",
                new String[]{realName});
        db.close();
        return isok;
    }*/

    public long updateUserInfo(UserInfo userInfo) {
        ContentValues values = new ContentValues();
        values.put("superiornum", userInfo.getSuperiornum());
        values.put("email", userInfo.getEmail());
        values.put("sex", userInfo.getSex());
        values.put("headimg", userInfo.getHeadImg());
        values.put("signature", userInfo.getSignature());
        values.put("birthday", userInfo.getBirthday());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        int isok = db.update("userinfo", values, "nickName=?",
                new String[]{userInfo.getNickName()});
        db.close();
        return isok;
    }

    public void deleteUserInfo(String tableName, Integer id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from " + tableName + " where id=?",
                new Object[]{id});
        db.close();
    }

    // 查询用户信息，一台手机只对应一个用户
    public UserInfo findUserInfo() {
        UserInfo userinfo = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select uID,realName,nickName,email,password,phone,sex,headimg,signature,birthday,gid,superiornum,registered,level,onbind from userinfo", null);
        if (cursor.moveToNext()) {
            userinfo = new UserInfo();
            userinfo.setuID(cursor.getInt(0));
            userinfo.setRealName(cursor.getString(1));
            userinfo.setNickName(cursor.getString(2));
            userinfo.setEmail(cursor.getString(3));
            userinfo.setPassword(cursor.getString(4));
            userinfo.setPhone(cursor.getString(5));
            userinfo.setSex(cursor.getString(6));
            userinfo.setHeadImg(cursor.getString(7));
            userinfo.setSignature(cursor.getString(8));
            userinfo.setBirthday(cursor.getString(9));
            userinfo.setGid(cursor.getString(10));
            userinfo.setSuperiornum(cursor.getString(11));
            userinfo.setRegistered(cursor.getString(12));
            userinfo.setLevel(cursor.getInt(13));
            userinfo.setOnbind(cursor.getInt(14) != 0);
        }
        cursor.close();
        db.close();
        return userinfo;
    }

    public long getDataCount(String tableName) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from " + tableName, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        db.close();
        return count;
    }

    public void close() {
        dbOpenHelper.close();
    }

    // 查询所有的商品信息从云端获取图片
    public List<Map<String, Order>> findAllOrder() {
        List<Map<String, Order>> list = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("commodity", new String[]{"cid", "goodId",
                        "clothesTypeId", "goodCode", "goodName", "goodsDesc", "price",
                        "imgPath", "color", "size", "count", "divided_into"}, null, null, null, null,
                "cid desc");
        while (cursor.moveToNext()) {
            Map<String, Order> map = new LinkedHashMap<>();
            Order order = new Order(cursor.getInt(0), cursor.getInt(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5),
                    cursor.getDouble(6), cursor.getString(7),
                    cursor.getString(8), cursor.getString(9), cursor.getInt(10), cursor.getDouble(11));
            map.put(order.getCid() + "", order);
            list.add(map);
        }
        cursor.close();
        db.close();
        return list;
    }

    // 根据商品类别，商品id查询数据库是否存在该商品
    public Order findOrder(int goodId, String color, String size) {
        Order order = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db
                .rawQuery(
                        "select cid,count from commodity where goodId=? and color=? and size=?",
                        new String[]{goodId + "", color, size});
        if (cursor.moveToNext()) {
            order = new Order(cursor.getInt(0), cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return order;
    }

    // 添加订单项目
    public long addOrder(Goods good, String color, String size) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goodId", good.getId());
        values.put("goodCode", good.getGoodCode());
        values.put("goodName", good.getGoodName());
        values.put("goodsDesc", good.getGoodsDesc());
        values.put("price", good.getPrice());
        values.put("imgPath", good.getImgpath());
        values.put("color", color);
        values.put("size", size);
        values.put("count", 1);
        values.put("divided_into", good.getDivided_into());
        long i = db.insert("commodity", null, values);
        db.close();
        return i;
    }

    // 更新订单项目商品数量
    public int updateOrder(int cid, int count) {
        ContentValues values = new ContentValues();
        values.put("count", count);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int i = db.update("commodity", values, "cid=?",
                new String[]{cid + ""});
        db.close();
        return i;
    }

    // 查询所有订单项目的商品个数
    public int findTotalCount() {
        // 数据库查询
        int counts = 0;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("commodity", new String[]{"count"}, null,
                null, null, null, null);
        while (cursor.moveToNext()) {
            counts += cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return counts;
    }

    // 删除订单项目
    public int delOrder(int cid) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int did = db.delete("commodity", "cid=?", new String[]{cid + ""});
        db.close();
        return did;
    }

    /*添加商品浏览记录*/
    public void addHistory(Goods good, String time) {
        int count = findPropertyById(good.getId());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (count == 0) {
            values.put("goodId", good.getId());
            values.put("goodCode", good.getGoodCode());
            values.put("goodName", good.getGoodName());
            values.put("goodsDesc", good.getGoodsDesc());
            values.put("price", good.getPrice());
            values.put("imgPath", good.getImgpath());
            values.put("illustrations", good.getIllustrations());
            values.put("addtime", time);
            values.put("count", 1);
            values.put("colors", good.getColors());
            values.put("sizes", good.getSizes());
            db.insert("history", null, values);
        } else {
            values.put("addtime", time);
            values.put("count", count + 1);
            db.update("history", values, "goodId=?", new String[]{good.getId() + ""});
        }
        db.close();
    }

    private int findPropertyById(int goodid) {
        int count = 0;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select h.count from history h where h.goodId=?", new String[]{goodid + ""});
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }


    // 查询所有商品浏览记录
    public List<Goods> findHistory() {
        List<Goods> list = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("history", new String[]{"goodId", "goodCode", "goodName", "goodsDesc", "price",
                        "imgPath", "illustrations", "colors", "sizes"}, null, null, null, null,
                "addtime desc");
        while (cursor.moveToNext()) {
            Goods goods = new Goods(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getDouble(4),
                    cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
            list.add(goods);
        }
        cursor.close();
        db.close();
        return list;
    }

    // 删除商品浏览记录
    public int delHistoryById(int goodId) {
        int did = 0;
        int count = findPropertyById(goodId);
        if (count > 0) {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            did = db.delete("history", "goodId=?", new String[]{goodId + ""});
            db.close();
        }
        return did;
    }

    //清空history表
    public void delHistory() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM history");
        db.close();
    }

    public void remoreUser() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM userinfo");
        db.close();
    }

    public long saveStoreInfo(StoreInfo storeInfo) {
        ContentValues values = new ContentValues();
        values.put("id", storeInfo.getId());
        values.put("loginEmail", storeInfo.getLoginEmail());
        values.put("loginPwd", storeInfo.getLoginPwd());
        values.put("storeName", storeInfo.getStoreName());
        values.put("faceIcon", storeInfo.getFaceIcon());
        values.put("tell", storeInfo.getTell());
        values.put("contacts", storeInfo.getContacts());
        values.put("address", storeInfo.getAddress());
        values.put("visible", storeInfo.getVisible());
        values.put("regTime", storeInfo.getRegTime());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        long i = db.insert("storeInfo", null, values);
        db.close();
        return i;
    }

    // 查询用户信息，一台手机只对应一个用户
    public StoreInfo findStoreInfo() {
        StoreInfo storeInfo = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from storeInfo", null);
        if (cursor.moveToNext()) {
            storeInfo = new StoreInfo();
            storeInfo.setId(cursor.getInt(0));
            storeInfo.setLoginEmail(cursor.getString(1));
            storeInfo.setLoginPwd(cursor.getString(2));
            storeInfo.setStoreName(cursor.getString(3));
            storeInfo.setFaceIcon(cursor.getString(4));
            storeInfo.setTell(cursor.getString(5));
            storeInfo.setContacts(cursor.getString(6));
            storeInfo.setAddress(cursor.getString(7));
            storeInfo.setVisible(cursor.getInt(8));
            storeInfo.setRegTime(cursor.getString(9));
        }
        cursor.close();
        db.close();
        return storeInfo;
    }

    public void remoreStoreInfo() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM storeInfo");
        db.close();
    }

    public long addPushMag(ContentValues values) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        long i = db.insert("order_messages", null, values);
        db.close();
        return i;
    }

    /*查询接受者的所有消息*/
    public Cursor getMessage(String[] selectionArgs) {
        List<MessageEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        return db.rawQuery("select id,messageId,createTime,messageTitle,messageContent,targetActivity,targetURL,unPackOrderID from order_messages where receiver=? order by createTime DESC",
                selectionArgs);
    }

    public int getUnReadCount(String[] selectionArgs) {
        List<MessageEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from order_messages where receiver=? and modifyTime is null ",
                selectionArgs);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /*根据id删除一条消息*/
    public int delMsgById(String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int i = db.delete("order_messages", "id=?", selectionArgs);
        db.close();
        return i;
    }

    /*根据接受者删除所有消息*/
    public void delAllMsg(String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from order_messages where receiver=?", selectionArgs);
        db.close();
    }

    /*更新消息*/
    public int modifyMsg(ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int i = db.update("order_messages", values, selection, selectionArgs);
        db.close();
        return i;
    }

    public void save_wx_user(WXUser wxUser) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickName", wxUser.getNickName());
        values.put("sex", wxUser.getSex());
        values.put("headImg", wxUser.getHeadImg());
        values.put("country", wxUser.getCountry());
        values.put("province", wxUser.getProvince());
        values.put("unionid", wxUser.getUnionid());
        db.insert("wx_user", null, values);
        db.close();
    }

    public WXUser getWXUser() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("wx_user", new String[]{"nickName", "sex", "headImg", "country", "province", "unionid"}, null,
                null, null, null, null);
        try {
            WXUser wxUser = new WXUser();
            if (cursor.moveToNext()) {
                wxUser.setNickName(cursor.getString(0));
                wxUser.setSex(cursor.getInt(1));
                wxUser.setHeadImg(cursor.getString(2));
                wxUser.setCountry(cursor.getString(3));
                wxUser.setProvince(cursor.getString(4));
                wxUser.setUnionid(cursor.getString(5));
                return wxUser;
            }
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

}
