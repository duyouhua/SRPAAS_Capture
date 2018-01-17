package com.srpaas.capture.render;

import android.graphics.Point;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * @authordingna
 * @date2016-12-23
 **/
public class CameraInterface {
    private static CameraInterface instance;
    private int rotation = 0;
    private List<GLSurfaceView> glSurfaceViewList = new ArrayList<GLSurfaceView>();
    private Point dataSize;
    private int mCameraType = 1;
    private boolean isOpenCamera = false;
    private boolean isPreviewing = false;
    private boolean isSwitch = false;

    public static synchronized CameraInterface getInstance() {
        if (instance == null) {
            instance = new CameraInterface();
        }
        return instance;
    }

    /**
     * 保存共创建了多少个CameraGLSurfaceView
     *
     * @param glSurfaceView
     */
    public void haveGLSurfaceView(GLSurfaceView glSurfaceView) {
        if (glSurfaceViewList == null)
            glSurfaceViewList = new ArrayList<GLSurfaceView>();
        if (glSurfaceView != null)
            glSurfaceViewList.add(glSurfaceView);

    }

    public void clearGLSurfaceView() {
        if (glSurfaceViewList == null || glSurfaceViewList.size() <= 0) return;
        glSurfaceViewList.clear();
        glSurfaceViewList = null;
    }
    /**
     * 获取创建的CameraGLSurfaceView列表
     *
     * @return
     */
    public List<GLSurfaceView> getCreateGLSurfaceView() {
        return this.glSurfaceViewList;
    }

    /**
     * 获取相机预览大小
     *
     * @return
     */
    public Point getDataSize() {
        return this.dataSize;
    }

    /**
     * 设置相机预览大小
     *
     * @param dataSize
     */
    public void setDataSize(Point dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * 获取相机类型
     *
     * @return
     */
    public int getCameraType() {
        return this.mCameraType;
    }

    /**
     * 设置相机类型
     *
     * @param mCameraType
     */
    public void setCameraType(int mCameraType) {
        this.mCameraType = mCameraType;
    }

    /**
     * 获取相机旋转角度
     *
     * @return
     */
    public int getRotation() {
        return this.rotation;
    }

    /**
     * 设置相机旋转角度
     *
     * @param
     */
    public void setRotation(int r) {
        this.rotation = r;
    }

    /**
     * 获取相机是否开启
     *
     * @return
     */
    public boolean isOpenCamera() {
        return this.isOpenCamera;
    }

    /**
     * 设置相机是否开启
     *
     * @param isOpenCamera
     */
    public void isOpenCamera(boolean isOpenCamera) {
        this.isOpenCamera = isOpenCamera;
    }

    /**
     * 获取相机是否开启预览
     *
     * @param isPreviewing
     */
    public void isPreviewing(boolean isPreviewing) {
        this.isPreviewing = isPreviewing;
    }

    /**
     * 设置相机是否开启预览
     *
     * @return
     */
    public boolean isPreviewing() {
        return this.isPreviewing;
    }

    /**
     * 相机是否切换
     *
     * @return
     */
    public boolean isSwitch() {
        return this.isSwitch;
    }

    public void setSwitch(boolean isSwitch) {
        this.isSwitch = isSwitch;
    }

    public void setGLSurfaceView(List<GLSurfaceView> list) {
        this.glSurfaceViewList = list;
    }
}
