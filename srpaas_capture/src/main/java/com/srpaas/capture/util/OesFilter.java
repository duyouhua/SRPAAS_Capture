/*
 *
 * CameraFilter.java
 * 
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.srpaas.capture.util;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * Description:
 */
public class OesFilter extends AFilter {

    private static OesFilter OesFilterInstance = null;

    public OesFilter(Resources mRes) {
        super(mRes);
    }

    public static OesFilter getInstance(Resources mRes) {
        if (OesFilterInstance == null) {
            OesFilterInstance = new OesFilter(mRes);
        }
        return OesFilterInstance;
    }

    public void clear() {
        OesFilterInstance = null;
    }

    @Override
    protected void onCreate() {
//        createProgramByAssetsFile("shader/oes_base_vertex.sh", "shader/oes_base_fragment.sh");
        createProgramShader();
    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        GLES20.glUniform1i(mHTexture, getTextureType());
    }

}
