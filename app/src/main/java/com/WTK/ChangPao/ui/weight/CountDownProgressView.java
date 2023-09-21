package com.WTK.ChangPao.ui.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

import com.WTK.ChangPao.R;

public class CountDownProgressView extends AppCompatTextView {
    //TODO 圆实心的颜色
    private int circSolidColor;

    //TODO 圆边框的颜色
    private int circFrameColor;

    //TODO 圆边框的宽度
    private int circFrameWidth = 4;

    //TODO 圆的半径
    private int circRadius;

    //TODO 进度条的颜色
    private int progressColor;

    //TODO 进度条的宽度
    private int progressWidth = 4;

    //TODO 文字的颜色
    private int textColor;


    private Rect mBounds;
    private Paint mPaint;
    private RectF mArcRectF;

    private int mCenterX;
    private int mCenterY;

    private String mText = "SKIP";


    //TODO 进度倒计时时间
    private long timeMillis = 3000;

    //TODO 进度条通知
    private OnProgressListener mProgressListener;

    //TODO 进度默认100
    private int progress = 100;

    //TODO 进度条类型
    private ProgressType mProgressType = ProgressType.COUNT_BACK;


    public enum ProgressType {

        COUNT,


        COUNT_BACK;
    }


    public void setProgressType(ProgressType progressType) {
        this.mProgressType = progressType;
        resetProgress();

        invalidate();
    }


    public interface OnProgressListener {


        void onProgress(int progress);
    }

    public CountDownProgressView(Context context) {
        super(context);
        init();
    }


    public CountDownProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownProgress);
        if (typedArray != null) {

            if (typedArray.hasValue(R.styleable.CountDownProgress_circSolidColor)) {

                circSolidColor = typedArray.getColor(R.styleable.CountDownProgress_circSolidColor, 0);
            } else {

                circSolidColor = typedArray.getColor(R.styleable.CountDownProgress_circSolidColor, Color.parseColor("#ffffff"));
            }

            //TODO 园边框颜色属性
            if (typedArray.hasValue(R.styleable.CountDownProgress_circFrameColor)) {

                circFrameColor = typedArray.getColor(R.styleable.CountDownProgress_circFrameColor, 0);
            } else {

                circFrameColor = typedArray.getColor(R.styleable.CountDownProgress_circFrameColor, Color.parseColor("#A9A9A9"));
            }

            //TODO 文字颜色属性
            if (typedArray.hasValue(R.styleable.CountDownProgress_ProgressText_color)) {

                textColor = typedArray.getColor(R.styleable.CountDownProgress_ProgressText_color, 0);
            } else {

                textColor = typedArray.getColor(R.styleable.CountDownProgress_ProgressText_color, Color.parseColor("#A9A9A9"));
            }

            //TODO 进度条颜色属性
            if (typedArray.hasValue(R.styleable.CountDownProgress_progressColor)) {

                progressColor = typedArray.getColor(R.styleable.CountDownProgress_progressColor, 0);
            } else {

                progressColor = typedArray.getColor(R.styleable.CountDownProgress_progressColor, Color.parseColor("#ff0080"));
            }


            typedArray.recycle();
        }

    }

    /**
     * 初始化画笔画布等
     */
    public void init() {
        mPaint = new Paint();
        mBounds = new Rect();
        mArcRectF = new RectF();
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width > height) {
            height = width;
        } else {
            width = height;
        }
        circRadius = width / 2;
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getDrawingRect(mBounds);

        mCenterX = mBounds.centerX();
        mCenterY = mBounds.centerY();

        //TODO 画实心圆
        mPaint.setAntiAlias(true); //设置抗锯齿
        mPaint.setStyle(Paint.Style.FILL); //实心填充style
        mPaint.setColor(circSolidColor);
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), circRadius, mPaint);


        //TODO 画外边框(空心圆,即园边框)
        mPaint.setAntiAlias(true);//设置抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);//空心style
        mPaint.setStrokeWidth(circFrameWidth);//设置空心线宽度
        mPaint.setColor(circFrameColor);
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), circRadius - circFrameWidth, mPaint);

        //TODO 画文字
        Paint text_paint = getPaint();
        text_paint.setColor(textColor);
        text_paint.setAntiAlias(true);
        text_paint.setTextAlign(Paint.Align.CENTER);
        float textY = mCenterY - (text_paint.descent() + text_paint.ascent()) / 2;
        canvas.drawText(mText, mCenterX, textY, text_paint);


        //TODO 画进度条
        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcRectF.set(mBounds.left + progressWidth, mBounds.top + progressWidth, mBounds.right - progressWidth, mBounds.bottom - progressWidth);
        canvas.drawArc(mArcRectF, -90, 360 * progress / 100, false, mPaint); //这里的-90.是方向，大家可以改成0，90，180，-90等就可以看到效果区别


    }


    /**
     * 开始。
     */
    public void start() {
        stop();
        post(progressChangeTask);
    }

    /**
     * 停止。
     */
    public void stop() {
        removeCallbacks(progressChangeTask);
    }

    /**
     * 重新开始。
     */
    public void reStart() {
        resetProgress();
        start();
    }


    /**
     * 重置进度。
     */
    private void resetProgress() {
        switch (mProgressType) {
            case COUNT:
                progress = 0;
                break;
            case COUNT_BACK:
                progress = 100;
                break;
        }
    }

    /**
     * 进度更新task
     */
    private Runnable progressChangeTask = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            switch (mProgressType) {
                case COUNT:
                    progress += 1;
                    break;
                case COUNT_BACK:
                    progress -= 1;
                    break;
            }
            if (progress >= 0 && progress <= 100) {
                if (mProgressListener != null)
                    mProgressListener.onProgress(progress);
                invalidate();
                postDelayed(progressChangeTask, timeMillis / 60);
            } else
                progress = validateProgress(progress);
        }
    };

    private int validateProgress(int progress) {
        if (progress > 100)
            progress = 100;
        else if (progress < 0)
            progress = 0;
        return progress;
    }



    public void setText(String text) {
        this.mText = text;
    }



    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
        invalidate();
    }


    public void setProgressListener(OnProgressListener mProgressListener) {
        this.mProgressListener = mProgressListener;
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (Math.abs(x - (mBounds.centerX())) <= (circRadius) * 2 && Math.abs(y - (mBounds.centerY())) <= (circRadius) * 2) {
                }
                break;
            default:
        }
        return super.onTouchEvent(event);
    }
}
