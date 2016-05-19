/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.cooeeui.nanolock.systemkeyguard;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.MediaStore;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cooeeui.controlcenter.api.ControlCenterManager;
import com.cooeeui.cordova.plugins.UnlockListener;
import com.cooeeui.lock.core.api.LockViewManager;
import com.cooeeui.lock.core.blur.GlassView;
import com.cooeeui.lock.nanolock.R;
import com.cooeeui.nanolock.ControlCenter;
import com.cooeeui.nanolock.News;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;

/**
 * Implementation for the bottom area of the Keyguard, including camera/phone affordance and status
 * text.
 */
public class KeyguardBottomAreaView extends FrameLayout
    implements View.OnClickListener, View.OnLongClickListener, KeyguardAffordanceHelper.Callback,
               UnlockListener {

    final static String TAG = KeyguardBottomAreaView.class.getSimpleName();

    public static final String CAMERA_LAUNCH_SOURCE_AFFORDANCE = "lockscreen_affordance";
    public static final String CAMERA_LAUNCH_SOURCE_WIGGLE = "wiggle_gesture";
    public static final String CAMERA_LAUNCH_SOURCE_POWER_DOUBLE_TAP = "power_double_tap";

    public static final String EXTRA_CAMERA_LAUNCH_SOURCE
        = "com.android.systemui.camera_launch_source";

    private static final Intent SECURE_CAMERA_INTENT =
        new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE)
            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    public static final Intent INSECURE_CAMERA_INTENT =
        new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
    private static final Intent PHONE_INTENT = new Intent(Intent.ACTION_DIAL);
    private static final int DOZE_ANIMATION_STAGGER_DELAY = 48;
    private static final int DOZE_ANIMATION_ELEMENT_DURATION = 250;

    private KeyguardAffordanceView mCameraImageView;
    private KeyguardAffordanceView mLeftAffordanceView;
    private LockIcon mLockIcon;
    private TextView mIndicationText;
    private ViewGroup mPreviewContainer;
    private View mLeftPreview;
    private View mRightPreview;
    private View mCameraPreview;
    private GlassView mGlassView;
    private ValueAnimator mGlassViewBlurAnim;

    public KeyguardAffordanceHelper afforanceHelper;
    private boolean mHintAnimationRunning;

    private ActivityStarter mActivityStarter;
//    private UnlockMethodCache mUnlockMethodCache;
//    private LockPatternUtils mLockPatternUtils;
//    private FlashlightController mFlashlightController;
//    private PreviewInflater mPreviewInflater;
//    private KeyguardIndicationController mIndicationController;
//    private AccessibilityController mAccessibilityController;
//    private PhoneStatusBar mPhoneStatusBar;

    private final Interpolator mLinearOutSlowInInterpolator;
    private boolean mUserSetupComplete;
    private boolean mPrewarmBound;
    private Messenger mPrewarmMessenger;
    private final ServiceConnection mPrewarmConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPrewarmMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPrewarmMessenger = null;
        }
    };

    private boolean mLeftIsVoiceAssist;
//    private AssistManager mAssistManager;


    public ControlCenterManager controlCenterManager;


    public KeyguardBottomAreaView(Context context) {
        this(context, null);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLinearOutSlowInInterpolator = PathInterpolatorCompat.create(0, 0, 0.2f, 1);
//        mLinearOutSlowInInterpolator =
//            AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in);
    }

//    public KeyguardBottomAreaView(Context context, AttributeSet attrs, int defStyleAttr,
//                                  int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//
//    }

    private AccessibilityDelegate mAccessibilityDelegate = new AccessibilityDelegate() {
        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            String label = null;
            if (host == mLockIcon) {
                label = getResources().getString(R.string.unlock_label);
            } else if (host == mCameraImageView) {
                label = getResources().getString(R.string.camera_label);
            } else if (host == mLeftAffordanceView) {
                if (mLeftIsVoiceAssist) {
                    label = getResources().getString(R.string.voice_assist_label);
                } else {
                    label = getResources().getString(R.string.phone_label);
                }
            }
//            info.addAction(new AccessibilityAction(ACTION_CLICK, label));
        }

        @Override
        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            if (action == ACTION_CLICK) {
                if (host == mLockIcon) {
//                    mPhoneStatusBar.animateCollapsePanels(
//                        CommandQueue.FLAG_EXCLUDE_RECENTS_PANEL, true /* force */);
                    return true;
                } else if (host == mCameraImageView) {
                    launchCamera(CAMERA_LAUNCH_SOURCE_AFFORDANCE);
                    return true;
                } else if (host == mLeftAffordanceView) {
                    launchLeftAffordance();
                    return true;
                }
            }
            return super.performAccessibilityAction(host, action, args);
        }
    };


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        mLockPatternUtils = new LockPatternUtils(mContext);
//        mPreviewContainer = (ViewGroup) findViewById(R.id.preview_container);

        mCameraImageView = (KeyguardAffordanceView) findViewById(R.id.camera_button);
        mLeftAffordanceView = (KeyguardAffordanceView) findViewById(R.id.left_button);
        mLockIcon = (LockIcon) findViewById(R.id.lock_icon);
//        mIndicationText = (TextView) findViewById(R.id.keyguard_indication_text);
//        watchForCameraPolicyChanges();
        updateCameraVisibility();

//        mLockIcon.update();
        setClipChildren(false);
        setClipToPadding(false);
        inflateCameraPreview();
        mLockIcon.setOnClickListener(this);
        mLockIcon.setOnLongClickListener(this);
        mCameraImageView.setOnClickListener(this);
        mLeftAffordanceView.setOnClickListener(this);
//        initAccessibility();

        mLeftPreview = findViewById(R.id.control_center);
        ((ControlCenter) mLeftPreview).setKeyguardBottomAreaView(this);
        controlCenterManager = new ControlCenterManager(getContext());
        View controlview = controlCenterManager.getControlCenter();
        ((ControlCenter) mLeftPreview).addView(controlview);
        controlview.setBackgroundColor(Color.TRANSPARENT);
        controlCenterManager.setUnlockListener(this);
        mRightPreview = findViewById(R.id.nano_news);
        ((News) mRightPreview).setKeyguardBottomAreaView(this);

        afforanceHelper = new KeyguardAffordanceHelper(this, getContext());
    }


    public void setupBluredView(GlassView glassView) {
        mGlassView = glassView;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e(TAG, "####### onInterceptTouchEvent ev.getAction = " + ev.getAction());
//        Log.e(TAG, "######## onInterceptTouchEvent ev.x = " + ev.getX() + ", ev.y = " + ev.getY());
        boolean intercept = afforanceHelper.onInterceptTouchEvent(ev);
        if (intercept) {
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e(TAG, "####### onTouchEvent ev.getAction = " + event.getAction());
//        Log.e(TAG, "######## onTouchEvent ev.x = " + event.getX() + ", ev.y = " + event.getY());

        return afforanceHelper.onTouchEvent(event);
//        return super.onTouchEvent(event);
    }

    private void initAccessibility() {
        mLockIcon.setAccessibilityDelegate(mAccessibilityDelegate);
        mLeftAffordanceView.setAccessibilityDelegate(mAccessibilityDelegate);
        mCameraImageView.setAccessibilityDelegate(mAccessibilityDelegate);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        int indicationBottomMargin = getResources().getDimensionPixelSize(
//            R.dimen.keyguard_indication_margin_bottom);
//        MarginLayoutParams mlp = (MarginLayoutParams) mIndicationText.getLayoutParams();
//        if (mlp.bottomMargin != indicationBottomMargin) {
//            mlp.bottomMargin = indicationBottomMargin;
//            mIndicationText.setLayoutParams(mlp);
//        }

        // Respect font size setting.
//        mIndicationText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                                    getResources().getDimensionPixelSize(
//                                        com.android.internal.R.dimen.text_size_small_material));
    }

    public void setActivityStarter(ActivityStarter activityStarter) {
        mActivityStarter = activityStarter;
    }

//    public void setFlashlightController(FlashlightController flashlightController) {
//        mFlashlightController = flashlightController;
//    }

//    public void setAccessibilityController(AccessibilityController accessibilityController) {
//        mAccessibilityController = accessibilityController;
//        mLockIcon.setAccessibilityController(accessibilityController);
//        accessibilityController.addStateChangedCallback(this);
//    }

//    public void setPhoneStatusBar(PhoneStatusBar phoneStatusBar) {
//        mPhoneStatusBar = phoneStatusBar;
//        updateCameraVisibility(); // in case onFinishInflate() was called too early
//    }

    public void setUserSetupComplete(boolean userSetupComplete) {
        mUserSetupComplete = userSetupComplete;
        updateCameraVisibility();
        updateLeftAffordanceIcon();
    }

    private Intent getCameraIntent() {
//        KeyguardUpdateMonitor updateMonitor = KeyguardUpdateMonitor.getInstance(mContext);
//        boolean canSkipBouncer = updateMonitor.getUserCanSkipBouncer(
//            KeyguardUpdateMonitor.getCurrentUser());
//        boolean secure = mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser());
//        return (secure && !canSkipBouncer) ? SECURE_CAMERA_INTENT : INSECURE_CAMERA_INTENT;
        return null;
    }

    /**
     * Resolves the intent to launch the camera application.
     */
    public ResolveInfo resolveCameraIntent() {
//        return mContext.getPackageManager().resolveActivityAsUser(getCameraIntent(),
//                                                                  PackageManager.MATCH_DEFAULT_ONLY,
//                                                                  KeyguardUpdateMonitor
//                                                                      .getCurrentUser());
        return null;
    }

    private void updateCameraVisibility() {
        if (mCameraImageView == null) {
            // Things are not set up yet; reply hazy, ask again later
            return;
        }
//        ResolveInfo resolved = resolveCameraIntent();
//        boolean visible = !isCameraDisabledByDpm() && resolved != null
//                          && getResources().getBoolean(R.bool.config_keyguardShowCameraAffordance)
//                          && mUserSetupComplete;
//        mCameraImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void updateLeftAffordanceIcon() {
        mLeftIsVoiceAssist = canLaunchVoiceAssist();
        int drawableId;
        int contentDescription;
        boolean visible = mUserSetupComplete;
//        if (mLeftIsVoiceAssist) {
//            drawableId = R.drawable.ic_mic_26dp;
//            contentDescription = R.string.accessibility_voice_assist_button;
//        } else {
//            visible &= isPhoneVisible();
//            drawableId = R.drawable.ic_phone_24dp;
//            contentDescription = R.string.accessibility_phone_button;
//        }
        mLeftAffordanceView.setVisibility(visible ? View.VISIBLE : View.GONE);
//        mLeftAffordanceView.setImageDrawable(mContext.getDrawable(drawableId));
//        mLeftAffordanceView.setContentDescription(mContext.getString(contentDescription));
    }

    public boolean isLeftVoiceAssist() {
        return mLeftIsVoiceAssist;
    }

//    private boolean isPhoneVisible() {
//        PackageManager pm = mContext.getPackageManager();
//        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
//               && pm.resolveActivity(PHONE_INTENT, 0) != null;
//    }

    private boolean isCameraDisabledByDpm() {
        final DevicePolicyManager dpm =
            (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
//        if (dpm != null && mPhoneStatusBar != null) {
//            try {
//                final int userId = ActivityManagerNative.getDefault().getCurrentUser().id;
//                final int disabledFlags = dpm.getKeyguardDisabledFeatures(null, userId);
//                final boolean disabledBecauseKeyguardSecure =
//                    (disabledFlags & DevicePolicyManager.KEYGUARD_DISABLE_SECURE_CAMERA) != 0
//                    && mPhoneStatusBar.isKeyguardSecure();
//                return dpm.getCameraDisabled(null) || disabledBecauseKeyguardSecure;
//            } catch (RemoteException e) {
//                Log.e(TAG, "Can't get userId", e);
//            }
//        }
        return false;
    }

    private void watchForCameraPolicyChanges() {
//        final IntentFilter filter = new IntentFilter();
//        filter.addAction(DevicePolicyManager.ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED);
//        getContext().registerReceiverAsUser(mDevicePolicyReceiver,
//                                            UserHandle.ALL, filter, null, null);
//        KeyguardUpdateMonitor.getInstance(mContext).registerCallback(mUpdateMonitorCallback);
    }

    public void onStateChanged(boolean accessibilityEnabled, boolean touchExplorationEnabled) {
        mCameraImageView.setClickable(touchExplorationEnabled);
        mLeftAffordanceView.setClickable(touchExplorationEnabled);
        mCameraImageView.setFocusable(accessibilityEnabled);
        mLeftAffordanceView.setFocusable(accessibilityEnabled);
        mLockIcon.update();
    }

    @Override
    public void onClick(View v) {
        if (v == mCameraImageView) {
            launchCamera(CAMERA_LAUNCH_SOURCE_AFFORDANCE);
        } else if (v == mLeftAffordanceView) {
            launchLeftAffordance();
        }
        if (v == mLockIcon) {
//            if (!mAccessibilityController.isAccessibilityEnabled()) {
//                handleTrustCircleClick();
//            } else {
//                mPhoneStatusBar.animateCollapsePanels(
//                    CommandQueue.FLAG_EXCLUDE_NONE, true /* force */);
//            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        handleTrustCircleClick();
        return true;
    }

    private void handleTrustCircleClick() {
//        EventLogTags.writeSysuiLockscreenGesture(
//            EventLogConstants.SYSUI_LOCKSCREEN_GESTURE_TAP_LOCK, 0 /* lengthDp - N/A */,
//            0 /* velocityDp - N/A */);
//        mIndicationController.showTransientIndication(
//            R.string.keyguard_indication_trust_disabled);
//        mLockPatternUtils.requireCredentialEntry(KeyguardUpdateMonitor.getCurrentUser());
    }

    public void bindCameraPrewarmService() {
//        Intent intent = getCameraIntent();
//        ActivityInfo targetInfo = PreviewInflater.getTargetActivityInfo(mContext, intent,
//                                                                        KeyguardUpdateMonitor
//                                                                            .getCurrentUser());
//        if (targetInfo != null && targetInfo.metaData != null) {
//            String clazz = targetInfo.metaData.getString(
//                MediaStore.META_DATA_STILL_IMAGE_CAMERA_PREWARM_SERVICE);
//            if (clazz != null) {
//                Intent serviceIntent = new Intent();
//                serviceIntent.setClassName(targetInfo.packageName, clazz);
//                serviceIntent.setAction(CameraPrewarmService.ACTION_PREWARM);
//                try {
//                    if (getContext().bindServiceAsUser(serviceIntent, mPrewarmConnection,
//                                                       Context.BIND_AUTO_CREATE
//                                                       | Context.BIND_FOREGROUND_SERVICE,
//                                                       new UserHandle(UserHandle.USER_CURRENT))) {
//                        mPrewarmBound = true;
//                    }
//                } catch (SecurityException e) {
//                    Log.w(TAG, "Unable to bind to prewarm service package=" + targetInfo.packageName
//                               + " class=" + clazz, e);
//                }
//            }
//        }
    }

    public void unbindCameraPrewarmService(boolean launched) {
//        if (mPrewarmBound) {
//            if (mPrewarmMessenger != null && launched) {
//                try {
//                    mPrewarmMessenger.send(Message.obtain(null /* handler */,
//                                                          CameraPrewarmService.MSG_CAMERA_FIRED));
//                } catch (RemoteException e) {
//                    Log.w(TAG, "Error sending camera fired message", e);
//                }
//            }
//            mContext.unbindService(mPrewarmConnection);
//            mPrewarmBound = false;
//        }
    }

    public void launchCamera(String source) {
//        final Intent intent = getCameraIntent();
//        intent.putExtra(EXTRA_CAMERA_LAUNCH_SOURCE, source);
//        boolean wouldLaunchResolverActivity = PreviewInflater.wouldLaunchResolverActivity(
//            mContext, intent, KeyguardUpdateMonitor.getCurrentUser());
//        if (intent == SECURE_CAMERA_INTENT && !wouldLaunchResolverActivity) {
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    int result = ActivityManager.START_CANCELED;
//                    try {
//                        result = ActivityManagerNative.getDefault().startActivityAsUser(
//                            null, getContext().getBasePackageName(),
//                            intent,
//                            intent.resolveTypeIfNeeded(getContext().getContentResolver()),
//                            null, null, 0, Intent.FLAG_ACTIVITY_NEW_TASK, null, null,
//                            UserHandle.CURRENT.getIdentifier());
//                    } catch (RemoteException e) {
//                        Log.w(TAG, "Unable to start camera activity", e);
//                    }
//                    mActivityStarter.preventNextAnimation();
//                    final boolean launched = isSuccessfulLaunch(result);
//                    post(new Runnable() {
//                        @Override
//                        public void run() {
//                            unbindCameraPrewarmService(launched);
//                        }
//                    });
//                }
//            });
//        } else {
//
//            // We need to delay starting the activity because ResolverActivity finishes itself if
//            // launched behind lockscreen.
//            mActivityStarter.startActivity(intent, false /* dismissShade */,
//                                           new ActivityStarter.Callback() {
//                                               @Override
//                                               public void onActivityStarted(int resultCode) {
//                                                   unbindCameraPrewarmService(
//                                                       isSuccessfulLaunch(resultCode));
//                                               }
//                                           });
//        }
    }

    private static boolean isSuccessfulLaunch(int result) {
//        return result == ActivityManager.START_SUCCESS
//               || result == ActivityManager.START_DELIVERED_TO_TOP
//               || result == ActivityManager.START_TASK_TO_FRONT;
        return false;
    }

    public void launchLeftAffordance() {
        if (mLeftIsVoiceAssist) {
            launchVoiceAssist();
        } else {
            launchPhone();
        }
    }

    private void launchVoiceAssist() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                mAssistManager.launchVoiceAssistFromKeyguard();
//                mActivityStarter.preventNextAnimation();
//            }
//        };
//        if (mPhoneStatusBar.isKeyguardCurrentlySecure()) {
//            AsyncTask.execute(runnable);
//        } else {
//            mPhoneStatusBar.executeRunnableDismissingKeyguard(runnable, null /* cancelAction */,
//                                                              false /* dismissShade */, false /* afterKeyguardGone */);
//        }
    }

    private boolean canLaunchVoiceAssist() {
//        return mAssistManager.canVoiceAssistBeLaunchedFromKeyguard();
        return false;
    }

    private void launchPhone() {
//        final TelecomManager tm = TelecomManager.from(mContext);
//        if (tm.isInCall()) {
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    tm.showInCallScreen(false /* showDialpad */);
//                }
//            });
//        } else {
//            mActivityStarter.startActivity(PHONE_INTENT, false /* dismissShade */);
//        }
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && visibility == VISIBLE) {
//            mLockIcon.update();
            updateCameraVisibility();
        }
    }

    @Override
    public void onAnimationToSideStarted(boolean rightPage, float translation, float vel) {
        // 启动某个页面的动作
        glassViewBlurAnim(true);

    }

    @Override
    public void onAnimationToSideEnded() {

    }

    @Override
    public float getMaxTranslationDistance() {
        return (float) Math.hypot(getWidth(), getHeight());
    }

    @Override
    public void onSwipingStarted(boolean rightIcon) {
        // 开始滑动了
//        requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onSwipingAborted() {
        // 滑动取消了
    }

    @Override
    public void onIconClicked(boolean rightIcon) {
        if (mHintAnimationRunning) {
            return;
        }
        mHintAnimationRunning = true;
        afforanceHelper.startHintAnimation(rightIcon, new Runnable() {
            @Override
            public void run() {
                mHintAnimationRunning = false;
            }
        });
    }

    @Override
    public void onReset() {
        glassViewBlurAnim(false);
    }

    @Override
    public KeyguardAffordanceView getLeftIcon() {
        return mLeftAffordanceView;
    }

    @Override
    public KeyguardAffordanceView getCenterIcon() {
        return mLockIcon;
    }

    @Override
    public KeyguardAffordanceView getRightIcon() {
        return mCameraImageView;
    }

    @Override
    public View getLeftPreview() {
        return mLeftPreview;
    }

    @Override
    public View getRightPreview() {
        return mRightPreview;//mCameraPreview;
    }

    @Override
    public float getAffordanceFalsingFactor() {
        return 1.0f;
    }


    public View getIndicationView() {
        return mIndicationText;
    }


    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onUnlockMethodStateChanged() {
        mLockIcon.update();
        updateCameraVisibility();
    }

    private void inflateCameraPreview() {
//        mCameraPreview = mPreviewInflater.inflatePreview(getCameraIntent());
//        if (mCameraPreview != null) {
//            mPreviewContainer.addView(mCameraPreview);
//            mCameraPreview.setVisibility(View.INVISIBLE);
//        }
    }

    private void updateLeftPreview() {
//        View previewBefore = mLeftPreview;
//        if (previewBefore != null) {
//            mPreviewContainer.removeView(previewBefore);
//        }
//        if (mLeftIsVoiceAssist) {
//            mLeftPreview = mPreviewInflater.inflatePreviewFromService(
//                mAssistManager.getVoiceInteractorComponentName());
//        } else {
//            mLeftPreview = mPreviewInflater.inflatePreview(PHONE_INTENT);
//        }
//        if (mLeftPreview != null) {
//            mPreviewContainer.addView(mLeftPreview);
//            mLeftPreview.setVisibility(View.INVISIBLE);
//        }
    }

    public void startFinishDozeAnimation() {
        long delay = 0;
        if (mLeftAffordanceView.getVisibility() == View.VISIBLE) {
            startFinishDozeAnimationElement(mLeftAffordanceView, delay);
            delay += DOZE_ANIMATION_STAGGER_DELAY;
        }
        startFinishDozeAnimationElement(mLockIcon, delay);
        delay += DOZE_ANIMATION_STAGGER_DELAY;
        if (mCameraImageView.getVisibility() == View.VISIBLE) {
            startFinishDozeAnimationElement(mCameraImageView, delay);
        }
        mIndicationText.setAlpha(0f);
//        mIndicationText.animate()
//            .alpha(1f)
//            .setInterpolator(mLinearOutSlowInInterpolator)
//            .setDuration(NotificationPanelView.DOZE_ANIMATION_DURATION);
    }

    private void startFinishDozeAnimationElement(View element, long delay) {
        element.setAlpha(0f);
        element.setTranslationY(element.getHeight() / 2);
        element.animate()
            .alpha(1f)
            .translationY(0f)
            .setInterpolator(mLinearOutSlowInInterpolator)
            .setStartDelay(delay)
            .setDuration(DOZE_ANIMATION_ELEMENT_DURATION);
    }

    private final BroadcastReceiver mDevicePolicyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            post(new Runnable() {
                @Override
                public void run() {
                    updateCameraVisibility();
                }
            });
        }
    };

//    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback =
//        new KeyguardUpdateMonitorCallback() {
//            @Override
//            public void onUserSwitchComplete(int userId) {
//                updateCameraVisibility();
//            }
//
//            @Override
//            public void onStartedWakingUp() {
//                mLockIcon.setDeviceInteractive(true);
//            }
//
//            @Override
//            public void onFinishedGoingToSleep(int why) {
//                mLockIcon.setDeviceInteractive(false);
//            }
//
//            @Override
//            public void onScreenTurnedOn() {
//                mLockIcon.setScreenOn(true);
//            }
//
//            @Override
//            public void onScreenTurnedOff() {
//                mLockIcon.setScreenOn(false);
//            }
//
//            @Override
//            public void onKeyguardVisibilityChanged(boolean showing) {
//                mLockIcon.update();
//            }
//
//            @Override
//            public void onFingerprintRunningStateChanged(boolean running) {
//                mLockIcon.update();
//            }
//
//            @Override
//            public void onStrongAuthStateChanged(int userId) {
//                mLockIcon.update();
//            }
//        };
//
//    public void setKeyguardIndicationController(
//        KeyguardIndicationController keyguardIndicationController) {
//        mIndicationController = keyguardIndicationController;
//    }
//
//    public void setAssistManager(AssistManager assistManager) {
//        mAssistManager = assistManager;
//        updateLeftAffordance();
//    }

    public void updateLeftAffordance() {
        updateLeftAffordanceIcon();
        updateLeftPreview();
    }

    public void glassViewBlurAnim(boolean toblur) {

        if (mGlassViewBlurAnim != null && mGlassViewBlurAnim.isRunning()) {
            mGlassViewBlurAnim.cancel();
        }

        float startAlpha;
        final float endAlpha;

        if (toblur) {
            startAlpha = 0f;
            endAlpha = 1f;
        } else {
            if (mGlassView.getAlpha() == 0) {
                return;
            }
            startAlpha = 1f;
            endAlpha = 0f;
        }
        if (mGlassViewBlurAnim == null) {
            mGlassViewBlurAnim = ValueAnimator.ofFloat(startAlpha, endAlpha);
        } else {
            mGlassViewBlurAnim.setFloatValues(startAlpha, endAlpha);
        }

        mGlassViewBlurAnim.setDuration(250);
        mGlassViewBlurAnim.setInterpolator(mLinearOutSlowInInterpolator);
        mGlassViewBlurAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mGlassView.setAlpha(value);
            }
        });
        mGlassViewBlurAnim.start();

        mGlassViewBlurAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mGlassView.setAlpha(endAlpha);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onUnlock() {
        controlCenterManager.onDestroy();
        LockViewManager.getInstance().unlock(getContext());
    }
}
