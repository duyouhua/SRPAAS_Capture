package com.srpaas.capture.service;

import android.content.Context;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.render.CameraCaptureListener;
import com.srpaas.capture.render.VideoCapture;
import com.srpaas.capture.util.PreviewFrameUtil;
import com.suirui.srpaas.base.util.log.SRLog;

public class VideoServiceImpl implements VideoService, CameraCaptureListener.CameraVideoListener {
    SRLog log = new SRLog(VideoServiceImpl.class.getSimpleName());
    private VideoServiceListener mListener;
    private VideoCapture videoCapture;


    public VideoServiceImpl() {
        if (videoCapture == null)
            videoCapture = new VideoCapture();
        CameraCaptureListener.getInstance().addCameraVideoListener(this);
    }

    @Override
    public void addVideoServiceListener(VideoServiceListener listener) {
        this.mListener = listener;
    }

    @Override
    public void removeVideoServiceListener() {
        this.mListener = null;
    }

    @Override
    public void setDeviceType(int type) {
        CameraEntry.deviceType=type;

    }

    @Override
    public int getDeviceType() {
        return  CameraEntry.deviceType;

    }

    @Override
    public boolean startCapture(Context context, int mCameraType) {
        boolean openStatus = false;
        if (videoCapture != null)
            openStatus = videoCapture.startCapture(context, mCameraType);
        if (openStatus)
            videoCapture.onResume();//解决多次打开关闭相机时，本地预览数据没回调
        if (!openStatus && mListener != null) {
            mListener.onstartCaptureFailListener();
        }
        return true;
    }

    @Override
    public boolean stopCapture() {
        boolean closeStatus = false;
        if (videoCapture != null)
            closeStatus = videoCapture.stopCapture();
        if (!closeStatus && mListener != null) {
            mListener.onstopCaptureFailListener();
        }
        return true;
    }

    @Override
    public void switchCamera(Context context, int cameraType) {
        if (videoCapture != null)
            videoCapture.switchCamera(context, cameraType);
    }

    @Override
    public void setCaptureSize(int width, int height) {
        CameraEntry.CaptureSize.width=width;
        CameraEntry.CaptureSize.height=height;

    }


    @Override
    public void onPreviewCallback(final byte[] data, final int width, final int height, int cameraType, int rotation) {
        if (data == null)
            return;
        //width:1920 height: 1080 cameraType: 0 rotation:0
//        log.E("onPreviewCallback....width:"+width+" height: "+height+" cameraType: "+cameraType+" rotation:"+rotation);
      if(CameraEntry.deviceType==CameraEntry.DeviceType.box){
          onPreviewCallback(data, width, height,false, 0, false);
      }else {//手机端

          switch (rotation) {
              case CameraEntry.Rotation.ROTATE_0:// 旋转270度
                  if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
//                    log.E("onPreviewCallback...front..旋转270度 ...width:" + width + " height:" + height + "  cameraType:" + cameraType);
                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, true, 270, false);
                      } else {
                          onPreviewCallback(data, width, height, true, 270, false);
                      }

                  } else {// 后
                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, true, 90, false);
                      } else {
                          onPreviewCallback(data, width, height, true, 90, false);
                      }
                  }
                  break;
              case CameraEntry.Rotation.ROTATE_90:
                  if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
//                    log.E("onPreviewCallback...front...不旋转.镜像..width:" + width + " height:" + height + "  cameraType:" + cameraType);
                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, false, 0, true);
                      } else {
                          onPreviewCallback(data, width, height, false, 0, true);
                      }
                  } else {
//                    log.E("onPreviewCallback...back...不旋转...width:" + width + " height:" + height + "  cameraType:" + cameraType);
                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, false, 0, false);
                      } else {
                          onPreviewCallback(data, width, height, false, 0, false);
                      }
                  }
                  break;
              case CameraEntry.Rotation.ROTATE_180:// 旋转90度
                  if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
//                    log.E("onPreviewCallback..front....旋转90度并镜像..width:" + width + " height:" + height + "  cameraType:" + cameraType);
                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, true, 90, true);
                      } else {
                          onPreviewCallback(data, width, height, true, 90, true);
                      }

                  } else {
//                    log.E("onPreviewCallback..back...旋转270度并镜像 ...width:" + width + " height:" + height + "  cameraType:" + cameraType);

                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, true, 270, true);
                      } else {
                          onPreviewCallback(data, width, height, true, 270, true);
                      }
                  }
                  break;
              case CameraEntry.Rotation.ROTATE_270:// 旋转180度
                  if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
//                    log.E("onPreviewCallback...front... 旋转180度并镜像...width:" + width + " height:" + height + "  cameraType:" + cameraType);
                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, true, 180, true);
                      } else {
                          onPreviewCallback(data, width, height, true, 180, true);
                      }

                  } else {
//                    log.E("onPreviewCallback...back... 旋转180度...width:" + width + " height:" + height + "  cameraType:" + cameraType);
                      if (CameraEntry.isToYuv.isToYuv420) {
                          render(data, width, height, true, 180, false);
                      } else {
                          onPreviewCallback(data, width, height, true, 180, false);
                      }
                  }
                  break;

              default:
                  break;
          }
      }
    }

    /**
     * 新增接口，转码原始数据
     *
     * @param data
     * @param width
     * @param height
     * @param isRotation
     * @param rotation
     * @param isMirror
     */
    private void onPreviewCallback(byte[] data, int width, int height, boolean isRotation, int rotation, boolean isMirror) {
        if (mListener != null)
            mListener.onPreviewCallback(data, width, height, isRotation, rotation, isMirror);
    }

    /**
     * 转码数据处理
     *
     * @param data
     * @param width
     * @param height
     * @param isRotation
     * @param rotation
     * @param isMirror
     */
    private void render(byte[] data, int width, int height, boolean isRotation, int rotation, boolean isMirror) {
        byte[] des = new byte[width * height * 3 / 2];
        PreviewFrameUtil.YUV420SPToYUV420P(data, des, width, height);
        int desWidth = width;
        int desHeight = height;
        if (isRotation) {
            switch (rotation) {
                case 270:
                    PreviewFrameUtil.rotateYUV270(data, des, width, height);
                    desWidth = height;
                    desHeight = width;
                    break;
                case 90:
                    PreviewFrameUtil.rotateYUV90(data, des, width, height);
                    desWidth = height;
                    desHeight = width;
                    break;
                case 180:
                    PreviewFrameUtil.rotateYUV180(data, des, width, height);
                    break;
            }
        }
        if (isMirror) {
            PreviewFrameUtil.Mirror(des, desWidth, desHeight);
        }


        //        if (yuvPlanes != null) {
//            byte[] y = new byte[yuvPlanes[0].remaining()];
//            yuvPlanes[0].get(y, 0, y.length);
//
//            byte[] u = new byte[yuvPlanes[1].remaining()];
//            yuvPlanes[1].get(u, 0, u.length);
//
//            byte[] v = new byte[yuvPlanes[2].remaining()];
//            yuvPlanes[2].get(v, 0, v.length);
//            if (mListener != null)
//                mListener.onPreviewCallback(des, y, u, v, width, height, rotation);
        if (mListener != null)
            mListener.onPreviewCallback(des, desWidth, desHeight, rotation);

    }

}
