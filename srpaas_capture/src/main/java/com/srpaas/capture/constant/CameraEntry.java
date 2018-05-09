package com.srpaas.capture.constant;

import android.graphics.ImageFormat;

public class CameraEntry {
    public static boolean isSwitch = false;//解决前后相机切换时，最后一帧图像倒置问题
    public static boolean isRotate = false;//解决相机旋转时，最后一帧图像显示不对问题
    public static int deviceType=CameraEntry.DeviceType.mobile;
//    public static boolean isRotationSwitch = false;//切换过程中，相机旋转了

    public enum Type {
        // 0代表后置摄像头,1代表前置摄像头
        BACK_CAMERA(0), FRONT_CAMERA(1);
        private int type;

        private Type(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }
    }

    public class Rotation {
        public static final int ROTATE_0 = 0;// 竖屏
        public static final int ROTATE_90 = 1;// 横屏
        public static final int ROTATE_180 = 2;
        public static final int ROTATE_270 = 3;
    }
    public class DeviceType {
        public static final int mobile = 0;
        public static final int box = 1;
    }
    public static class CaptureSize{
        public static int width = 640;
        public static int height = 480;

    }
    public static class isToYuv {
        public static boolean isToYuv420 = false;//采集回的数据是否需要在capture中转码，旋转或镜像
    }
    public static class CaptureParam{//采集参数设置
        public static int  mFps=15;//默认15帧
        public static long delta=1000 / mFps;
    }
    public static class CaptrueRenderCode{
        public static boolean isRender=true;//是否本地渲染
        public static boolean isCode=true;//是否发送给sdk编码
        public static int Image_Format=ImageFormat.NV21;
    }

}
