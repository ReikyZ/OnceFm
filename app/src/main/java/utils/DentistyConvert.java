package utils;


import app.MyApp;

/**
 * Created by clownqiang on 15/9/11.
 */
public class DentistyConvert {

    public static int dp2px(float dp) {
        return (int) (0.5F + dp * (MyApp.getContext().getResources().getDisplayMetrics().density));
    }

    public static int px2dp(float px) {
        return (int) (px / (MyApp.getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = MyApp.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
