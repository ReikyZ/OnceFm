package view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.reikyz.oncefm.R;


/**
 * Created by Administrator on 2015/7/21.
 */
public class CircleProgressBar extends View {

    //主画笔
    Paint mPaint;
    float progress = 0;
    int startDegree = -90;

    int colorWhite = 0xffcbefd7;
    int colorBlue = 0xff77b1c5;
    int pointColorBlue = 0x8877b1c5;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitResources(context, attrs);
    }

    int backgroundColor;
    int progressColor;
    float layoutWidth;
    int textColor;
    float textSize;
    int maxProgress;
    Drawable thumb;
    int thumbSize;

    //初始化属性
    private void InitResources(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

        backgroundColor = typedArray.getColor(R.styleable.CircleProgressBar_backgroundColor, colorWhite);

        progressColor = typedArray.getColor(R.styleable.CircleProgressBar_progressColor, colorBlue);

        layoutWidth = typedArray.getDimension(R.styleable.CircleProgressBar_layout_width, 7);

        textColor = typedArray.getColor(R.styleable.CircleProgressBar_textColor, 0xffada1de);

        textSize = typedArray.getDimension(R.styleable.CircleProgressBar_textSize, 50);

        maxProgress = typedArray.getInt(R.styleable.CircleProgressBar_max, 100);
        //当前进度
        progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, 0.0f);

        thumb = typedArray.getDrawable(R.styleable.CircleProgressBar_thumb);

        thumbSize = typedArray.getInt(R.styleable.CircleProgressBar_thumbSize, 30);


        typedArray.recycle();
        //画进度点
        if (thumb == null) {
            Bitmap bitmap = Bitmap.createBitmap(thumbSize, thumbSize, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(pointColorBlue);
            int center = thumbSize / 2;
            int radius = center - 4;
            canvas.drawCircle(center, center, radius, paint);
            thumb = new BitmapDrawable(getResources(), bitmap);

        }
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgressVIew(canvas);
    }

    //画图集合
    private void drawProgressVIew(Canvas canvas) {
        InitOval(canvas);
        drawBackground(canvas);
        drawProgress(canvas);
        drawProgressText(canvas);
    }


    //画进度范围
    RectF oval;

    private void InitOval(Canvas canvas) {
        int center = getWidth() / 2;
        int radius = (int) (center - thumbSize / 2);
        int left_top = center - radius;
        int right_bottom = center + radius;

        if (oval == null) {
            oval = new RectF(left_top, left_top, right_bottom, right_bottom);
        }
    }

    //进度显示文本
    private void drawProgressText(Canvas canvas) {
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL);
        String progressText = progress + "%";
        float text_left = (getWidth() - mPaint.measureText(progressText)) / 2;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float text_top = (float) (getHeight() / 2 + Math.ceil(fontMetrics.descent - fontMetrics.ascent) / 2);
        canvas.drawText(progressText, text_left, text_top, mPaint);
    }


    //画进度
    private void drawProgress(Canvas canvas) {
        mPaint.setColor(progressColor);
        mPaint.setStrokeWidth(layoutWidth);
        float seek = 0;
        if (maxProgress > 0) {
            seek = (float) progress / maxProgress * 360;
        }
        canvas.drawArc(oval, startDegree, seek, false, mPaint);
        canvas.save();

        int center = getWidth() / 2;
        int radius = (center - thumbSize / 2);

        double cycle_round = (seek + startDegree) * Math.PI / 180;
        float x = (float) (Math.cos(cycle_round) * (radius) + center - thumbSize / 2);
        float y = (float) (Math.sin(cycle_round) * (radius) + center - thumbSize / 2);

        thumb.setBounds(0, 0, thumbSize, thumbSize);
        canvas.translate(x, y);
        thumb.draw(canvas);
        canvas.restore();
    }

    //画背景
    private void drawBackground(Canvas canvas) {
        mPaint.setStrokeWidth(layoutWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(backgroundColor);
        canvas.drawArc(oval, startDegree, 360, false, mPaint);
    }


    //设置进度方法
    public synchronized void setProgress(float progress) {
        if (progress > maxProgress) {
            progress = maxProgress;
        }

        this.progress = progress;
        postInvalidate();
    }

    public int getProgress() {
        int temp = (int) progress;
        return temp;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (thumb != null) {
            thumb.setCallback(null);
            thumb = null;
        }
    }
}
