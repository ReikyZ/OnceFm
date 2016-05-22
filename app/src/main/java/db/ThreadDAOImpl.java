package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.ThreadInfo;


/**
 * Created by Reiky on 2015/7/2.
 */
public class ThreadDAOImpl {

    private static ThreadDAOImpl threadDAO;
    private DatabaseManager databaseManager;
    private DBHelper mHelper = null;

    public ThreadDAOImpl(Context context) {
        mHelper = DBHelper.getInstance(context);
        this.databaseManager = DatabaseManager.initInstance(DBHelper.getInstance(context));
    }


    public synchronized static ThreadDAOImpl getInstance(Context context) {
        if (threadDAO == null) {
            threadDAO = new ThreadDAOImpl(context);
        }
        return threadDAO;
    }

    public synchronized void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.execSQL("insert into thread_info(thread_id,url,start,end,finished) values(?,?,?,?,?)",
                new Object[]{threadInfo.getId(), threadInfo.getUrl(), threadInfo.getStart(), threadInfo.getEnd(), threadInfo.getFinished()});
        databaseManager.getInstance().closeDatabase();
    }

    public synchronized void deleteThread(String url) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.execSQL("delete from thread_info where url = ?", new Object[]{url});

        DatabaseManager.getInstance().closeDatabase();
    }

    public synchronized void deleteAllThread() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.execSQL("delete from thread_info", new Object[]{});
        DatabaseManager.getInstance().closeDatabase();
        Log.i("Test db", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Clear all Thread");
    }

    public synchronized void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.execSQL("update thread_info set finished = ? where url = ? and thread_id =?", new Object[]{finished, url, thread_id});

        DatabaseManager.getInstance().closeDatabase();
    }

    public synchronized List<ThreadInfo> getThreads(String url) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        List<ThreadInfo> list = new ArrayList<ThreadInfo>();

        Cursor cursor = db.rawQuery("select * from thread_info where url =?", new String[]{url});

        while (cursor.moveToNext()) {
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(threadInfo);
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return list;
    }

    public synchronized boolean isExists(String url, int thread_id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?", new String[]{url, thread_id + ""});

        boolean exists = cursor.moveToNext();
        cursor.close();

        DatabaseManager.getInstance().closeDatabase();

        return exists;
    }

    public synchronized int hasUnfinshedThread() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info", null);

        int count = new Integer(cursor.getCount());
        cursor.close();

        DatabaseManager.getInstance().closeDatabase();

        return count;
    }


    public synchronized boolean isExistSame(int songID) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where thread_id = ?", new String[]{songID + ""});

        boolean exists = cursor.moveToNext();
        cursor.close();

        DatabaseManager.getInstance().closeDatabase();

        return exists;
    }
}
