package com.srpaas.capture.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.srpaas.capture.service.VideoService;
import com.srpaas.capture.service.VideoServiceImpl;
import com.srpaas.capture.service.VideoServiceListener;

/**
 * @authordingna
 * @date2017-11-09
 **/
public class TestCameraActivity extends Activity implements VideoServiceListener {

    private VideoService videoService;

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

    @Override
    protected void onStart() {
        super.onStart();
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
    protected void onDestroy() {
        super.onDestroy();
        if (videoService != null) {
            videoService.stopCapture();
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
}
