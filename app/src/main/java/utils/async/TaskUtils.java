package utils.async;

import android.os.AsyncTask;

/**
 * Created by clownqiang on 15/10/21.
 */
public class TaskUtils {

    public static void cancelTask(ResponseSimpleNetTask task) {
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true); // 如果Task还在运行，则先取消它
        }
    }
}
