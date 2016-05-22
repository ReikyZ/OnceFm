package model.response;

/**
 * Created by clownqiang on 15/7/31.
 */
public class BaseResponseModel {
    int code;
    String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
