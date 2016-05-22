package utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.reikyz.oncefm.R;

import java.io.File;
import java.util.Random;

import app.MyApp;
import data.Config;
import db.SongDAOImpl;
import model.SongStatusInfo;

/**
 * Created by clownqiang on 15/7/15.
 */
public class Utils {

    final static String TAG = "===Utils===";

    SongDAOImpl songDAO = null;

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 随机产生一个color
     */
    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


    /**
     * 生成长度为30的随机字符串，防止重复
     */
    public static String getRandomString() { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 30; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * * 获取版本号
     * * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            Log.e("sssss", TAG + "APP Version==" + version);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //获得本机IMEI码
    public String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.e("sssss", TAG + "IMEI==" + tm.getDeviceId());
        return tm.getDeviceId();
    }


    //获得MAC地址
    private String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        Log.e("sssss", TAG + "MAC Address==" + info.getMacAddress());
        return info.getMacAddress();
    }


    int currVolume = 0;

    public void CloseSpeaker(Context context) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(false);
                    Log.i("Test mediaPlayer ", "======================================= first close speakker " + audioManager.getMode());
                } else {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(false);

                    Log.i("Test mediaPlayer ", "======================================= close speakker " + audioManager.getMode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getLineNumber(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0) return "==" + -1 + "=="; //
        return "==" + trace[0].getLineNumber() + "==";
    }


    public void noticeGone(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(730277);
    }


    public void noticeControler(Context context, boolean isPlaying, int currentID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //此Builder为android.support.v4.app.NotificationCompat.Builder中的,下同。
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        songDAO = SongDAOImpl.getInstance(context);
        SongStatusInfo songStatusInfo = songDAO.getSongStatus(currentID);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_bar);
        mBuilder.setContent(remoteViews);

        //系统收到通知时,通知栏上面显示的文字。
        mBuilder.setTicker(isPlaying ? "正在播放 " + songStatusInfo.getSongName() : "暂停播放 " + songStatusInfo.getSongName());

        //notification Views
        remoteViews.setCharSequence(R.id.noti_song_name, "setText", songStatusInfo.getSongName());
        remoteViews.setCharSequence(R.id.noti_song_artist, "setText", songStatusInfo.getArtist());
        remoteViews.setBitmap(R.id.noti_img, "setImageBitmap", BitmapUtils.loadBitMap(new File(MyApp.DOWNLOAD_DIR, "P" + currentID + ".png")));
        remoteViews.setImageViewResource(R.id.noti_star, songDAO.isStared(currentID) ? R.mipmap.stared_black : R.mipmap.star_black);
        remoteViews.setImageViewResource(R.id.noti_play, isPlaying ? R.mipmap.pause_black : R.mipmap.play_black);

        //nptofocation on click
//        Intent intentToMain = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntentToMain = PendingIntent.getActivities(this, 0, new Intent[]{intentToMain}, 0);
//        remoteViews.setOnClickPendingIntent(R.id.noti_img, pendingIntentToMain);
        Intent intentBroadcase = new Intent("BROADCASR TEST");
        PendingIntent pendingIntentBroadcat = PendingIntent.getBroadcast(context, 0, intentBroadcase, PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.noti_img, pendingIntentBroadcat);


        Intent intentShutdown = new Intent(Config.ACTION_CLOSE_MAIN);
        PendingIntent pendingIntentShutdown = PendingIntent.getBroadcast(context, 0, intentShutdown, PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.noti_shutdown, pendingIntentShutdown);
        //显示在通知栏上的小图标
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//
        //设置为不可清除模式
        mBuilder.setOngoing(false);

        //显示通知,id必须不重复,否则新的通知会覆盖旧的通知（利用这一特性,可以对通知进行更新）
        notificationManager.notify(730277, mBuilder.build());
    }



    /**
     * 判断当前是否是wifi连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
