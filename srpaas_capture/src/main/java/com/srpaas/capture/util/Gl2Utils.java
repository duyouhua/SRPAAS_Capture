/*
 *
 * FastDrawerHelper.java
 * 
 * Created by Wuwang on 2016/11/17
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.srpaas.capture.util;

import android.opengl.Matrix;


/**
 * Description:
 */
public class Gl2Utils {
    private static Gl2Utils g12UtilInstance = null;

    public Gl2Utils() {

    }

    public static Gl2Utils getInstance() {
        if (g12UtilInstance == null) {
            g12UtilInstance = new Gl2Utils();
        }
        return g12UtilInstance;
    }

    public void getShowMatrix(float[] matrix, int dataWidth, int dataHeight, int width, int
            height) {
        if (dataHeight > 0 && dataWidth > 0 && width > 0 && height > 0) {
            float sWhView = (float) width / height;
            float sWhImg = (float) dataWidth / dataHeight;
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (sWhImg > sWhView) {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
            } else {
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public float[] rotate(float[] m, float angle) {
        Matrix.rotateM(m, 0, angle, 0, 0, 1);
        return m;
    }

    public float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) {
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }

    public float[] getOriginalMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }

    public void clear() {
        g12UtilInstance = null;
    }
}
