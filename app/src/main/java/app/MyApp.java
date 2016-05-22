package app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import api.utils.SessionData;
import io.fabric.sdk.android.Fabric;
import utils.BitmapUtils;

/**
 * Created by Reiky on 2016/5/20.
 */
public class MyApp extends Application {
    final static String TAG = "===MyApp===";

    public static String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OnceFm/";
    static Context context;
    public static List<Integer> downloadingList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        context = getApplicationContext();
        SessionData.initApi(context);

        //如果允许更新
        //检查是否有新版本apk
        //检查是否已下载
        // 已下载 安装
        // 未下载 下载

        // 是否希望提醒更新

        initImageLoader(this);
        initDownloadFir();

    }

    private void initDownloadFir() {
        File dir = new File(DOWNLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.i("ssss ", TAG + "Made Download Folder");
        }
    }


    public static Context getContext() {
        return context;
    }

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                "OnceFm/Cache");
        ImageLoaderConfiguration config = BitmapUtils.getImageLoaderConfig(context, cacheDir);
        ImageLoader.getInstance().init(config);
    }
}
