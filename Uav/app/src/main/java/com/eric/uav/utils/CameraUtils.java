package com.eric.uav.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.util.Collections;

/**
 * 相机操作工具类
 */
public class CameraUtils {
    private static CameraUtils cameraUtils;

    private Activity currentActivity;

    private int cameraId;

    private CameraManager cameraManager;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession cameraCaptureSession;


    private CameraUtils() {
        if (cameraUtils != null) {
            throw new RuntimeException("CameraUtils已存在");
        }
    }

    public static CameraUtils getInstance(Activity currentActivity, int cameraId) {
        if (cameraUtils == null) {
            cameraUtils = new CameraUtils();
        }
        cameraUtils.currentActivity = currentActivity;
        cameraUtils.cameraId = cameraId;
        return cameraUtils;
    }


    /**
     * 打开摄像头
     */
    public void openCamera(TextureView textureView) {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
                // TODO 自动生成的方法存根
                Surface mPreviewSurface = new Surface(arg0);
                cameraUtils.cameraManager = (CameraManager) currentActivity.getSystemService(Context.CAMERA_SERVICE);
                try {
                    if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(currentActivity, "没有权限打开摄像头", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cameraManager.openCamera(String.valueOf(cameraId), new CameraDevice.StateCallback() {

                        @Override
                        public void onOpened(CameraDevice device) {
                            // TODO 自动生成的方法存根s
                            mCameraDevice = device;
                            try {
                                mCameraDevice.createCaptureSession(Collections.singletonList(mPreviewSurface), new CameraCaptureSession.StateCallback() {

                                    @Override
                                    public void onConfigured(CameraCaptureSession session) {
                                        // TODO 自动生成的方法存根
                                        cameraUtils.cameraCaptureSession = session;
                                        try {
                                            CaptureRequest.Builder builder;
                                            builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                            builder.addTarget(mPreviewSurface);
                                            cameraUtils.cameraCaptureSession.setRepeatingRequest(builder.build(), null, null);
                                        } catch (CameraAccessException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(CameraCaptureSession arg0) {
                                    }
                                }, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(CameraDevice arg0, int arg1) {
                        }
                        @Override
                        public void onDisconnected(CameraDevice arg0) {
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
                return false;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
            }
        });
    }
}



