package db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Reiky on 2015/7/22.
 */
public class DatabaseManager {

    private AtomicInteger openCounter = new AtomicInteger();

    private static DatabaseManager instance;
    private  static SQLiteOpenHelper databaseHelper;
    private SQLiteDatabase database;

    public  static  synchronized DatabaseManager initInstance(SQLiteOpenHelper helper){
        if (instance ==null){
            instance = new DatabaseManager();
            databaseHelper = helper;
        }
        return null;
    }

    public  static  synchronized  DatabaseManager getInstance(){
        if ( instance==null){
            throw new IllegalStateException(DatabaseManager.class.getSimpleName()+" is not initialized, call initializeInstance(..) method first.");
        }
        return  instance;
    }

    public  synchronized SQLiteDatabase openDatabase(){
        if (openCounter.incrementAndGet()==1){
            database = databaseHelper.getReadableDatabase();
        }
        return  database;
    }

    public  synchronized  void closeDatabase(){
        if (openCounter.decrementAndGet()==0){
            database.close();
        }
    }
}
