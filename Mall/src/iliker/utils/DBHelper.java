package iliker.utils;

import iliker.db.DatabaseService;

import static com.iliker.application.CustomApplication.customApplication;

/**
 * Created by WDHTC on 2016/4/15.
 */
public final class DBHelper {
    private static DatabaseService ds;

    private DBHelper() {
    }

    public static DatabaseService getDB() {
        if (ds == null) {
            ds = new DatabaseService(customApplication);
        }
        return ds;
    }
}

