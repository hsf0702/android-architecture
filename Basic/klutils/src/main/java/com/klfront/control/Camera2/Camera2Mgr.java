package com.klfront.control.Camera2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Arrays;

/**
 *  作者：英勇青铜5
    链接：http://www.jianshu.com/p/7f766eb2f4e7
 * Created by dell on 2017/9/21.
 */
public class Camera2Mgr {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    //为了使照片竖直显示
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 90);
//        ORIENTATIONS.append(Surface.ROTATION_90, 0);
//        ORIENTATIONS.append(Surface.ROTATION_180, 270);
//        ORIENTATIONS.append(Surface.ROTATION_270, 180);
//    }
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 270);
        ORIENTATIONS.append(Surface.ROTATION_90, 180);
        ORIENTATIONS.append(Surface.ROTATION_180, 90);
        ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private CameraManager mCameraManager;//摄像头管理器
    private Handler childHandler, mainHandler;
    private String mCameraID;//摄像头Id 0 为后  1 为前
    private ImageReader mImageReader;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Activity mContext;
    private @IdRes int mSurfaceViewId;
    private boolean mIsFacingBack;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Camera2Mgr(Activity context,@IdRes int surfaceViewId,boolean isFacingBack)
    {
        this.mContext = context;
        this.mSurfaceViewId = surfaceViewId;
        this.mIsFacingBack = isFacingBack;
    }

    ImageReader.OnImageAvailableListener mImageAvailableListener;
    public void SetEventListener(ImageReader.OnImageAvailableListener listener)
    {
        mImageAvailableListener = listener;
    }

    /**
     * 初始化Camera2
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initCamera2() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(mContext.getMainLooper());
        mCameraID = "" + (mIsFacingBack?CameraCharacteristics.LENS_FACING_BACK:CameraCharacteristics.LENS_FACING_FRONT);//LENS_FACING_BACK 为前置摄像头

        mImageReader = ImageReader.newInstance(540, 960, ImageFormat.JPEG,1);
        mImageReader.setOnImageAvailableListener(mImageAvailableListener, mainHandler);
        //获取摄像头管理
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //打开摄像头
            mCameraManager.openCamera(mCameraID, stateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetCamera2()
    {
        mImageReader = ImageReader.newInstance(540, 960, ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(mImageAvailableListener, mainHandler);
        takePreview(mSurfaceViewId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void disposeCamera2()
    {
        // 释放Camera资源
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    /**
     * 摄像头创建监听
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {//打开摄像头
            mCameraDevice = camera;
            //开启预览
            try {
                takePreview(mSurfaceViewId);
            }catch (Exception e)
            {
                Log.e("error",e.getMessage());
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {//关闭摄像头
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {//发生错误
            Toast.makeText(mContext, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * 开始预览
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePreview(@IdRes int resId) {
        try {
//            SurfaceView surfaceView = new SurfaceView(mContext);
            mSurfaceView = (SurfaceView) mContext.findViewById(resId);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.setFixedSize(mSurfaceView.getWidth(),mSurfaceView.getHeight());
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            previewRequestBuilder.addTarget(mImageReader.getSurface());

            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // 自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(mContext, "配置失败", Toast.LENGTH_SHORT).show();
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }catch (Exception e)
        { //UnsupportOperateException
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void takePicture() {

        if (mCameraDevice == null||mCameraCaptureSession==null) return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = mContext.getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.capture(mCaptureRequest, null, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {  // IlleagalArgumentExcenption
            e.printStackTrace();
        }
    }
}