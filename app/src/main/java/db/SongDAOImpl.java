package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.SongStatusInfo;
import utils.Utils;


/**
 * Created by Administrator on 2015/7/14.
 */
public class SongDAOImpl {

    final static String TAG = "===SongDAOImpl===";

    private static SongDAOImpl songDAO;
    DatabaseManager databaseManager;
//    private DBHelper mHelper = null;

    public SongDAOImpl(Context context) {
//        mHelper = DBHelper.getInstance(context);
        databaseManager = DatabaseManager.initInstance(DBHelper.getInstance(context));
    }


    public synchronized static SongDAOImpl getInstance(Context context) {
        if (songDAO == null) {
            songDAO = new SongDAOImpl(context);
        }
        return songDAO;
    }


    public synchronized void insertSongStatus(SongStatusInfo songStatusInfo) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        db.execSQL("insert into song_info(db_id, songUri, picUri, songName, artist, albumName, status, stared, finished) values(?,?,?,?,?,?,?,?,?)",
                new Object[]{songStatusInfo.getDb_id(), songStatusInfo.getSongUri(), songStatusInfo.getPicUri(), songStatusInfo.getSongName(), songStatusInfo.getArtist(), songStatusInfo.getAlbumName(), 0, 0, 0});
        databaseManager.getInstance().closeDatabase();
        Log.e("sssss", TAG + "insert==" + songStatusInfo.getDb_id() + "==" + Utils.getLineNumber(new Exception()));
    }

    public synchronized void deleteSongInfo(int id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        db.execSQL("delete from song_info where db_id = ?", new Object[]{id});
        Log.i("Test info", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> delete success on " + id);
        databaseManager.getInstance().closeDatabase();
    }

    public synchronized List<SongStatusInfo> getSongStatuss() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        List<SongStatusInfo> list = new ArrayList<SongStatusInfo>();

        Cursor cursor = db.rawQuery("select * from song_info where status = 0 ORDER BY RANDOM() ", null);

        while (cursor.moveToNext()) {
            SongStatusInfo songStatusInfo = new SongStatusInfo();
            songStatusInfo.setDb_id(cursor.getInt(cursor.getColumnIndex("db_id")));
            songStatusInfo.setSongUri(cursor.getString(cursor.getColumnIndex("songUri")));
            songStatusInfo.setPicUri(cursor.getString(cursor.getColumnIndex("picUri")));
            songStatusInfo.setSongName(cursor.getString(cursor.getColumnIndex("songName")));
            songStatusInfo.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
            songStatusInfo.setAlbumName(cursor.getString(cursor.getColumnIndex("albumName")));
            songStatusInfo.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            list.add(songStatusInfo);
        }
        cursor.close();
        databaseManager.getInstance().closeDatabase();
        return list;
    }

    public synchronized SongStatusInfo getSongStatus(int db_id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        SongStatusInfo songStatusInfo = new SongStatusInfo();
        Cursor cursor = db.rawQuery("select * from song_info where db_id = ? ", new String[]{String.valueOf(db_id)});
        if (cursor.moveToNext()) {
            songStatusInfo.setDb_id(cursor.getInt(cursor.getColumnIndex("db_id")));
            songStatusInfo.setSongUri(cursor.getString(cursor.getColumnIndex("songUri")));
            songStatusInfo.setPicUri(cursor.getString(cursor.getColumnIndex("picUri")));
            songStatusInfo.setSongName(cursor.getString(cursor.getColumnIndex("songName")));
            songStatusInfo.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
            songStatusInfo.setAlbumName(cursor.getString(cursor.getColumnIndex("albumName")));
            songStatusInfo.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

        } else {
            return null;
        }
        cursor.close();
        databaseManager.getInstance().closeDatabase();
        Log.i("Test db", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET aonginfo by ID " + db_id + "==" + songStatusInfo.toString());
        return songStatusInfo;
    }

    public synchronized boolean isExist(long db_id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from song_info where db_id = ? and status =0 ", new String[]{String.valueOf(db_id)});

        boolean exists = cursor.moveToNext();
        cursor.close();

        databaseManager.getInstance().closeDatabase();
        if (!exists)
            Log.e("sssss", TAG + "isExist==" + exists + "==" + Utils.getLineNumber(new Exception()));
        return exists;
    }

    public synchronized boolean isExistSame(String songName, String artist) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from song_info where songName = ? and artist = ?", new String[]{songName, artist});

        boolean exists = cursor.moveToNext();
        cursor.close();

        databaseManager.getInstance().closeDatabase();

        return exists;
    }

    public synchronized int hasData() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from song_info where status = 0", null);

        int count = new Integer(cursor.getCount());
        cursor.close();

        databaseManager.getInstance().closeDatabase();
        return count;
    }

    public synchronized int getPlayed() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from song_info where status = -1", null);

        int count = new Integer(cursor.getCount());
        cursor.close();

        databaseManager.getInstance().closeDatabase();
        return count;
    }

    public synchronized int getStared() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from song_info where stared = 1", null);

        int count = new Integer(cursor.getCount());
        cursor.close();

        databaseManager.getInstance().closeDatabase();
        return count;
    }

    public synchronized int getFinished() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from song_info where finished = 1", null);

        int count = new Integer(cursor.getCount());
        cursor.close();

        databaseManager.getInstance().closeDatabase();
        return count;
    }

    public synchronized void updateStatus(int status, int db_id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        db.execSQL("update song_info set status = ? where db_id = ? ", new Object[]{status, db_id});

        databaseManager.getInstance().closeDatabase();
        Log.i("Test fileinfo", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Skip success status = -1" + "  on " + db_id);
    }

    public synchronized SongStatusInfo getRandomOne(int currentID) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM song_info where status = 0 and db_id != ? ORDER BY RANDOM() limit 1", new String[]{currentID + ""});
        SongStatusInfo songStatusInfo = new SongStatusInfo();
        if (cursor.moveToNext()) {
            songStatusInfo.setDb_id(cursor.getInt(cursor.getColumnIndex("db_id")));
            songStatusInfo.setSongUri(cursor.getString(cursor.getColumnIndex("songUri")));
            songStatusInfo.setPicUri(cursor.getString(cursor.getColumnIndex("picUri")));
            songStatusInfo.setSongName(cursor.getString(cursor.getColumnIndex("songName")));
            songStatusInfo.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
            songStatusInfo.setAlbumName(cursor.getString(cursor.getColumnIndex("albumName")));
            songStatusInfo.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

        } else {
            return null;
        }
        cursor.close();
        Log.i("Test db", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET RANDOM ONE " + songStatusInfo.getDb_id() + " " + songStatusInfo.getSongName());
        return songStatusInfo;
    }

    public synchronized void onFinished(int db_id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        db.execSQL("update song_info set finished = 1 where db_id = ? ", new Object[]{db_id});
        databaseManager.getInstance().closeDatabase();
        Log.i("Test fileinfo", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Finished = 1" + "  on " + db_id);
    }

    public synchronized boolean isStared(int db_id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from song_info where stared = 1 and db_id = ? ", new String[]{String.valueOf(db_id)});

        boolean exists = new Boolean(cursor.moveToNext());
        cursor.close();

        databaseManager.getInstance().closeDatabase();

        return exists;
    }

    public synchronized void markStar(int db_id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        db.execSQL("update song_info set stared = 1 where db_id = ? ", new Object[]{db_id});
        databaseManager.getInstance().closeDatabase();
    }

    public synchronized void markStarCancel(int db_id) {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        db.execSQL("update song_info set stared = 0 where db_id = ? ", new Object[]{db_id});
        databaseManager.getInstance().closeDatabase();
    }


    public synchronized String getStaredArtist() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM song_info where stared = 1 ORDER BY RANDOM() limit 1", new String[]{});
        String staredArtist;
        if (cursor.moveToNext()) {
            staredArtist = cursor.getString(cursor.getColumnIndex("artist"));

        } else {
            return null;
        }
        cursor.close();
        databaseManager.getInstance().closeDatabase();
        return staredArtist;
    }

    public synchronized String getStaredAlbum() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM song_info where stared = 1 ORDER BY RANDOM() limit 1", new String[]{});
        String staredAlbum;
        if (cursor.moveToNext()) {
            staredAlbum = cursor.getString(cursor.getColumnIndex("albumName"));

        } else {
            return null;
        }
        cursor.close();
        databaseManager.getInstance().closeDatabase();
        return staredAlbum;
    }

    public synchronized String getFinishedArtist() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM song_info where finished = 1 ORDER BY RANDOM() limit 1", new String[]{});
        String finishedArtist;
        if (cursor.moveToNext()) {
            finishedArtist = cursor.getString(cursor.getColumnIndex("artist"));

        } else {
            return null;
        }
        cursor.close();
        databaseManager.getInstance().closeDatabase();
        return finishedArtist;
    }

    public synchronized String getfinishedAlbum() {
        SQLiteDatabase db = databaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM song_info where finished = 1 ORDER BY RANDOM() limit 1", new String[]{});
        String finishedAlbum;
        if (cursor.moveToNext()) {
            finishedAlbum = cursor.getString(cursor.getColumnIndex("albumName"));

        } else {
            return null;
        }
        cursor.close();
        databaseManager.getInstance().closeDatabase();
        return finishedAlbum;
    }
}
