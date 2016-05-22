package utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.reikyz.oncefm.R;

import app.MyApp;


/**
 * Created by panl on 15/7/7.
 */
public class AnimHelper {


    private static final Context context = MyApp.getContext();
    public static final int UPDATE_GUIDE_TEXT = 1000;
    private static Animation leftPopAnim;
    private static Animation rightPopAnim;
    private static ValueAnimator ballBouncer;
    private static Animation ballDropAnim;

    /**
     * 引导页文字动画效果
     *
     * @param guideLL
     * @param isLeftSliding
     * @param handler
     */
    public static void showGuideTextAnim(final LinearLayout guideLL, boolean isLeftSliding, final Handler handler) {
        final Animation animation1;
        final Animation animation2;
        if (isLeftSliding) {
            animation1 = AnimationUtils.loadAnimation(context, R.anim.left_out);
            animation2 = AnimationUtils.loadAnimation(context, R.anim.right_in);
        } else {
            animation1 = AnimationUtils.loadAnimation(context, R.anim.right_out);
            animation2 = AnimationUtils.loadAnimation(context, R.anim.left_in);
        }
        guideLL.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                guideLL.startAnimation(animation2);
                handler.sendEmptyMessage(UPDATE_GUIDE_TEXT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


/*    public static void showGuideImgAnim(final ImageView view) {
        ballBouncer = ValueAnimator.ofInt(0, DentistyConvert.dp2px(50));
        ballBouncer.setDuration(1500);
        ballBouncer.setRepeatMode(ValueAnimator.REVERSE);
        ballBouncer.setRepeatCount(ValueAnimator.INFINITE);
        ballBouncer.setInterpolator(new DecelerateInterpolator());
        ValueAnimator.setFrameDelay(50);
        ballBouncer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int animatedValue = (Integer) ballBouncer.getAnimatedValue();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setPadding(view.getPaddingLeft(),
                                DentistyConvert.dp2px(50) - animatedValue,
                                view.getPaddingRight(), animatedValue);
                        view.invalidate();
                    }
                });
            }
        });
        ballBouncer.start();
    }

    public static void clearGuideImgAnim() {
        if (ballBouncer != null) {
            ballBouncer.cancel();
            ballBouncer = null;
        }
    }*/

    /**
     * 引导页饭团动画效果
     *
     * @param view
     */
    public static void showBallDropAnim(View view) {
        ballDropAnim = AnimationUtils.loadAnimation(context, R.anim.icon_drop_down);
        view.startAnimation(ballDropAnim);
    }

    public static void clearBallAnim() {
        if (ballDropAnim != null) {
            ballDropAnim.cancel();
            ballDropAnim = null;
        }
    }

    public static void showLeftPopAnim(View view) {
        leftPopAnim = AnimationUtils.loadAnimation(context, R.anim.left_pop);
        view.startAnimation(leftPopAnim);
    }

    public static void showRightPopAnim(View view) {
        rightPopAnim = AnimationUtils.loadAnimation(context, R.anim.right_pop);
        view.startAnimation(rightPopAnim);
    }

    /**
     * 给CheckBox加点击动画
     *
     * @param view
     */
    public static void showPopScaleAnimation(View view) {
        float[] values = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", values),
                ObjectAnimator.ofFloat(view, "scaleY", values));
        set.setDuration(300);
        set.start();
    }

    public static void showPopScaleCamera(View view) {
        float[] values = new float[]{0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", values),
                ObjectAnimator.ofFloat(view, "scaleY", values));
        set.setDuration(500);
        set.start();
    }

    public static void showListLayoutAnim(ListView view) {
        view.setLayoutAnimation(getAnimationController());
    }

    /**
     * Layout动画
     *
     * @return
     */
    private static LayoutAnimationController getAnimationController() {
        int duration = 500;
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.1f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

    public static LayoutAnimationController getListAlphaController() {
        int duration = 300;
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.1f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

    public static void cancelAnim() {
        if (leftPopAnim != null) {
            leftPopAnim.cancel();
            leftPopAnim = null;
        }
        if (rightPopAnim != null) {
            rightPopAnim.cancel();
            rightPopAnim = null;

        }
    }
}
