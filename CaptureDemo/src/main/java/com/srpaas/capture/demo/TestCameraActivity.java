package com.srpaas.capture.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.srpaas.capture.render.CameraInterface;
import com.srpaas.capture.service.VideoService;
import com.srpaas.capture.service.VideoServiceImpl;
import com.srpaas.capture.service.VideoServiceListener;

import java.lang.reflect.Method;

/**
 * @authordingna
 * @date2017-11-09
 **/
public class TestCameraActivity extends Activity implements VideoServiceListener, MyOrientationDetector.onOrientationChanged {

    private VideoService videoService;
    private MyOrientationDetector myOrientationDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        FrameLayout bigFrameLayout = (FrameLayout) findViewById(R.id.bigLayout);
        CameraSurFaceView cameraSurFaceView = new CameraSurFaceView(this);
        bigFrameLayout.addView(cameraSurFaceView.getView());

        videoService = new VideoServiceImpl();
        videoService.addVideoServiceListener(this);
        videoService.startCapture(this, 1);
    }

    private static int getScreenRotation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            Method m = display.getClass().getDeclaredMethod("getRotation");
            return (Integer) m.invoke(display);
        } catch (Exception e) {
            return Surface.ROTATION_0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (myOrientationDetector == null)
            myOrientationDetector = new MyOrientationDetector(this);
        myOrientationDetector.addOnOrientationChanged(this);
        if (myOrientationDetector.canDetectOrientation()) {
            myOrientationDetector.enable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myOrientationDetector == null) return;
        if (myOrientationDetector.canDetectOrientation()) {
            myOrientationDetector.disable();
        }
        myOrientationDetector.removeOnOrientationChanged();
        myOrientationDetector = null;
        if (videoService != null) {
            videoService.stopCapture();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getScreenOrientation(this);
        updateOnConfigurationChanged();
    }

    public boolean getScreenOrientation(Context context) {
        int mRotation = getOrientation(context);
        //设置当前屏幕的方向/角度
        if (mRotation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            CameraInterface.getInstance().setRotation(0);
            return false;
        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            CameraInterface.getInstance().setRotation(2);
            return false;
        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            CameraInterface.getInstance().setRotation(1);
            return true;
        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            CameraInterface.getInstance().setRotation(3);
            return true;
        } else {
            CameraInterface.getInstance().setRotation(0);//默认是竖屏
            return false;
        }
    }

    //获取当前屏幕方向
    private int getOrientation(Context context) {
        switch (getScreenRotation(context)) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_180:
                return (Build.VERSION.SDK_INT >= 8 ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_270:
                return (Build.VERSION.SDK_INT >= 8 ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            default:
                return 0;
        }
    }

    @Override
    public void onstartCaptureFailListener() {

    }

    @Override
    public void onstopCaptureFailListener() {

    }

    @Override
    public void onPreviewCallback(byte[] des, int width, int height, int rotation) {

    }

    @Override
    public void onPreviewCallback(byte[] data, int width, int height, boolean isRotation, int rotation, boolean isMirror) {

    }

    @Override
    public void onScreenChange(int oldRotation, int rotation) {
        if (rotation == 0 || rotation == 2) {
            if (oldRotation == 0 || oldRotation == 2) {
                updateOnConfigurationChanged();
            } else {
                if (rotation == 0) {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                } else {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);//反向的竖屏
                }
            }
        } else {
            if (oldRotation == 1 || oldRotation == 3) {
                updateOnConfigurationChanged();
            } else {
                if (rotation == 1) {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                } else {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);//反向的横屏
                }
            }
        }
    }

    private void updateOnConfigurationChanged() {
        //屏幕旋转成功了，开始做旋转之后的逻辑
    }
}
