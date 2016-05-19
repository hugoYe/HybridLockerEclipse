package com.cooeeui.nanolock;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cooeeui.lock.core.utilities.AnimUtils;
import com.cooeeui.lock.nanolock.R;


public class ControlCenterFrame extends FrameLayout {

    public static final float THRESHOLD_CIRCLE = 0.15f;
    //public static final float THRESHOLD_AUTO_SCROLL = 0.3f;
    public static final float THRESHOLD_FIRE_SCROLL = 0.23f;
    private int scrollX = 0;
    private int scrollDistance = 0;
    private int height = 0;
    private int width = 0;

    protected Animator mEnterAnim = null;
    protected Animator mExitAnim = null;

    private ImageView mBack;


    private PaintFlagsDrawFilter pdf =
        new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Paint paint = new Paint();

    {
        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
    }

    private PorterDuffXfermode xfermode = new PorterDuffXfermode(Mode.DST_IN);
    private PorterDuffXfermode xfermodeClear = new PorterDuffXfermode(Mode.CLEAR);
    private Path path = new Path();
    protected Bitmap mBmpCache = null;

    public ControlCenterFrame(Context context) {
        super(context);

    }

    public ControlCenterFrame(Context context, AttributeSet set) {
        super(context, set);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBack = (ImageView) findViewById(R.id.iv_back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exitControlCenter();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = this.getHeight();
        width = this.getWidth();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        height = getHeight();
        width = getWidth();
        if (scrollDistance >= (int) (THRESHOLD_CIRCLE * height) + height) {
            super.dispatchDraw(canvas);
            if (mBmpCache != null && !mBmpCache.isRecycled()) {
                mBmpCache.recycle();
                mBmpCache = null;
            }
        } else {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            if (mBmpCache == null || mBmpCache.getWidth() != viewWidth
                || mBmpCache.getHeight() != viewHeight) {
                if (mBmpCache != null && !mBmpCache.isRecycled()) {
                    mBmpCache.recycle();
                }
                mBmpCache = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
                Canvas bmpcanvas = new Canvas(mBmpCache);
                super.dispatchDraw(bmpcanvas);
            }

            int layer = canvas.save();
            canvas.setDrawFilter(pdf);
            canvas.drawBitmap(mBmpCache, 0, 0, null);
            paint.setXfermode(xfermodeClear);
            canvas.drawRect(0, 0, width, height - scrollDistance, paint);
            paint.setXfermode(xfermode);
            drawCover(canvas, true);
            paint.setXfermode(null);
            drawCover(canvas, false);
            canvas.restoreToCount(layer);
        }
    }

    // hugo modified
//    public void drawCover(Canvas canvas, boolean mask) {
//        if (scrollDistance <= THRESHOLD_CIRCLE * height) {
//            paint.setARGB(255, 255, 255, 255);
//            path.reset();// 重置path
//            // 贝赛尔曲线的起始点
//            path.moveTo(0, height);
//            // 设置贝赛尔曲线的操作点以及终止点
//            path.quadTo(width / 4, height - scrollDistance, width/2, height);
//            path.moveTo(0, height);
//            // 绘制贝赛尔曲线（Path）
//            canvas.drawPath(path, paint);
//        } else {
//            int tmp = (int) (scrollDistance - THRESHOLD_CIRCLE * height);
//            paint.setARGB(mask ? 255 : (int) ((float) (height - tmp) / height * 255), 255, 255,
//                          255);
//            //paint.setARGB( (int)( (float)tmp/height*255 ) , 255 , 255 , 255 );
//            path.reset();// 重置path
//            // 贝赛尔曲线的起始点
//            path.moveTo(0, height - tmp);
//            // 设置贝赛尔曲线的操作点以及终止点
//            path.quadTo(width / 2, height - scrollDistance, width, height - tmp);
//            path.addRect(0, height - tmp, width, height, Direction.CW);
//            // 绘制贝赛尔曲线（Path）
//            canvas.drawPath(path, paint);
//        }
//    }

    public void drawCover(Canvas canvas, boolean mask) {
        if (scrollDistance <= THRESHOLD_CIRCLE * height) {
            paint.setARGB(255, 255, 255, 255);
            path.reset();// 重置path
            // 贝赛尔曲线的起始点
            path.moveTo(0, height);
            // 设置贝赛尔曲线的操作点以及终止点
            path.quadTo(width / 2, height - scrollDistance, width, height);
            path.moveTo(0, height);
            // 绘制贝赛尔曲线（Path）
            canvas.drawPath(path, paint);
        } else {
            int tmp = (int) (scrollDistance - THRESHOLD_CIRCLE * height);
            paint.setARGB(mask ? 255 : (int) ((float) (height - tmp) / height * 255), 255, 255,
                          255);
            //paint.setARGB( (int)( (float)tmp/height*255 ) , 255 , 255 , 255 );
            path.reset();// 重置path
            // 贝赛尔曲线的起始点
            path.moveTo(0, height - tmp);
            // 设置贝赛尔曲线的操作点以及终止点
            path.quadTo(width / 2, height - scrollDistance, width, height - tmp);
            path.addRect(0, height - tmp, width, height, Direction.CW);
            // 绘制贝赛尔曲线（Path）
            canvas.drawPath(path, paint);
        }
    }


    public void showControlCenter() {
        if (isShown()) {
            return;
        }
        if (mEnterAnim != null && mEnterAnim.isRunning()) {
            return;
        }
        if (mExitAnim != null && mExitAnim.isRunning()) {
            return;
        }
        setVisibility(View.VISIBLE);
        bringToFront();
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }


    public void enterControlCenter() {
        if (mEnterAnim != null && mEnterAnim.isStarted()) {
            return;
        }
        if (mExitAnim != null && mExitAnim.isStarted()) {
            return;
        }
        if (!isShown()) {
            showControlCenter();
        }
        //step2 播放动画
        mEnterAnim = getEnterAnim();
        mEnterAnim.addListener(new AnimatorListenerAdapter() {

            private void resetView() {
                setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                resetView();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                resetView();
            }
        });
        mEnterAnim.start();
    }

    public void exitControlCenter() {
        if (isShown() == false) {
            return;
        }
        if (mEnterAnim != null && mEnterAnim.isRunning()) {
            return;
        }
        if (mExitAnim != null && mExitAnim.isRunning()) {
            return;
        }
        //step2 播放动画
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mExitAnim = getExitAnim();
        mExitAnim.addListener(new AnimatorListenerAdapter() {

            private void resetView() {
                setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationEnd(
                Animator animation) {
                // TODO Auto-generated method stub
                resetView();
                onExitControlCenter();
            }

            @Override
            public void onAnimationCancel(
                Animator animation) {
                // TODO Auto-generated method stub
                resetView();
                onExitControlCenter();
            }
        });
        mExitAnim.start();

    }

    protected void onExitControlCenter() {
        setVisibility(View.GONE);
    }

    public void updateScrollDistance(int x, int distance) {
        height = getHeight();
        width = getWidth();
        if (scrollDistance >= THRESHOLD_FIRE_SCROLL * height) {
            return;
        }
        scrollX = x;
        scrollDistance = distance;
        invalidate();
        if (distance >= THRESHOLD_FIRE_SCROLL * height) {
            enterControlCenter();
        }
    }

    public boolean onScrollFinish() {
        if (scrollDistance < THRESHOLD_FIRE_SCROLL * height && scrollDistance >= 0) {
            return false;
        }
        return true;
    }

    public Animator getEnterAnim() {
        int duration = 500;
        AnimatorSet animSet = AnimUtils.createAnimatorSet();
        animSet.setDuration(duration);
        ValueAnimator conAni = ValueAnimator.ofInt(scrollDistance,
                                                   (int) (THRESHOLD_CIRCLE * height) + height);
        conAni.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(
                ValueAnimator animation) {
                // TODO Auto-generated method stub
                int distance = (Integer) animation.getAnimatedValue();
                scrollDistance = distance;
                invalidate();
            }
        });
        animSet.play(conAni);

        return animSet;
    }


    public Animator getExitAnim() {
        int duration = 500;
        AnimatorSet animSet = AnimUtils.createAnimatorSet();
        animSet.setDuration(duration);
        ValueAnimator conAni = ValueAnimator.ofInt(scrollDistance, 0);
        conAni.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(
                ValueAnimator animation) {
                // TODO Auto-generated method stub
                int distance = (Integer) animation.getAnimatedValue();
                scrollDistance = distance;
                invalidate();
                if (scrollDistance == 0 && mBmpCache != null && !mBmpCache.isRecycled()) {
                    mBmpCache.recycle();
                    mBmpCache = null;
                }
            }
        });
        animSet.play(conAni);

        return animSet;
    }
}
