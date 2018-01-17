package com.srpaas.capture.demo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.srpaas.capture.render.CameraGLSurfaceView;
import com.srpaas.capture.render.CameraRender;
import com.srpaas.capture.service.VideoService;
import com.srpaas.capture.service.VideoServiceImpl;
import com.srpaas.capture.service.VideoServiceListener;
import com.srpaas.capture.util.ToolUtil;

/**
 * @authordingna
 * @date2017-11-09
 **/
public class TestCameraActivity extends Activity implements VideoServiceListener, MyOrientationDetector.onOrientationChanged {

    private VideoService videoService;
    private MyOrientationDetector myOrientationDetector;
    private CameraGLSurfaceView mBigCameraView, mSmallCameraView;
    private CameraRender mBigCameraRender, mSmallCameraRender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        findView();
        videoService = new VideoServiceImpl();
        videoService.addVideoServiceListener(this);
        videoService.startCapture(this, 1);
    }

    private void findView() {
        //big video
        FrameLayout bigFrameLayout = (FrameLayout) findViewById(R.id.bigLayout);
        CameraSurFaceView mBigCamera = new CameraSurFaceView(this);
        addCameraView(bigFrameLayout, mBigCamera, false);

        //small video
        FrameLayout smallFrameLayout = (FrameLayout) findViewById(R.id.smallLayout);
        CameraSurFaceView mSmallCamera = new CameraSurFaceView(this);
        addCameraView(smallFrameLayout, mSmallCamera, true);
    }

    private void addCameraView(FrameLayout frameLayout, CameraSurFaceView cameraView, final boolean isMediaOverlay) {
        cameraView.addItemView(new CameraSurFaceView.getItemView() {
            @Override
            public void onItemView(CameraGLSurfaceView cameraGLSurfaceView, CameraRender cameraRender) {
                cameraGLSurfaceView.setZOrderMediaOverlay(isMediaOverlay);
                if (!isMediaOverlay) {
                    mBigCameraView = cameraGLSurfaceView;
                    mBigCameraRender = cameraRender;
                } else {
                    mSmallCameraView = cameraGLSurfaceView;
                    mSmallCameraRender = cameraRender;
                }
            }
        });
        frameLayout.addView(cameraView.getView());
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
        boolean isScreen = ToolUtil.getScreenOrientation(this);
        updateOnConfigurationChanged(isScreen);
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
                updateOnConfigurationChanged(false);
            } else {
                if (rotation == 0) {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                } else {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);//反向的竖屏
                }
            }
        } else {
            if (oldRotation == 1 || oldRotation == 3) {
                updateOnConfigurationChanged(true);
            } else {
                if (rotation == 1) {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                } else {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);//反向的横屏
                }
            }
        }
    }

    private void updateOnConfigurationChanged(boolean isScreen) {
        //屏幕旋转成功了，开始做旋转之后的逻辑
        if (mBigCameraView != null && mBigCameraRender != null) {
            updateCameraVideo(mBigCameraView, mBigCameraRender, isScreen);
        }
        if (mSmallCameraView != null && mSmallCameraRender != null) {
            updateCameraVideo(mSmallCameraView, mSmallCameraRender, isScreen);
        }
    }

    private void updateCameraVideo(CameraGLSurfaceView cameraView, CameraRender cameraRender, boolean isScreen) {
        int width = cameraView.getMeasuredWidth();
        int height = cameraView.getMeasuredHeight();
        if (isScreen) {//横屏
            if (width > height) {
                cameraRender.updateLocalVideo(width, height);
            } else {
                cameraRender.updateLocalVideo(height, width);
            }
        } else {//竖屏
            if (width < height) {
                cameraRender.updateLocalVideo(width, height);
            } else {
                cameraRender.updateLocalVideo(height, width);
            }
        }
    }
}
