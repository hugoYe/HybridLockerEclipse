package com.cooeeui.nanolock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cooeeui.lock.core.api.LockViewManager;
import com.cooeeui.lock.core.blur.GlassView;
import com.cooeeui.lock.core.utilities.AnimUtils;
import com.cooeeui.lock.nanolock.R;
import com.cooeeui.nanolock.systemkeyguard.KeyguardBottomAreaView;

/**
 * Created by user on 2015/12/2.
 */
public class LockFrameContainer extends RelativeLayout {

    private static final String TAG = LockFrameContainer.class.getSimpleName();

    private Context mContext;
    private Handler mHandler = new Handler();

    private ImageView mTimeWeather;
    private AnimatorSet mUnlockAnim;
    private GlassView mGlassView;
    private KeyguardBottomAreaView mkeyguardBottomAreaView;
    private float mUnlockDisMax;
    private float mUnlockDis;
    private float mOffset;
    private boolean mUnlocked;

    private ControlCenterFrame mControlCenter;
    private boolean hasScrollH = false;
    private boolean hasScrollV = false;
    private int touchSlop = 10;
    public static int MAX_TOUCH_SLOP = 10;
    //监控触摸事件
    private int downX = -100;
    private int downY = -100;
    private int scrollDistance = 0;


    public LockFrameContainer(Context context) {
        super(context);
        this.mContext = context;
    }

    public LockFrameContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public LockFrameContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        touchSlop = (int) (configuration.getScaledPagingTouchSlop() * 0.2);
        touchSlop = touchSlop > MAX_TOUCH_SLOP ? MAX_TOUCH_SLOP : touchSlop;

        mUnlockDisMax = getResources().getDimensionPixelOffset(R.dimen.unlock_distance_max);
        mUnlockDis = getResources().getDimension(R.dimen.unlock_distance);
        initLockview();

    }

    private void initLockview() {

        mTimeWeather = (ImageView) findViewById(R.id.iv_time_weather);
        mGlassView = (GlassView) findViewById(R.id.glass_view);
        mkeyguardBottomAreaView = (KeyguardBottomAreaView) findViewById(R.id.keyguard_bottom_area);
        mkeyguardBottomAreaView.setupBluredView(mGlassView);

//        mControlCenter = (ControlCenterFrame) findViewById(R.id.control_center);

        // blur 1 begin
        //Give the blurring view a reference to the blurred view.
//        ImageView imageView = (ImageView) mLockView.findViewById(R.id.iv_test_temp);
//        mBlurringView = (BlurringView) mLockView.findViewById(R.id.blurring_view);
//        mBlurringView.setBlurredView(imageView);
        // blur 1 end

        // temp test begin

//        mWebView = (WebView) mLockView.findViewById(R.id.wv_webview);
//        initWebViewSettings();
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//            }
//        });
//        mWebView.loadUrl("http://baidu.com"); // 联网,加载html
//
//        mGotoOtherActivity = (Button) mLockView.findViewById(R.id.btn_startActivity);
//        mGotoOtherActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(mContext, WallpaperActivity.class);
//                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(it);
//                mWindowManager.removeViewImmediate(mLockView);
//                stopSelf();
//            }
//        });

        // temp test end
    }

    private void clearScroll(int x, int y) {
        downX = x;
        downY = y;
        hasScrollH = false;
        hasScrollV = false;
    }

    public void updateScroll(int x, int y) {
        scrollDistance = (int) ((downY - y) * 1.5);
        if (scrollDistance < 0) {
            scrollDistance = 0;
        }
        if (mControlCenter.isShown()) {
            mControlCenter.updateScrollDistance(x, scrollDistance);
        }
    }

    private boolean determineMove(int x, int y) {
        if (downX == -100 || downY == -100) {
            return false;
        }
        if (!hasScrollH && !hasScrollV) {
            int scrollX = Math.abs(x - downX);
            int scrollY = downY - y;
            if (scrollX > touchSlop || scrollY > touchSlop) {
                if (scrollX >= scrollY) {
                    hasScrollH = true;
                } else {
                    downY = y;
                    hasScrollV = true;
//                    mControlCenter.showControlCenter();
//                    updateScroll(x, y);
                    unlockAnim(x, y);
                    return true;
                }
            }
        } else {
            return true;
        }

        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e(TAG, "####### onInterceptTouchEvent ev.getAction = " + ev.getAction());
//        Log.e(TAG, "######## onInterceptTouchEvent ev.x = " + ev.getX() + ", ev.y = " + ev.getY());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e(TAG, "####### onTouchEvent ev.getAction = " + event.getAction());
//        Log.e(TAG, "######## onTouchEvent ev.x = " + event.getX() + ", ev.y = " + event.getY());

        boolean res = false;
        if (event.getPointerId(event.getActionIndex()) != 0) {
            return res;
        }
        final int action = event.getAction();
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        determineMove(x, y);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                clearScroll(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (hasScrollV) {
//                    updateScroll(x, y);
                    unlockAnim(x, y);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                clearScroll(-100, -100);
                autoUnlockAnim();
//                if (!mControlCenter.onScrollFinish()) {
//                    mControlCenter.exitControlCenter();
//                }
                break;
        }
        return res;
    }

    private void unlockAnim(int x, int y) {
        if (mUnlockAnim != null && mUnlockAnim.isRunning()) {
            mUnlockAnim.cancel();
        }
        mOffset = downY - y;
        if (mOffset < 0) {
            mOffset = 0;
        }
        float alpha = 1 - (mOffset / mUnlockDisMax);
        if (alpha <= 0) {
            alpha = 0;
        }
        float scale = 1 - (mOffset / mUnlockDisMax);
        if (scale <= 0) {
            scale = 0;
        }
        mTimeWeather.setScaleX(scale);
        mTimeWeather.setScaleY(scale);
        mTimeWeather.setAlpha(alpha);
    }

    private void autoUnlockAnim() {
        if (mUnlockAnim != null && mUnlockAnim.isRunning()) {
            return;
        }
        int duration = 500;
        if (mUnlockAnim == null) {
            mUnlockAnim = AnimUtils.createAnimatorSet();
        }

        mUnlockAnim.setDuration(duration);
        ValueAnimator alphaAnim = null;

        if (mOffset > mUnlockDis) {
            mUnlocked = true;

            alphaAnim = ValueAnimator.ofFloat(mTimeWeather.getAlpha(), 0);

        } else {
            mUnlocked = false;

            alphaAnim = ValueAnimator.ofFloat(mTimeWeather.getAlpha(), 1);
        }

        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // TODO Auto-generated method stub
                float value = (Float) animation.getAnimatedValue();
                mTimeWeather.setAlpha(value);
                mTimeWeather.setScaleX(value);
                mTimeWeather.setScaleY(value);
                invalidate();
            }
        });

        mUnlockAnim.play(alphaAnim);

        mUnlockAnim.start();

        mUnlockAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mUnlocked) {
                    mkeyguardBottomAreaView.controlCenterManager.onDestroy();
                    LockViewManager.getInstance().unlock(getContext());
                } else {

                    mTimeWeather.setAlpha(1.0f);
                    mTimeWeather.setScaleX(1.0f);
                    mTimeWeather.setScaleY(1.0f);

                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

//    private void initWebViewSettings() {
//        mWebView.setInitialScale(0);
//        mWebView.setVerticalScrollBarEnabled(false);
//        // Enable JavaScript
//        final WebSettings settings = mWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//
//        // Set the nav dump for HTC 2.x devices (disabling for ICS, deprecated
//        // entirely for Jellybean 4.2)
//        try {
//            Method gingerbread_getMethod = WebSettings.class.getMethod(
//                "setNavDump", new Class[]{boolean.class});
//
//            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB
//                && android.os.Build.MANUFACTURER.contains("HTC")) {
//                gingerbread_getMethod.invoke(settings, true);
//            }
//        } catch (NoSuchMethodException e) {
//            Log.d(TAG,
//                  "We are on a modern version of Android, we will deprecate HTC 2.3 devices in 2.8");
//        } catch (IllegalArgumentException e) {
//            Log.d(TAG, "Doing the NavDump failed with bad arguments");
//        } catch (IllegalAccessException e) {
//            Log.d(TAG,
//                  "This should never happen: IllegalAccessException means this isn't Android anymore");
//        } catch (InvocationTargetException e) {
//            Log.d(TAG,
//                  "This should never happen: InvocationTargetException means this isn't Android anymore.");
//        }
//
//        // We don't save any form data in the application
//        settings.setSaveFormData(false);
//        settings.setSavePassword(false);
//
//        // Jellybean rightfully tried to lock this down. Too bad they didn't
//        // give us a whitelist
//        // while we do this
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            settings.setAllowUniversalAccessFromFileURLs(true);
//        }
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            settings.setMediaPlaybackRequiresUserGesture(false);
//        }
//
//        // Enable DOM storage
//        settings.setDomStorageEnabled(true);
//
//        // Enable built-in geolocation
//        settings.setGeolocationEnabled(true);
//
//    }


}
