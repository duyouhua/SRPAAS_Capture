package com.srpaas.capture.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * 相机GLSurfaceView
 *
 * @authordingna
 * @date2016-12-23
 **/
public class CameraGLSurfaceView extends GLSurfaceView {

    public CameraGLSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

}
