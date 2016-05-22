package widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.reikyz.oncefm.R;

import data.Config;
import data.Prefs;

/**
 * Created by Reiky on 2016/5/23.
 */
public class AlertHelper {

    public static void askDownload(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.is_downing_wizout_wifi));
        builder.setMessage(context.getString(R.string.shall_continue));
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Prefs.save(Config.ALLOW_DOWNLOAD_WIZOUT_WIFI, false);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Prefs.save(Config.ALLOW_DOWNLOAD_WIZOUT_WIFI, true);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
