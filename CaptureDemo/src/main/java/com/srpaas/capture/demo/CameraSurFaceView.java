package com.srpaas.capture.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.srpaas.capture.render.CameraGLSurfaceView;
import com.srpaas.capture.render.CameraRender;

/**
 * @authordingna
 * @date2017-11-09
 **/
public class CameraSurFaceView extends View {
    private View view;
    private CameraGLSurfaceView cameraGLSurfaceView;
    private CameraRender cameraRender;
    public CameraSurFaceView(Context context) {
        super(context);
        this.init(context);
    }

    public CameraSurFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public CameraSurFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.test_camera_view, null);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.cameraView);
        cameraGLSurfaceView = new CameraGLSurfaceView(context);
        cameraGLSurfaceView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        frameLayout.addView(cameraGLSurfaceView);
        cameraRender = new CameraRender(cameraGLSurfaceView, context);
        cameraGLSurfaceView.setRenderer(cameraRender);
    }

    public View getView() {
        return view;
    }

    public void addItemView(getItemView mGetItemView) {
        mGetItemView.onItemView(cameraGLSurfaceView, cameraRender);
    }

    public interface getItemView {
        void onItemView(CameraGLSurfaceView cameraGLSurfaceView, CameraRender cameraRender);
    }
}
