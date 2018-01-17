package com.srpaas.capture.service;

public interface VideoServiceListener {

    /**
     * 相机失败
     */
    void onstartCaptureFailListener();

    /**
     * 关闭相机失败
     */
    void onstopCaptureFailListener();


    /**
     * 相机采集回来的yuv420数据
     *
     * @param des
     * @param width
     * @param height
     * @param rotation
     */
    void onPreviewCallback(byte[] des, int width, int height, int rotation);

    /**
     * 相机采集回的原始数据
     *
     * @param data
     * @param width
     * @param height
     * @param isRotation
     * @param rotation
     * @param isMirror
     */
    void onPreviewCallback(byte[] data, int width, int height, boolean isRotation, int rotation, boolean isMirror);
}
