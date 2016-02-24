package com.amp.scammer;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    private CameraPreview mPreview;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreview = new CameraPreview(this);
        setContentView(mPreview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void captureCamera() {
        releaseCamera();
        if (Camera.getNumberOfCameras() > 0) {
            try {
                Camera.CameraInfo camInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(0, camInfo);
                mCamera = Camera.open(0);
                mCamera.setDisplayOrientation(90);
                mPreview.setCamera(mCamera);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void releaseCamera(){
        mPreview.setCamera(null);
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
