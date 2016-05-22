package utils.async;

/**
 * Created by clownqiang on 15/7/30.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ListView;

import api.utils.HttpCode;
import api.utils.HttpHelper;
import model.response.ApiResponse;
import utils.Utils;


public abstract class ResponseNetAsyncTask extends AsyncTask<Void, Void, ApiResponse> {
    Dialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    protected Context ctx;
    boolean openDialog = true;
    boolean dialogCancelable = false;
    boolean showErrorMsg = true;

    protected ResponseNetAsyncTask(Context ctx) {
        this.ctx = ctx;
    }

    protected ResponseNetAsyncTask(Context ctx, boolean openDialog) {
        this.ctx = ctx;
        this.openDialog = openDialog;
    }

    protected ResponseNetAsyncTask(Context ctx, boolean openDialog, SwipeRefreshLayout swipeRefreshLayout) {
        this.ctx = ctx;
        this.openDialog = openDialog;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    protected ResponseNetAsyncTask(Context ctx, boolean openDialog, ListView listView) {
        this.ctx = ctx;
        this.openDialog = openDialog;
        this.listView = listView;
    }

    protected ResponseNetAsyncTask(Context ctx, boolean openDialog, boolean showErrorMsg) {
        this.ctx = ctx;
        this.openDialog = openDialog;
        this.showErrorMsg = showErrorMsg;
    }

    protected ResponseNetAsyncTask(Context ctx, boolean openDialog, SwipeRefreshLayout swipeRefreshLayout, boolean showErrorMsg) {
        this.ctx = ctx;
        this.openDialog = openDialog;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.showErrorMsg = showErrorMsg;
    }

    protected ResponseNetAsyncTask(Context ctx, boolean openDialog, ListView listView, boolean showErrorMsg) {
        this.ctx = ctx;
        this.openDialog = openDialog;
        this.listView = listView;
        this.showErrorMsg = showErrorMsg;
    }

    public ResponseNetAsyncTask setOpenDialog(boolean openDialog) {
        this.openDialog = openDialog;
        return this;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialogCancelable(boolean cancelable) {
        dialogCancelable = cancelable;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (openDialog) {
//            dialog = Utils.createProgressDialog((Activity) ctx);
            dialog.setCancelable(dialogCancelable);
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancel(true);
                }
            });
        }
//        if (listView != null) listView.loadShow();
    }

    @Override
    protected ApiResponse doInBackground(Void... params) {
        ApiResponse apiResponse = null;
        if (isDestroy()) return null;
        try {
            apiResponse = doInBack();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sssssss", "UNKNOW ERROR====" + e);
            apiResponse = new ApiResponse(HttpCode.UNKNOWN_ERROR.getCode(), HttpCode.UNKNOWN_ERROR.getMessage(), "");
        }
        if (isDestroy()) return null;
        if (isCancelled()) {
            return null;
        }
        return apiResponse;
    }

    private boolean isDestroy() {
        if (ctx instanceof Activity && ((Activity) ctx).isFinishing()) {
            cancel(true);
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(ApiResponse response) {
        super.onPostExecute(response);
        if (openDialog) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        //添加SwipeRefreshLayout时
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                }
            });
        //添加ListView时
//        if (listView != null) listView.loadComplete();
        if (response == null) return;
        if (HttpHelper.checkCodeEffect(response.getCode())) {
            //成功时返回Success的Message
            onPost(response.getBodyStr());
        } else {
            //错误时根据不同情况做出相应提示
            if (response.getCode() == HttpCode.TOKEN_EXPIRED.getCode())
//                JPushInterface.stopPush(ctx);
                Log.i("error", response.getMsg() + " ");
            if (showErrorMsg) {
                if (response.getCode() == 600) {
                    Utils.showToast(ctx, "网络中断");
                } else {
                    // Toast ERROR
//                    if (!response.getMsg().equals("请先登录"))
//                        Utils.showToast(ctx, response.getMsg() + "");
                }
            }
            onFailure();
        }
    }

    public void destroyTask() {
        if (getStatus() != Status.FINISHED)
            cancel(true);
    }

    protected abstract ApiResponse doInBack() throws Exception;

    protected abstract void onPost(String result);

    protected abstract void onFailure();

}
