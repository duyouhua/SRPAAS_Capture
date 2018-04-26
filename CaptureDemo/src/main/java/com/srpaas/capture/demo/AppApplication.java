package com.srpaas.capture.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @authordingna
 * @date2017-11-23
 **/
public class AppApplication extends Application {
    public int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {//切到前台
                    CameraEvent.getInstance().openOrCloseCamera(false);
                }
                count++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {//切到后台
                    CameraEvent.getInstance().openOrCloseCamera(true);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
