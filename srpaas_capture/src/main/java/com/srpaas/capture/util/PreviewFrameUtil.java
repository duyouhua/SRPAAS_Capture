package com.srpaas.capture.util;

import android.hardware.Camera;


import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PreviewFrameUtil {
    private static CameraSizeComparator sizeComparator = new CameraSizeComparator();

    /**
     * 将data转成yuv420P,其格式为:YYYYYYYY UU VV或YYYYYYYY VV UU
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static byte[] YUV420SPToYUV420P(byte[] data, byte[] dataYUV420P,
                                           int width, int height) {
        // 每一帧的大小
        int framesize = width * height;
        int i = 0, j = 0;
        // 这块没问题--Y
        for (i = 0; i < framesize; i++) {
            dataYUV420P[i] = data[i];
        }
        // U
        i = 0;
        for (j = 0; j < framesize / 2; j += 2) {
            dataYUV420P[i + framesize * 5 / 4] = data[j + framesize];
            i++;
        }
        // v
        i = 0;
        for (j = 1; j < framesize / 2; j += 2) {
            dataYUV420P[i + framesize] = data[j + framesize];
            i++;
        }
        return dataYUV420P;
    }

    // 镜像
    public static byte[] Mirror(byte[] yuv_temp, int w, int h) {
        int i, j;
        int a, b;
        byte temp;
        // mirror y
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = yuv_temp[a];
                yuv_temp[a] = yuv_temp[b];
                yuv_temp[b] = temp;
                a++;
                b--;
            }
        }
        // mirror u
        int uindex = w * h;
        for (i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = yuv_temp[a + uindex];
                yuv_temp[a + uindex] = yuv_temp[b + uindex];
                yuv_temp[b + uindex] = temp;
                a++;
                b--;
            }
        }
        // mirror v
        uindex = w * h / 4 * 5;
        for (i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = yuv_temp[a + uindex];
                yuv_temp[a + uindex] = yuv_temp[b + uindex];
                yuv_temp[b + uindex] = temp;
                a++;
                b--;
            }
        }
        return yuv_temp;
    }

    /**
     * yuv420sp顺时针旋转90度
     *
     * @param data
     * @param yuvdata
     * @param srcFrameWidth
     * @param srcFrameHeight
     */
    public static void rotateYUV90(byte[] data, byte[] yuvdata,
                                   int srcFrameWidth, int srcFrameHeight) {
        int i = 0, j = 0, k = 0;
        int uvHeight = srcFrameHeight >> 1;
        int frameSize = srcFrameWidth * srcFrameHeight;
        int qtrFrameSize = srcFrameWidth * srcFrameHeight >> 2;
        // 旋转y
        for (i = 0; i < srcFrameWidth; i++) {
            for (j = srcFrameHeight - 1; j >= 0; j--) {
                yuvdata[k] = data[srcFrameWidth * j + i];
                k++;
            }
        }

        // 旋转uv
        for (i = 0; i < srcFrameWidth; i += 2) {
            for (j = uvHeight - 1; j >= 0; j--) {
                yuvdata[k] = data[frameSize + srcFrameWidth * j + i + 1];// cb/u
                yuvdata[k + qtrFrameSize] = data[frameSize + srcFrameWidth * j
                        + i];// cr/v
                k++;
            }
        }
    }

    /**
     * yuv420sp顺时针旋转180度
     *
     * @param data
     * @param yuvdata
     * @param srcFrameWidth
     * @param srcFrameHeight
     */
    public static void rotateYUV180(byte[] data, byte[] yuvdata,
                                    int srcFrameWidth, int srcFrameHeight) {
        int k = 0;
        int uh = srcFrameHeight >> 1;
        int frameSize = srcFrameWidth * srcFrameHeight;
        int qtrFrameSize = srcFrameWidth * srcFrameHeight >> 2;
        // copy y
        for (int j = srcFrameHeight - 1; j >= 0; j--) {
            for (int i = srcFrameWidth - 1; i >= 0; i--) {
                yuvdata[k] = data[srcFrameWidth * j + i];
                k++;
            }
        }

        for (int j = uh - 1; j >= 0; j--) {
            for (int i = srcFrameWidth - 2; i >= 0; i -= 2) {
                yuvdata[k] = data[frameSize + srcFrameWidth * j + i + 1];
                yuvdata[k + qtrFrameSize] = data[frameSize + srcFrameWidth * j
                        + i];
                k++;
            }
        }
    }

    /**
     * yuv420sp顺时针旋转270度
     *
     * @param data
     * @param yuvdata
     * @param srcFrameWidth
     * @param srcFrameHeight
     */
    public static byte[] rotateYUV270(byte[] data, byte[] yuvdata,
                                      int srcFrameWidth, int srcFrameHeight) {
        int i = 0, j = 0, k = 0;
        int uvHeight = srcFrameHeight >> 1;
        int frameSize = srcFrameWidth * srcFrameHeight;
        int qtrFrameSize = srcFrameWidth * srcFrameHeight >> 2;
        // 旋转y
        for (i = srcFrameWidth - 1; i >= 0; i--) {
            for (j = srcFrameHeight - 1; j >= 0; j--) {
                yuvdata[k] = data[srcFrameWidth * j + i];
                k++;
            }
        }

        // 旋转uv
        for (i = srcFrameWidth - 2; i >= 0; i -= 2) {
            for (j = uvHeight - 1; j >= 0; j--) {
                yuvdata[k] = data[frameSize + srcFrameWidth * j + i + 1];// cb/u
                yuvdata[k + qtrFrameSize] = data[frameSize + srcFrameWidth * j
                        + i];// cr/v
                k++;
            }
        }
        return yuvdata;
    }

    /**
     * 获取yuv的分量
     *
     * @param yuvData
     * @param width
     * @param height
     * @return
     */
    public static ByteBuffer[] bufferToByte(byte[] yuvData, int width,
                                            int height) {
        ByteBuffer[] yuvPlanes = null;
        int[] yuvStrides = {width, width / 2, width / 2};

        if (yuvPlanes == null) {
            yuvPlanes = new ByteBuffer[3];
            yuvPlanes[0] = ByteBuffer.allocateDirect(yuvStrides[0] * height);
            yuvPlanes[1] = ByteBuffer
                    .allocateDirect(yuvStrides[1] * height / 2);
            yuvPlanes[2] = ByteBuffer
                    .allocateDirect(yuvStrides[2] * height / 2);
        }
        int planeSize = 0;
        if (yuvData.length < width * height * 3 / 2) {
            planeSize = yuvData.length * 2 / 3;
        } else {
            planeSize = width * height;
        }
        ByteBuffer[] planes = new ByteBuffer[3];
        planes[0] = ByteBuffer.wrap(yuvData, 0, planeSize);
        planes[1] = ByteBuffer.wrap(yuvData, planeSize, planeSize / 4);
        planes[2] = ByteBuffer.wrap(yuvData, planeSize + planeSize / 4,
                planeSize / 4);
        for (int i = 0; i < 3; i++) {
            yuvPlanes[i].position(0);
            yuvPlanes[i].put(planes[i]);
            yuvPlanes[i].position(0);
            yuvPlanes[i].limit(yuvPlanes[i].capacity());
        }
        return yuvPlanes;
    }

    public static Camera.Size getPropPreviewSize(List<Camera.Size> list, int minHeight) {
        Collections.sort(list, sizeComparator);
        Camera.Size mSize = null;
        for (Camera.Size s : list) {
//            log.E("getPropPreviewSize...start.width:" + s.width + "  height:" + s.height);
            if (s.height == minHeight) {
                mSize = s;
            } else {
                if (s.height < minHeight) {
                    mSize = s;
                }
            }
        }
        if (mSize == null && list != null && list.size() > 0) {
            mSize = list.get(0);
        }
//        log.E("getPropPreviewSize.end...width:" + mSize.width + "  height:" + mSize.height);
        return mSize;
    }

    public static boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    public static Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    /**
     * 匹配相机采集的分辨率，最小匹配360，最大匹配480
     *
     * @param list
     * @param minHeight
     * @param maxHeight
     * @return
     */
    public static Camera.Size getPropPreviewSize(List<Camera.Size> list, int minHeight, int maxHeight) {
        Collections.sort(list, sizeComparator);
        Camera.Size mSize = null;
        Camera.Size maxSize = null;
        //先匹配360的
        for (Camera.Size s : list) {
            if (s.height == minHeight) {
                mSize = s;
                break;
            } else {
                if (s.height < minHeight) {
                    mSize = s;
                    break;
                }
            }
        }
//        log.E("getPropPreviewSize...0000....width:" + mSize.width + "  height:" + mSize.height);
        //匹配480的
        for (Camera.Size s : list) {
            if (s.height == maxHeight) {
                maxSize = s;
                break;
            }
        }

//        log.E("getPropPreviewSize...11111....width:" + maxSize.width + "  height:" + maxSize.height);
        if (mSize != null && maxSize != null) {
            if (mSize.height != minHeight) {
                mSize = maxSize;
            } else {
                maxSize = null;
            }
        } else {
            if (maxSize != null) {
                mSize = maxSize;
            } else {
                if (mSize == null && list != null && list.size() > 0) {
                    mSize = list.get(0);
                }
            }
        }
//        log.E("getPropPreviewSize.end...width:" + mSize.width + "  height:" + mSize.height);
        return mSize;
    }

    public static class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
