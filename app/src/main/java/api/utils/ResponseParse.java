package api.utils;

import android.util.Log;

import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import app.MyApp;
import model.response.ApiResponse;
import utils.Utils;

/**
 * Created by clownqiang on 15/9/11.
 */
public class ResponseParse {

    final static String TAG = "===ResponseParse===";

    public ApiResponse parse(Response response) throws IOException {
        ApiResponse apiResponse = null;
        if (response == null) {
            return new ApiResponse(HttpCode.TOKEN_EXPIRED.getCode(), HttpCode.TOKEN_EXPIRED.getMessage(), "");
        }
        int code = response.code();
        String message = response.message();
        String bodyStr = response.body().string();

        apiResponse = new ApiResponse<>(code, message, bodyStr);

        //TODO:自行添加遇到的情况
        if (!HttpHelper.checkCodeEffect(code)) {
            if (code == HttpCode.SERVER_ERROR.getCode()) {
                apiResponse.setMsg(HttpCode.SERVER_ERROR.getMessage());
            } else if (code == HttpCode.TIMEOUT.getCode()) {
                apiResponse.setMsg(HttpCode.TIMEOUT.getMessage());
            } else if (code == HttpCode.TOKEN_EXPIRED.getCode()) {
                apiResponse.setMsg(HttpCode.TOKEN_EXPIRED.getMessage());
            } else {
                try {
                    String alert = JsonUtils.getString(new JSONObject(bodyStr), "_message");
                    apiResponse.setMsg(alert);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return apiResponse;
        }
        if (!message.contains("_message") && message.contains("DOCTYPE")) {
            return new ApiResponse(HttpCode.SERVER_ERROR.getCode(), HttpCode.SERVER_ERROR.getMessage(), "");
        }
        return apiResponse;
    }

    public ApiResponse decodeFile(Response response, String fileName) throws IOException {
        ApiResponse apiResponse = null;
        if (response == null) {
            return new ApiResponse(HttpCode.TOKEN_EXPIRED.getCode(), HttpCode.TOKEN_EXPIRED.getMessage(), "");
        }
        int code = response.code();
        String message = response.message();
//        String bodyStr = response.body().string();
        String bodyStr = "";


        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            File file = new File(MyApp.DOWNLOAD_DIR, "m" + fileName + ".mp3");
            Log.e("sssss", file.toString());
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            //如果下载文件成功，第一个参数为文件的绝对路径
            Log.e("ssss", TAG + "SUCCESS==" + Utils.getLineNumber(new Exception()));
        } catch (IOException e) {
            Log.e("ssss", TAG + "IOException==" + Utils.getLineNumber(new Exception()));
        } finally {
            Log.e("ssss", TAG + Utils.getLineNumber(new Exception()));
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
        apiResponse = new ApiResponse<>(code, message, bodyStr);

        //TODO:自行添加遇到的情况
        if (!HttpHelper.checkCodeEffect(code)) {
            if (code == HttpCode.SERVER_ERROR.getCode()) {
                apiResponse.setMsg(HttpCode.SERVER_ERROR.getMessage());
            } else if (code == HttpCode.TIMEOUT.getCode()) {
                apiResponse.setMsg(HttpCode.TIMEOUT.getMessage());
            } else if (code == HttpCode.TOKEN_EXPIRED.getCode()) {
                apiResponse.setMsg(HttpCode.TOKEN_EXPIRED.getMessage());
            }
            return apiResponse;
        }
        if (!message.contains("_message") && message.contains("DOCTYPE")) {
            return new ApiResponse(HttpCode.SERVER_ERROR.getCode(), HttpCode.SERVER_ERROR.getMessage(), "");
        }
        return apiResponse;

    }
}
