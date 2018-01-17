package com.srpaas.capture.render;

/**
 * Created by cui.li on 2016/11/2.
 */

public class CameraCaptureListener {
    private static CameraCaptureListener cameraListener;
    private CameraVideoListener cameraVideoListener;

    public CameraCaptureListener() {

    }

    public synchronized static CameraCaptureListener getInstance() {

        if (cameraListener == null) {
            cameraListener = new CameraCaptureListener();
        }
        return cameraListener;

    }

    public void addCameraVideoListener(CameraVideoListener listener) {
        this.cameraVideoListener = listener;
    }

    public void onPreviewCallback(byte[] data, int width, int height, int cameraType, int rotation) {
        if (cameraVideoListener != null) {
            cameraVideoListener.onPreviewCallback(data, width, height, cameraType, rotation);
        }
    }

    public interface CameraVideoListener {
        void onPreviewCallback(byte[] data, int width, int height, int cameraType, int rotation);
    }

}
