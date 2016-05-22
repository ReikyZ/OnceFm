package api.utils;

/**
 * Created by clownqiang on 15/8/4.
 */
public enum HttpCode {

    TOKEN_REFRESH_FAILED(-200, "网络异常"),
    TOKEN_EXPIRED(-100, "信息已过期，重新登录"), //由于现在refresh_token不可能过期，所以只可能为网络刷新异常
    TIMEOUT(0, "加载超时"),
    OK(200, "成功"),
    CREATE(201, "创建"),
    ACCEPTED(202, "接受"),
    UNAUTHORIZED(401, "未授权"),
    BAD_REQUEST(400, "请求出错"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "未找到"),
    SERVER_ERROR(500, "服务器维护中"),
    UNKNOWN_ERROR(600, "未知错误");

    private final int code;
    private final String message;

    HttpCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
