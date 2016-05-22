package api.utils;

/**
 * Created by clownqiang on 15/5/8.
 */

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    /**
     * 由JSON对象取出相应的字符串
     *
     * @param obj Json对象
     * @param key 取出String的键
     */
    public static String getString(JSONObject obj, String key) {
        return getString(obj, key, null);
    }

    /**
     * 由JSON对象取出相应的字符串
     *
     * @param obj
     * @param key
     * @param dft
     */
    public static String getString(JSONObject obj, String key, String dft) {
        if (!obj.has(key))
            return dft;
        try {
            Object value = obj.get(key);
            if (value == null)
                return null;
            String ret = value.toString();
            if ("null".equals(ret))
                return null;
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JsonUtils", "parse error");
            return dft;
        }
    }

    /**
     * 由JSON对象取出相应的int数据
     *
     * @param obj
     * @param key
     */
    public static int getInt(JSONObject obj, String key) {
        return getInt(obj, key, 0);
    }

    /**
     * 由JSON对象取出相应的int数据
     *
     * @param obj
     * @param key
     * @param dft
     */
    public static int getInt(JSONObject obj, String key, int dft) {
        if (!obj.has(key))
            return dft;
        try {
            int value = obj.getInt(key);
            return value;
        } catch (JSONException e) {
            //e.printStackTrace();
            return dft;
        }
    }


    public static boolean getBoolean(JSONObject obj, String key) {
        try {
            boolean value = obj.getBoolean(key);
            return value;
        } catch (JSONException e) {
            //e.printStackTrace();
            return false;
        }
    }

}
