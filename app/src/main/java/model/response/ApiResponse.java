package model.response;


/**
 * Created by clownqiang on 15/9/10.
 */
public class ApiResponse<T> {
    private int code;  //返回码
    private String msg; //返回信息
    private String bodyStr; //body有效信息
    private T result;

    public ApiResponse(int code, String msg, String bodyStr) {
        this.code = code;
        this.msg = msg;
        this.bodyStr = bodyStr;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getBodyStr() {
        return bodyStr;
    }

    public T getResult() {
        return result;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", bodyStr='" + bodyStr + '\'' +
                ", result=" + result +
                '}';
    }
}
