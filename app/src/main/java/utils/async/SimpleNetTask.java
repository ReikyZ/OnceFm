package utils.async;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by clownqiang on 2015/7/21.
 */
public abstract class SimpleNetTask extends NetAsyncTask {


    protected SimpleNetTask(Context cxt) {
        super(cxt);
    }

    protected SimpleNetTask(Context cxt, boolean openDialog) {
        super(cxt, openDialog);
    }

    protected SimpleNetTask(Context cxt, Dialog dialog) {
        super(cxt, dialog);
    }


    @Override
    protected void onPost(Exception e) {
        if (e != null) {
            e.printStackTrace();
        } else {
            onSucceed();
        }
    }

    protected abstract void doInBack() throws Exception;

    protected abstract void onSucceed();
}
