package com.srpaas.capture.service;

import android.content.Context;

public interface VideoService {
    void addVideoServiceListener(VideoServiceListener listener);

    void removeVideoServiceListener();
    void setDeviceType(int type);
    int getDeviceType();

    /**
     * 视频采集
     *
     * @return
     */
    boolean startCapture(Context context, int mCameraType);

    /**
     * 关闭相机
     */
    boolean stopCapture();

    /**
     * 切换相机
     *
     * @param context
     * @param cameraType
     */
    void switchCamera(Context context, int cameraType);

    /**
     * 设置采集的分辨率
     * @param width
     * @param height
     */
    void setCaptureSize(int width,int height);

}
