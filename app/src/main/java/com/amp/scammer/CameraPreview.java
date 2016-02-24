package com.amp.scammer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by ampirator on 24.02.2016.
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Context mContext;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera.Size mPreviewSize;

    public CameraPreview(Context context) {
        super(context);
        mContext = context;
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera){
        mCamera = camera;
        if(mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            mPreviewSize = sizes.get(1);
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
        final float k = mPreviewSize.width / (float)mPreviewSize.height;
        final int w =  right - left;
        final int h = bottom - top;
        getChildAt(0).layout(0, 0, w, (int)(w*k));
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Bitmap bmp = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        for(int i = 0; i < mPreviewSize.height; i++) {
            for(int j = 0; j < mPreviewSize.width; j++) {
                int y = bytes[mPreviewSize.width * i + j];
                if (i >= 300 && i < 600 && j>= 300 && j< 600)
                bmp.setPixel(j - 300, i - 300, Color.rgb(y, y, y));
            }
        }

        FileOutputStream out = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/test_images");
            myDir.mkdirs();
            File file = new File (myDir, "1.png");
            if (file.exists()){
                file.delete();
            }

            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
