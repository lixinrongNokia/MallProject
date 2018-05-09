package iliker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBOpenHelper extends SQLiteOpenHelper {

    private final static String DBNAME = "iliker.db";
    private final static int VERSION = 14;

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS wx_user (nickName text,sex integer,headImg text,country text,province text,unionid text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS userinfo (uID integer, realName text, nickName text , email text, password varchar(64),phone text,sex text default '女',headimg text,signature text,birthday text default '1997-01-01',gid text,superiornum text,registered date,level Integer default 0,onbind integer default 0 )");
        db.execSQL("CREATE TABLE IF NOT EXISTS commodity (cid integer primary key autoincrement,goodId Integer,clothesTypeId text,goodCode text,goodName text,goodsDesc text,price double,imgPath text,color char(10),size char(10),count integer,divided_into double)");
        db.execSQL("CREATE TABLE IF NOT EXISTS history (goodId integer,goodCode text,goodName text,goodsDesc text,price double,imgPath text,illustrations text,addtime timestamp,count Integer,colors text (200),sizes text (200))");
        db.execSQL("CREATE TABLE IF NOT EXISTS storeInfo (id integer,loginEmail text,loginPwd text,storeName text,faceIcon text,tell text,contacts text,address text,visible integer,regTime text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `order_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT,`messageId` VARCHAR,`receiver` VARCHAR,`createTime` timestamp,`messageTitle` VARCHAR,`messageContent` VARCHAR,`modifyTime` timestamp,`targetActivity` VARCHAR,`targetURL` VARCHAR,`unPackOrderID` INTEGER,`otherTips` VARCHAR )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("ALTER TABLE history add colors text (200)");*/
        db.execSQL("ALTER TABLE userinfo add onbind integer default 0");
    }

}
