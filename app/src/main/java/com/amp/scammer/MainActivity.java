package com.amp.scammer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private CameraPreview mPreview;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        mPreview = new CameraPreview(this);
        setContentView(mPreview);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
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
