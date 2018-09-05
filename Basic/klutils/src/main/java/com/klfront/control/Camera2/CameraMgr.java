package com.klfront.control.Camera2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by dell on 2017/9/28.
 */

public class CameraMgr {
    private String TAG = "CameraMgr";
    private Activity mContext;
    private Camera mCamera;

    private SurfaceView mPreView;
    private SurfaceHolder mHolder;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    Camera.PictureCallback pictureCallback;

    /**
     * 初始化拍照管理类
     *
     * @param context
     * @param mSurfaceViewId
     * @param isFacingBack   是否前置摄像头
     */
    public CameraMgr(Activity context, @IdRes int surfaceViewId, boolean isFacingBack) {
        this.mContext = context;
        mPreView = (SurfaceView) mContext.findViewById(surfaceViewId);
        this.cameraPosition = isFacingBack ? 0 : 1;
    }

    public void SetEventListener(Camera.PictureCallback listener) {
        this.pictureCallback = listener;
    }


    private boolean checkCamera(Context context) {
        // 可修改参数来判断设备是否支持ble等其他功能
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true; // 支持Camera功能
        } else {
            return false;
        }
    }

    private void getCamera() {
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 0) {
                //前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera = Camera.open(i);
                    Log.e("mCamera", mCamera == null ? "失败" : "成功");
                    break;
                }
            } else {
                //后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera = Camera.open(i);
                    break;
                }
            }
        }
    }

    Camera.CameraInfo cameraInfo;

    public boolean initCamera() {
        if (checkCamera(mContext)) {
            getCamera();
            takePreview();
            return true;
        } else {
            return false;
        }
    }

    public void resetCamera() {
        takePreview();
    }

    private void takePreview() {
        mHolder = mPreView.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mCamera != null && mHolder != null) {
                    setStartPreview(mCamera, mHolder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    if (mHolder != null) {
                        setStartPreview(mCamera, mHolder);
                    }
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCamera();
            }
        }); // 实现SurfaceHolder.Callback接口
        mCamera.autoFocus(null);
        setStartPreview(mCamera, mHolder);//开始预览
    }

    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder); //通过surfaceview显示取景画面
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(ImageFormat.JPEG); // 相片保存的格式
            parameters.setPictureSize(540, 960); // 相片保存的尺寸，因为是横屏所以长>高
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 自动对焦
            if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
            }
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "setStartPreview: setPreviewDisplay error");
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
        }
    }

    public void takePicture() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG); // 相片保存的格式
        parameters.setPictureSize(540, 960); // 相片保存的尺寸，因为是横屏所以长>高
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 自动对焦
//        if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);//拍照旋转无效
//        } else {
//            parameters.set("orientation", "landscape");
//            mCamera.setDisplayOrientation(0);
//        }
        try {
            mCamera.takePicture(null, null, pictureCallback);// 拍照
        } catch (Exception e) {
            Log.e("takePicture", e.getMessage());
        }
    }

}
