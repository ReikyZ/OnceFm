package api.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by clownqiang on 15/8/3.
 */
public class HttpHelper {

    /**
     * 检查返回参数是否通过
     */
    public static boolean checkCodeEffect(int code) {
        return code == HttpCode.OK.getCode() ||
                code == HttpCode.CREATE.getCode() ||
                code == HttpCode.ACCEPTED.getCode();
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        }
        return connectivityManager.getActiveNetworkInfo().isAvailable();
    }

    /**
     * 时间转换
     */
    public static long getLongFromTime(String isoTime) {
        Log.i("isoTime", isoTime);
        StringBuffer sbDate = new StringBuffer();
        sbDate.append(isoTime);
        String newDate = sbDate.substring(0, 19).toString();
        String rDate = newDate.replace("T", " ");
        String nDate = rDate.replaceAll("-", "/");
        long epoch = 0;
        try {
            epoch = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(nDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return epoch;
    }

    /**
     * http user-agent
     *
     * @return
     */
    public static String userAgent() {
        Context context = SessionData.getContext();
        PackageManager packageManager = context.getPackageManager();
        String version = "2.0";
        try {
            version = packageManager.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "ChiSha/" + version + " (" + Build.MODEL + "; Android " + Build.VERSION.RELEASE + "; " + "Scale/" + context.getResources().getDisplayMetrics().density + ")";
    }
}
