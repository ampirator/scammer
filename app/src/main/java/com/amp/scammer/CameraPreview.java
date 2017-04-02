package com.amp.scammer;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ampirator on 24.02.2016.
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera mCamera;
    private CameraSurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera.Size mPreviewSize;
    private ImageProcessor mImageProcessor;

    public CameraPreview(Context context) {
        super(context);
        mSurfaceView = new CameraSurfaceView(context);
        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mImageProcessor = new ImageProcessor(context);
        mImageProcessor.setCameraSurfaceView(mSurfaceView);
    }

    public void setCamera(Camera camera){
        mCamera = camera;
        if(mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            mPreviewSize = sizes.get(6);
            params.setPreviewFormat(ImageFormat.YV12);
            params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(params);
            requestLayout();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(mCamera == null || surfaceHolder == null) {
            return;
        }

        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.setPreviewCallback(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {
        if(mCamera == null || surfaceHolder == null) {
            return;
        }

        try{
            mCamera.startPreview();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final float k = mPreviewSize != null ? mPreviewSize.width / (float)mPreviewSize.height : 1;
        final int w =  right - left;
        final int h = bottom - top;
        getChildAt(0).layout(0, 0, w, (int) (w * k));
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
         mImageProcessor.process(bytes, mPreviewSize.width, mPreviewSize.height);
    }




}
