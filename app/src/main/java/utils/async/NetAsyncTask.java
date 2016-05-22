package utils.async;

/**
 * Created by clownqiang on 2015/7/21.
 */


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public abstract class NetAsyncTask extends AsyncTask<Void, Void, Void> {
    Dialog dialog;
    protected Context ctx;
    boolean openDialog = false;
    Exception exception;

    protected NetAsyncTask(Context ctx) {
        this.ctx = ctx;
    }

    protected NetAsyncTask(Context ctx, boolean openDialog) {
        this.ctx = ctx;
        this.openDialog = openDialog;
    }

    protected NetAsyncTask(Context ctx, Dialog dialog) {
        this.ctx = ctx;
        this.dialog = dialog;
    }

    public NetAsyncTask setOpenDialog(boolean openDialog) {
        this.openDialog = openDialog;
        return this;
    }

    public Dialog getDialog() {
        return dialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (openDialog) {
//            dialog = Utils.createProgressDialog((Activity) ctx);
        }
        if (dialog != null) {
            if (!((Activity) ctx).isFinishing())
                dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancel(true);
                }
            });
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            doInBack();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        super.onPostExecute(aVoid);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        onPost(exception);
    }

    protected abstract void doInBack() throws Exception;

    protected abstract void onPost(Exception e);
}

