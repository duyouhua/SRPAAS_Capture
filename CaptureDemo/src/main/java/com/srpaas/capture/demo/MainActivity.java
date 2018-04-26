package com.srpaas.capture.demo;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.render.CameraGLSurfaceView;
import com.srpaas.capture.render.CameraInterface;
import com.srpaas.capture.render.CameraRender;
import com.srpaas.capture.service.VideoService;
import com.srpaas.capture.service.VideoServiceImpl;
import com.srpaas.capture.service.VideoServiceListener;
import com.suirui.srpaas.base.util.CommonUtils;

import java.util.List;
import java.util.Observable;
import java.util.Observer;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera;

import static android.hardware.Camera.getCameraInfo;
import static android.hardware.Camera.getNumberOfCameras;


public class MainActivity extends AppCompatActivity implements VideoServiceListener, Observer {
    private String TAG = MainActivity.class.getSimpleName();
    private VideoService videoService;
    private Button btnClose, btnOpen, btnClear;
    private boolean isOpen = false;
    private FrameLayout bigLayout, smallLayout;
    private CameraGLSurfaceView bigCameraGLSurfaceView, smallCameraGLSurfaceView;
    private CameraRender bigCameraRender, smallCameraRender;

    //测试更新
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        CameraEvent.getInstance().addObserver(this);
        findview();
//        CameraPreview.getInstance().init();
        initCamera();

    }


    private void findview() {
        bigLayout = (FrameLayout) findViewById(R.id.bigLayout);
        CameraSurFaceView bigView = new CameraSurFaceView(MainActivity.this);
        bigView.addItemView(new CameraSurFaceView.getItemView() {
            @Override
            public void onItemView(CameraGLSurfaceView cameraGLSurfaceView, CameraRender cameraRender) {
                bigCameraGLSurfaceView = cameraGLSurfaceView;
                bigCameraRender = cameraRender;
            }
        });
        bigLayout.addView(bigView.getView());
        bigCameraGLSurfaceView.setZOrderMediaOverlay(false);

        smallLayout = (FrameLayout) findViewById(R.id.smallLayout);
        CameraSurFaceView smallView = new CameraSurFaceView(MainActivity.this);
        smallView.addItemView(new CameraSurFaceView.getItemView() {
            @Override
            public void onItemView(CameraGLSurfaceView cameraGLSurfaceView, CameraRender cameraRender) {
                smallCameraGLSurfaceView = cameraGLSurfaceView;
                smallCameraRender = cameraRender;
            }
        });
        smallLayout.addView(smallView.getView());
        smallCameraGLSurfaceView.setZOrderMediaOverlay(true);

        DisplayMetrics metrics = CommonUtils.getDM(this);
        btnClose = (Button) findViewById(R.id.btnClose);
        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    videoService.stopCapture();
                    isOpen = true;
                    btnClose.setText("打开");
                } else {
                    videoService.startCapture(MainActivity.this, CameraInterface.getInstance().getCameraType());
                    isOpen = false;
                    btnClose.setText("关闭");
                }
            }
        });


        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cameraType = CameraInterface.getInstance().getCameraType() == 1 ? 0 : 1;
                videoService.switchCamera(MainActivity.this, cameraType);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void initCamera() {
        Log.e("","startCapture...initCamera: ");
        videoService = new VideoServiceImpl();
        videoService.addVideoServiceListener(this);
        videoService.setDeviceType(CameraEntry.DeviceType.box);
        videoService.startCapture(this, 0);


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        CameraEvent.getInstance().deleteObserver(this);
        videoService.stopCapture();
        videoService.removeVideoServiceListener();
        super.onDestroy();
    }

    @Override
    public void onstartCaptureFailListener() {

    }

    @Override
    public void onstopCaptureFailListener() {

    }


    @Override
    public void onPreviewCallback(byte[] des, int width, int height, int rotation) {
    }

    @Override
    public void onPreviewCallback(byte[] data, int width, int height, boolean isRotation, int rotation, boolean isMirror) {

    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof CameraEvent) {
            final CameraEvent.NotifyCmd cmd = (CameraEvent.NotifyCmd) data;
            switch (cmd.type) {
                case OPEN_OR_CLOSE_CAMERA:
                    boolean isOpenOrClose = (boolean) cmd.data;
                    if (isOpenOrClose) {
                        videoService.stopCapture();
                        bigCameraGLSurfaceView.setVisibility(View.INVISIBLE);
                        smallCameraGLSurfaceView.setVisibility(View.INVISIBLE);
                    } else {
                        videoService.startCapture(MainActivity.this, CameraInterface.getInstance().getCameraType());
                        bigCameraGLSurfaceView.setVisibility(View.VISIBLE);
                        smallCameraGLSurfaceView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }
}
