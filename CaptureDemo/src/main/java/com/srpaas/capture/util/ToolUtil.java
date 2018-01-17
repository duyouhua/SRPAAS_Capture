package com.srpaas.capture.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.srpaas.capture.render.CameraInterface;

import java.lang.reflect.Method;

/**
 * @authordingna
 * @date2018-01-17
 **/
public class ToolUtil {

    private static int getScreenRotation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            Method m = display.getClass().getDeclaredMethod("getRotation");
            return (Integer) m.invoke(display);
        } catch (Exception e) {
            return Surface.ROTATION_0;
        }
    }

    public static boolean getScreenOrientation(Context context) {
        int mRotation = getOrientation(context);
        //设置当前屏幕的方向/角度
        if (mRotation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            CameraInterface.getInstance().setRotation(0);
            return false;
        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            CameraInterface.getInstance().setRotation(2);
            return false;
        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            CameraInterface.getInstance().setRotation(1);
            return true;
        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            CameraInterface.getInstance().setRotation(3);
            return true;
        } else {
            CameraInterface.getInstance().setRotation(0);//默认是竖屏
            return false;
        }
    }

    //获取当前屏幕方向
    private static int getOrientation(Context context) {
        switch (getScreenRotation(context)) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_180:
                return (Build.VERSION.SDK_INT >= 8 ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_270:
                return (Build.VERSION.SDK_INT >= 8 ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            default:
                return 0;
        }
    }
}
