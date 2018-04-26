package com.srpaas.capture.render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Handler;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.util.PreviewFrameUtil;
import com.srpaas.capture.util.TextureUtil;
import com.suirui.srpaas.base.util.log.SRLog;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@SuppressLint("NewApi")
public class VideoCapture implements Camera.PreviewCallback, SurfaceTexture.OnFrameAvailableListener {
    SRLog log = new SRLog(VideoCapture.class.getSimpleName());
     int minPreviewHeight = 360;
     int mPreviewHeight = 480;
    //-----------------------------------------
    boolean showVideo = false;
    boolean mStarted = false;
    Thread mFrameThread = null;
    private int cameraType = 1;
    private Camera mCamera;
    private int mCaptureWidth = 0;
    private int mCaptureHeight = 0;
    private int numCaptureBuffers = 3;
    private Camera.Size preSize;
    private Point mPreSize;
    private Camera.Parameters param;
    //    private int fps = 15000;
    private Handler handler;
    private SensorManager sm;
    private Sensor sensor;
    private int mFps = 15;
    long delta = 1000 / mFps;
    private List<GLSurfaceView> glSurfaceViewList;
    private List<Camera.Size> cameraList;
    private GLSurfaceView glSurfaceView;
    private BlockingQueue<Object> mFrameList = new ArrayBlockingQueue<Object>(18);
    Runnable frameRunnable = new Runnable() {

        @Override
        public void run() {
            while (showVideo && mStarted) {
                sendVideoFrame();//在这里把帧发出去
                if (mFrameList.size() >= mFps) {//缓存满了
                    Object[] obj = (Object[]) mFrameList.poll();
                    mFrameList.clear();
                    mFrameList.offer(obj);
                }
            }
        }
    };

    public VideoCapture() {
        TextureUtil.getSurfaceTexture().setOnFrameAvailableListener(this);
    }


    private void putVideoFrame(byte[] data, int width, int height, int cameraType, int rotation) {
        if (!showVideo) {
//            log.E("putVideoFrame.已经没有显示视频了。");
            return;
        }
        synchronized (mFrameList) {
            Object[] obj = new Object[]{data, width, height, cameraType, rotation};
            mFrameList.offer(obj);
        }
    }

    private void sendVideoFrame() {
        Object[] obj = null;
        obj = (Object[]) mFrameList.poll();
        if (null != obj) {
            long start = System.currentTimeMillis();
            byte[] data = (byte[]) obj[0];
            int width = (int) obj[1];
            int height = (int) obj[2];
            int cameraType = (int) obj[3];
            int rotation = (int) obj[4];
            if (showVideo) {
                CameraCaptureListener.getInstance().onPreviewCallback(data, mCaptureWidth, mCaptureHeight, cameraType, rotation);
                long end = System.currentTimeMillis();
                start = end - start;
                //限制发送的帧数。
                end = delta - start;
                if (end > 0) {
                    try {
                        Thread.sleep(end);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            obj = null;
        } else {
            try {
                Thread.sleep(delta);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean startCapture(Context context, int mCameraType) {
        log.E( "VideoCapture..startCapture...设置相机的类型...mCameraType:" + mCameraType);
        CameraInterface.getInstance().setCameraType(mCameraType);
//        log.E( "VideoCapture.....设置相机的类型...mCameraType:" + mCameraType);
        try {
            if (mCamera == null || !CameraInterface.getInstance().isOpenCamera()) {

                int cameraCount = Camera.getNumberOfCameras();

                  if(CameraEntry.deviceType==CameraEntry.DeviceType.mobile){
                      if (cameraCount <= 0)
                          return false;
                      boolean isHaveCamera = false;//是否有当前要打开的相机是否存在
                      int existCamera = -1;
                      Camera.CameraInfo info;
                      for (int i = 0; i < cameraCount; i++) {
                          info = new Camera.CameraInfo();
                          Camera.getCameraInfo(i, info);
                          if (info.facing == mCameraType) {
                              isHaveCamera = true;
                              break;
                          } else {
                              existCamera = info.facing;
                          }
                      }
                      if (!isHaveCamera) {//没有当前要打开的相机，则打开当前存在的相机
                          if (existCamera == -1) {
                              return false;
                          }
                          mCameraType = existCamera;
                      }
                      mCamera = Camera.open(mCameraType);
                      log.E("VideoCapture。。startCapture...mobile: "+mCameraType);
                  }else{
                      mCamera=Camera.open(mCameraType);
                      log.E("VideoCapture。。startCapture...cameraCount: 1111");

                  }
                CameraInterface.getInstance().setCameraType(mCameraType);
                CameraInterface.getInstance().isOpenCamera(true);
            }
            if (mCamera != null) {
                log.E("VideoCapture。。startCapture...cameraCount: 1111");
                param = mCamera.getParameters();
                cameraList = getCurrentSupportedVideoSizes(param);
                if (cameraList == null || cameraList.size() <= 0) {
                    return false;
                }

                    boolean isSupport = PreviewFrameUtil.isPropPreviewSize(cameraList, CameraEntry.CaptureSize.width, CameraEntry.CaptureSize.height);
                    if(isSupport) {
                        param.setPreviewSize(CameraEntry.CaptureSize.width, CameraEntry.CaptureSize.height);
                    }else {
                        preSize = PreviewFrameUtil.getPropPreviewSize(cameraList, minPreviewHeight, mPreviewHeight);
                        if (preSize != null) {
                            param.setPreviewSize(preSize.width, preSize.height);
                        }
                    }


//
//                for (Camera.Size s : cameraList) {
//                    log.E("cameraList...."+s.width+" : "+s.height);
//
//                }
                param.setPreviewFormat(ImageFormat.NV21);
                mCamera.setParameters(param);
                param = mCamera.getParameters();
                int mformat = param.getPreviewFormat();
                this.mCaptureWidth = param.getPreviewSize().width;
                this.mCaptureHeight = param.getPreviewSize().height;
                int bufSize = mCaptureWidth * mCaptureHeight * ImageFormat.getBitsPerPixel(mformat) / 8;
                for (int i = 0; i < numCaptureBuffers; i++) {
                    mCamera.addCallbackBuffer(new byte[bufSize]);
                }
                mCamera.setPreviewCallbackWithBuffer(this);
                Camera.Size pre = param.getPreviewSize();
//                log.E( "VideoCapture....startCapture.." + pre.height + "  :" + pre.width);
                mPreSize = new Point(pre.height, pre.width);
                CameraInterface.getInstance().setDataSize(mPreSize);
            }

            showVideo = true;
            mStarted = true;
            mFrameThread = new Thread(frameRunnable);
            mFrameThread.start();
            return setPreviewTexture(TextureUtil.getSurfaceTexture());
        } catch (Exception e) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            e.printStackTrace();
        }
        return false;
    }

    private boolean setPreviewTexture(SurfaceTexture surface) {
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera())
            return false;
        try {
            mCamera.setPreviewTexture(surface);
            startPreview();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera callbackCamera) {
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera() || mCamera != callbackCamera) {
            return;
        }
//        log.E( "VideoCapture。。。。。onPreviewFrame...getCameraType:" + CameraInterface.getInstance().getCameraType());
        try {
//            log.E( "VideoCapture..onPreviewFrame.." + callbackCamera.getParameters().getPreviewFrameRate());
            int rotation = CameraInterface.getInstance().getRotation();
            cameraType = CameraInterface.getInstance().getCameraType();
//            CameraCaptureListener.getInstance().onPreviewCallback(data, mCaptureWidth, mCaptureHeight, cameraType, rotation);
            putVideoFrame(data, mCaptureWidth, mCaptureHeight, cameraType, rotation);//限时发送帧率
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCamera.addCallbackBuffer(data);
    }

    public boolean stopCapture() {
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera()) {
            return true;
        }
        try {
            CameraInterface.getInstance().isOpenCamera(false);
            CameraInterface.getInstance().isPreviewing(false);
            mCamera.addCallbackBuffer(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.setPreviewTexture(null);
            mCamera.release();
            mCamera = null;
            showVideo = false;
            mStarted = false;
            if (cameraList != null) {
                cameraList.clear();
                cameraList = null;
            }
            if (mFrameList != null)
                mFrameList.clear();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void onPause() {
        glSurfaceViewList = CameraInterface.getInstance().getCreateGLSurfaceView();
        if (glSurfaceViewList == null || glSurfaceViewList.size() <= 0)
            return;
        int size = glSurfaceViewList.size();
        for (int i = 0; i < size; i++) {
            glSurfaceView = glSurfaceViewList.get(i);
            if (glSurfaceView != null) {
//                log.E( "VideoCapture...onPause()...i:" + i);
                glSurfaceView.onPause();
            }
        }
    }

    public void onResume() {
        glSurfaceViewList = CameraInterface.getInstance().getCreateGLSurfaceView();
        if (glSurfaceViewList == null || glSurfaceViewList.size() <= 0)
            return;
        int size = glSurfaceViewList.size();
        for (int i = 0; i < size; i++) {
            glSurfaceView = glSurfaceViewList.get(i);
            if (glSurfaceView != null) {
//                log.E( "VideoCapture...onResume()...i:" + i);
                glSurfaceView.onResume();
            }
        }
    }

    public void startPreview() {
        if (CameraInterface.getInstance().isPreviewing())
            return;
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera())
            return;
        mCamera.startPreview();
        try {
            CameraEntry.isSwitch = false;
            CameraInterface.getInstance().setSwitch(false);
            Thread.sleep(500);//解决华为手机切换前后相机时，前一类型的相机没有释放掉
        } catch (Exception e) {
            e.printStackTrace();
        }
        CameraInterface.getInstance().isPreviewing(true);
    }

    //重置相机的角度
    private void resetRotation() {
        int currentRotation = CameraInterface.getInstance().getRotation();
        CameraInterface.getInstance().setRotation(currentRotation);
    }

    private List<Camera.Size> getCurrentSupportedVideoSizes(Camera.Parameters param) {
        if (param.getSupportedVideoSizes() != null) {
            return param.getSupportedVideoSizes();
        } else {
            return param.getSupportedPreviewSizes();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        glSurfaceViewList = CameraInterface.getInstance().getCreateGLSurfaceView();
        if (glSurfaceViewList == null || glSurfaceViewList.size() <= 0)
            return;
        int size = glSurfaceViewList.size();
        for (int i = 0; i < size; i++) {
            glSurfaceView = glSurfaceViewList.get(i);
            if (glSurfaceView != null) {
//                log.E(  "VideoCapture...onFrameAvailable...i:" + i);
                glSurfaceView.requestRender();
            }
        }
    }

//    private int getSupportedPreviewFpsRange(Camera.Parameters param) {
//        int mFPS = 0;
//        List<int[]> range = param.getSupportedPreviewFpsRange();
//        for (int j = 0; j < range.size(); j++) {
//            int[] r = range.get(j);
//            for (int k = 0; k < r.length; k++) {
//                Log.e("", "startCapture.... r[k]:" + r[k]);
//                if (fps == r[k]) {
//                    mFPS = fps / 1000;
//                    break;
//                } else {
//                    if (r[k] > fps && mFPS == 0) {
//                        mFPS = r[k] / 1000;
//                        break;
//                    }
//
//                }
//            }
//
//            if (mFPS == 0) {
//                mFPS = r[(r.length) - 1] / 1000;
//            }
//        }
//        Log.e("", "startCapture....mFPS:" + mFPS);
//        return mFPS;
//    }

    //相机切换
    public void switchCamera(Context context, int cameraType) {
        log.E("VideoCapture.....开始切换");
        CameraEntry.isSwitch = true;
        CameraInterface.getInstance().setSwitch(true);
        onPause();
        stopCapture();
        //重置相机的角度
        resetRotation();
        startCapture(context, cameraType);
        onResume();
    }
}
