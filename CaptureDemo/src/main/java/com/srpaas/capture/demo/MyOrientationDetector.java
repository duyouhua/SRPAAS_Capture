package com.srpaas.capture.demo;

import android.content.Context;
import android.os.Build;
import android.view.OrientationEventListener;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.render.CameraInterface;


public class MyOrientationDetector extends OrientationEventListener {
    private static onOrientationChanged mListener;
    private int mCamera;
    private int lastOrientation = 0;
    private Context context;

    public MyOrientationDetector(Context context) {
        super(context);
        init(context);
    }

    public MyOrientationDetector(Context context, int rate) {
        super(context, rate);
        init(context);
    }

    public static void addOnOrientationChanged(onOrientationChanged listener) {
        mListener = listener;
    }

    public static void removeOnOrientationChanged() {
        mListener = null;
    }

    private void init(Context context) {
        this.context = context;
        mCamera = CameraInterface.getInstance().getCameraType();
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return; // 手机平放时，检测不到有效的角度
        }
        if (CameraInterface.getInstance().isSwitch()) {//相机切换中不旋转手机
            return;
        }
        checkOrientationChanged(orientation);
    }

    private void checkOrientationChanged(int orientation) {
        int rotation = CameraEntry.Rotation.ROTATE_0;
        // 只检测是否有四个角度的改变
        if (isOrientation0(orientation)) { // 0度(竖屏幕)
            if (isOrientation0(lastOrientation)) {
                return;
            }
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                rotation = CameraEntry.Rotation.ROTATE_0;
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                rotation = CameraEntry.Rotation.ROTATE_0;
            }

        } else if (isOrientation90(orientation)) { // 90度
            if (isOrientation90(lastOrientation)) {
                return;
            }
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                rotation = CameraEntry.Rotation.ROTATE_270;
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                rotation = CameraEntry.Rotation.ROTATE_270;
            }

        } else if (isOrientation180(orientation)) { // 180度
            if (isOrientation180(lastOrientation)) {
                return;
            }
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                rotation = CameraEntry.Rotation.ROTATE_180;
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                rotation = CameraEntry.Rotation.ROTATE_180;
            }

        } else if (isOrientation270(orientation)) { // 270度
            if (isOrientation270(lastOrientation)) {
                return;
            }
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                if (Build.MODEL != null && Build.MODEL.equals("U9180")) {
                    rotation = CameraEntry.Rotation.ROTATE_180;// 270 针对中兴U9180此款手机横屏时相反
                } else {
                    rotation = CameraEntry.Rotation.ROTATE_90;
                }
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                rotation = CameraEntry.Rotation.ROTATE_90;
            }

        } else {
            return;
        }
        startRotationScreen(orientation, rotation);
    }


    private void startRotationScreen(int orientation, int rotation) {
        lastOrientation = orientation;
        int oldRotation = CameraInterface.getInstance().getRotation();
        if (oldRotation != rotation) {
            if (mListener != null) {
                mListener.onScreenChange(oldRotation, rotation);
            }
        }
    }


    private boolean isOrientation0(int orientation) {
        return orientation < 10 || orientation > 350;
    }

    private boolean isOrientation90(int orientation) {
        return orientation < 100 && orientation > 80;
    }

    private boolean isOrientation180(int orientation) {
        return orientation < 190 && orientation > 170;
    }

    private boolean isOrientation270(int orientation) {
        return orientation < 280 && orientation > 260;
    }

    public interface onOrientationChanged {
        void onScreenChange(int oldRotation, int rotation);
    }
}
