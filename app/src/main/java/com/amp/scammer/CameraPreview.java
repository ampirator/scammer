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
        final float k = mPreviewSize != null ? mPreviewSize.width / (float)mPreviewSize.height : 1;
        final int w =  right - left;
        final int h = bottom - top;
        getChildAt(0).layout(0, 0, w, (int)(w*k));
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        imageProcessing(bytes, mPreviewSize.width, mPreviewSize.height);
    }

    private void imageProcessing(byte[] bytes, int width, int height) {
        final int [] pixels = new int[width * height];
        final int [][] integralImage = new int[height][width];
        int rowSum;
        for(int i = 0; i < height; i++) {
            rowSum = 0;
            for(int j = 0; j < width; j++) {
                rowSum += (bytes[width * i + j] & 0xff);
                integralImage[i][j] = i == 0 ? rowSum : integralImage[i - 1][j] + rowSum;
                pixels[width * i + j] = Color.rgb(integralImage[i][j]%256,integralImage[i][j]%256, integralImage[i][j]%256);
            }
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        saveBitmap(bmp, "1.png");
    }

    private void saveBitmap(Bitmap bmp, String filename) {
        FileOutputStream out = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/test_images");
            myDir.mkdirs();
            File file = new File (myDir, filename);
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
