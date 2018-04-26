package com.srpaas.capture.demo;

import java.util.Observable;

/**
 * @authordingna
 * @date2017-11-23
 **/
public class CameraEvent extends Observable {
    private volatile static CameraEvent instance;

    public CameraEvent() {

    }

    public static CameraEvent getInstance() {
        if (instance == null) {
            synchronized (CameraEvent.class) {
                if (instance == null) {
                    instance = new CameraEvent();
                }
            }
        }
        return instance;
    }

    public void openOrCloseCamera(boolean isOpenOrClose) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.OPEN_OR_CLOSE_CAMERA, isOpenOrClose));
    }


    public enum NotifyType {
        OPEN_OR_CLOSE_CAMERA,//打开或者关闭相机
    }

    /**
     * 通知上层用的数据
     */
    public class NotifyCmd {
        public final CameraEvent.NotifyType type;
        public final Object data;

        NotifyCmd(CameraEvent.NotifyType type, Object data) {
            this.type = type;
            this.data = data;
        }
    }
}
