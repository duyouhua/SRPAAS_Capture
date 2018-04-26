package com.srpaas.capture.render;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.util.AFilter;
import com.srpaas.capture.util.Gl2Utils;
import com.srpaas.capture.util.OesFilter;
import com.srpaas.capture.util.TextureUtil;
import com.suirui.srpaas.base.util.log.SRLog;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 相机采集的render
 *
 * @authordingna
 * @date2016-12-23
 **/
public class CameraRender implements GLSurfaceView.Renderer {
    SRLog log = new SRLog(CameraRender.class.getSimpleName(),0);
    int mTextureID = -1;
    private AFilter mOesFilter;
    //    private int width, height;
    private int dataWidth, dataHeight;
    private float[] matrix = new float[16];
    private Gl2Utils gl2Utils;
    private TextureUtil textureUtil;
    private int sw = 0, sh = 0;
    private GLSurfaceView glSurfaceView;
    private int mViewW = 0, mViewH = 0;

    public CameraRender(GLSurfaceView glSurfaceView, Context context) {
        this.glSurfaceView = glSurfaceView;
        CameraInterface.getInstance().haveGLSurfaceView(glSurfaceView);
        mOesFilter = OesFilter.getInstance(context.getResources());
        DisplayMetrics dm = getRelDM(context);
        this.sw = dm.widthPixels;
        this.sh = dm.heightPixels;
    }

    public DisplayMetrics getRelDM(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getMetrics(realDisplayMetrics);
        return realDisplayMetrics;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        textureUtil = TextureUtil.getInstance();
        mTextureID = createTextureID();
        gl2Utils = Gl2Utils.getInstance();
        mOesFilter.create();
        mOesFilter.setTextureId(mTextureID);
        calculateMatrix(0, 0);
        Point point = CameraInterface.getInstance().getDataSize();
        if (point != null) {
            setDataSize(point.x, point.y);
        }
    }

    public void setDataSize(int dataWidth, int dataHeight) {
        log.E("VideoCapture。。。。setDataSize...dataWidth:" + dataWidth + "  dataHeight:" + dataHeight);
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        calculateMatrix(0, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.mViewW = width;
        this.mViewH = height;
        log.E("VideoCapture。。。。onSurfaceChanged...width:" + width + "  height:" + height + " dataWidth:" + dataWidth + " dataHeight:" + dataHeight);
        if (this.dataWidth == 0 || this.dataHeight == 0) {
            calculateMatrix(0, 0);
            Point point = CameraInterface.getInstance().getDataSize();
            if (point != null) {
                setDataSize(point.x, point.y);
            }
        }
        setViewSize(width, height);
        // 设置画面的大小
        gl.glViewport(0, 0, width, height);
    }

    private void setViewSize(int width, int height) {
        calculateMatrix(width, height);
    }

    /**
     * @param isCamera 前后相机
     * @param isLand   横竖屏
     * @param isBig    大小屏
     * @param rotaion  旋转的角度
     * @param width
     * @param height
     */
    private void setMatrixRotation(boolean isCamera, boolean isLand, boolean isBig, int rotaion, int width, int height) {
//        log.E("VideoCapture.setMatrixRotation..isCamera:" + isCamera + "  isLand:" + isLand + "  isBig:" + isBig
//                + "  rotaion：" + rotaion + "  width:" + width + "  height:" + height
//                + "  dataWidth：" + dataWidth + "  dataHeight：" + dataHeight+" CameraEntry.deviceType: "+CameraEntry.deviceType);
       if(CameraEntry.deviceType==CameraEntry.DeviceType.box){
           log.E("VideoCapture.....setMatrixRotation...box");
           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, width, height, 0);
       }else {
           log.E("VideoCapture.....setMatrixRotation...mobile");
           if (isLand) {//横屏
               log.E("VideoCapture.....setMatrixRotation...横屏");
               if (isBig) {//大屏
                   if (width > height) {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, width, height, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, width, height, rotaion);
                       }
                   } else {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, width, height, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, width, height, rotaion);
                       }
                   }

               } else {//非大屏
                   if (width > height) {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, width, height, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, width, height, rotaion);
                       }
                   } else {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, height, width, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, height, width, rotaion);
                       }
                   }
               }

           } else {//竖屏
               log.E("VideoCapture.....setMatrixRotation...竖屏");
               if (isBig) {//大屏
                   if (width < height) {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, width, height, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, width, height, rotaion);
                       }
                   } else {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, height, width, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, height, width, rotaion);
                       }
                   }

               } else {//非大屏
                   if (width > height) {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, height, width, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, height, width, rotaion);
                       }
                   } else {
                       if (this.dataHeight > this.dataWidth) {
                           setShowMatrix(isCamera, matrix, this.dataWidth, this.dataHeight, width, height, rotaion);
                       } else {
                           setShowMatrix(isCamera, matrix, this.dataHeight, this.dataWidth, width, height, rotaion);
                       }
                   }
                   log.E("setShowMatrix......非大屏");
               }
           }
       }
    }

    public void calculateMatrix(int width, int height) {
        if (CameraInterface.getInstance().getCameraType() == CameraEntry.Type.FRONT_CAMERA.getValue()) {
            log.E("VideoCapture...前相机....getRotation:" + CameraInterface.getInstance().getRotation() + " width:" + width + " height:" + height);
            switch (CameraInterface.getInstance().getRotation()) {
                case CameraEntry.Rotation.ROTATE_0:
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(false, false, false, 90, width, height);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_90:
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(false, true, false, 0, width, height);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_180://270
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(false, false, false, 270, width, height);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_270:
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(false, true, false, 180, width, height);
                    }
                    break;
            }
        } else {
            log.E("VideoCapture...后相机....getRotation:" + CameraInterface.getInstance().getRotation() + " width:" + width + " height:" + height);
            switch (CameraInterface.getInstance().getRotation()) {
                case CameraEntry.Rotation.ROTATE_0:
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(true, false, false, 270, width, height);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_90:
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(true, true, false, 0, width, height);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_180://90
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(true, true, false, 90, width, height);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_270:
                    if (!CameraEntry.isSwitch) {
                        setMatrixRotation(true, true, false, 180, width, height);
                    }
                    break;
            }
        }
        mOesFilter.setMatrix(matrix);
    }

    private void setShowMatrix(boolean isCamera, float[] matrix, int dataW, int dataH, int w, int h, int rotation) {
        if (dataW == 0 || dataH == 0 || w == 0 || h == 0)
            return;
        if (!isCamera) {
            gl2Utils.getShowMatrix(matrix, dataW, dataH, w, h);
            gl2Utils.flip(matrix, true, false);
            gl2Utils.rotate(matrix, rotation);
        } else {
            gl2Utils.getShowMatrix(matrix, dataW, dataH, w, h);
            gl2Utils.rotate(matrix, rotation);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (!CameraEntry.isSwitch) {
                textureUtil.draw(mOesFilter, mTextureID);
            }
        }
    }

    public int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    /**
     * 更新render渲染时的宽高
     *
     * @param w
     * @param h
     */
    public void updateLocalVideo(int w, int h) {
        log.E("VideoCapture。。setRequestedOrientation。。updateLocalVideo...w:" + w + "  h:" + h);
        setViewSize(w, h);
    }

    //获取当前render渲染画布的宽度
    public int getViewWidth() {
        return mViewW;
    }

    //获取当前render渲染画布的高度
    public int getViewHeight() {
        return mViewH;
    }

    /**
     * 结束会议，清除render中相关的数据
     */
    public void clear() {
        glSurfaceView = null;
        CameraInterface.getInstance().clearGLSurfaceView();
        if (textureUtil != null) {
            textureUtil.clear();
        }
        if (gl2Utils != null) {
            gl2Utils.clear();
        }
    }

    //移出GLSurfaceView
    public void removeRender() {
        List<GLSurfaceView> glSurfaceViewList = CameraInterface.getInstance().getCreateGLSurfaceView();
        if (glSurfaceViewList == null || glSurfaceViewList.size() <= 0) return;
        int size = glSurfaceViewList.size();
        GLSurfaceView surfaceView = null;
        for (int i = 0; i < size; i++) {
            surfaceView = glSurfaceViewList.get(i);
            if (surfaceView != null && glSurfaceView != null && surfaceView.equals(glSurfaceView)) {
                glSurfaceViewList.remove(surfaceView);
                glSurfaceView = null;
                break;
            }
        }
    }
}
