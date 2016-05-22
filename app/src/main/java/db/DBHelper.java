package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Reiky on 2015/7/2.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "oncefm.db";
    private static DBHelper sHelper = null;

    private static final int VERSION = 1;
    private static final String SQL_CREATE_THREAD_TABLE = "create table thread_info(_id integer primary key autoincrement," +
            "thread_id integer, " +
            "url text," +
            "start integer," +
            "end integer," +
            "finished integer)";
    private static final String SQL_DROP_THREAD_TABLE = "drop table if exists thread_info";

    private static final String SQL_CREATE_SONG_TABLE = "create table song_info(_id integer primary key autoincrement," +
            "db_id integer," +
            "songUri text, " +
            "picUri text," +
            "songName text," +
            "artist text," +
            "albumName text," +
            "status integer," +
            "stared integer," +
            "finished integer)";
    private static final String SQL_DROP_SONG_TABLE = "drop table if exists song_info";

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (sHelper == null) {
            sHelper = new DBHelper(context);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_THREAD_TABLE);
        db.execSQL(SQL_CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_THREAD_TABLE);
        db.execSQL(SQL_CREATE_THREAD_TABLE);

        db.execSQL(SQL_DROP_SONG_TABLE);
        db.execSQL(SQL_CREATE_SONG_TABLE);
    }
}
