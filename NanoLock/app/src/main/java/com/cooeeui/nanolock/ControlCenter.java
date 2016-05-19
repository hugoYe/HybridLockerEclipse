package com.cooeeui.nanolock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cooeeui.lock.nanolock.R;
import com.cooeeui.nanolock.systemkeyguard.KeyguardBottomAreaView;

/**
 * Created by user on 2015/12/16.
 */
public class ControlCenter extends LinearLayout {

    private final String TAG = ControlCenter.class.getSimpleName();

    private ImageView mBack;
    private KeyguardBottomAreaView mkeyguardBottomAreaView;

    public ControlCenter(Context context) {
        super(context);
    }

    public ControlCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlCenter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBack = (ImageView) findViewById(R.id.iv_back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mkeyguardBottomAreaView != null) {
                    mkeyguardBottomAreaView.afforanceHelper.reset(true);
                    mkeyguardBottomAreaView.glassViewBlurAnim(false);
                }
            }
        });

    }

    public void setKeyguardBottomAreaView(KeyguardBottomAreaView view) {
        mkeyguardBottomAreaView = view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e(TAG, "######## onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e(TAG, "######## onTouchEvent");
        return true;
    }
}
