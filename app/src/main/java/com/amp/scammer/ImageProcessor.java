package com.amp.scammer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ampirator on 09.04.2016.
 */
public class ImageProcessor {
    private static final int SOBEL_SIZE = 3;
    private final Context context;
    private final int[][] sobelx =
                  {{-1, 0, 1},
                   { 2, 0, 2},
                   {-1, 0, 1}};
    private final int[][] sobely =
                   {{-1, -2, -1},
                    { 0,  0,  0},
                    { 1,  2,  1}};
    private CameraSurfaceView cameraSurfaceView;

    public ImageProcessor(Context context) {
        this.context = context;
    }

    public void process(byte[] bytes, int width, int height) {
        List<Point> corners = new LinkedList<>();
        final int [] pixels = new int[width * height];
        final int [][] integralImage = new int[height][width];
        int rowSum;
//        int y;
//        for(int i = 0; i < height; i++) {
//            rowSum = 0;
//            for(int j = 0; j < width; j++) {
//                y = (bytes[width * i + j] & 0xff);
//                rowSum += y;
//                integralImage[i][j] = i == 0 ? rowSum : integralImage[i - 1][j] + rowSum;
//                pixels[width * i + j] =  Color.rgb(y, y, y);
//            }
//        }

//        for(int y = 0; y < height; y++) {
//            for(int x = 0; x < width; x++) {
//                if(y > 0 && x > 0 && y < height - 1 && x < width - 1) {
//                    int sobel = sobelOperator(bytes, width, height, x, y);
//                    if(sobel > 400) {
//                        pixels[width * y + x] = Color.rgb(sobel, sobel, sobel);
//                    } else {
//                        pixels[width * y + x] = Color.rgb(0, 0, 0);
//                    }
//
//                }
//            }
//        }

        int ws = 3;
        for(int y = ws/2; y < height - ws/2; y++) {
            for(int x = ws/2; x < width - ws/2; x++) {

                int ix = 0, iy = 0, v;

                for(int k = -ws/2; k < ws/2 + 1; k++) {
                    for(int n = -ws/2; n < ws/2 + 1; n++) {
                        v = (bytes[width * (y + k) + (x + n)] & 0xff) - (bytes[width * (y - k) + (x)] & 0xff);
                        ix += v * v;

//                        v = (bytes[width * (y + k) + (x + 1)] & 0xff) + (bytes[width * (y - k) + (x)] & 0xff);
//                        ix += (v * v);

                        v = (bytes[width * (y - n) + (x + k)] & 0xff) - (bytes[width * (y) + (x + k)] & 0xff);
                        iy += (v * v);

//                        v = (bytes[width * (y + 1) + (x + k)] & 0xff) - (bytes[width * (y) + (x + k)] & 0xff);
//                        iy += (v * v);
                    }
                }

                int A = ix * ix, B = iy * iy, C = ix * iy;

                double l1 = 0.5 * (A + B + Math.sqrt(A * A - 2 * A * B + B * B + 4 * C * C));
                double l2 = 0.5 * (A + B - Math.sqrt(A * A - 2 * A * B + B * B + 4 * C * C));

                double detM = l1*l2;
                double traceM = l1 + l2;
                double r = detM - 0.1 * (traceM * traceM);
                boolean isCorner = r > 1000000;
                if (isCorner) {
                    corners.add(new Point(x, y));
                }

                int yuv = (bytes[width * (y) + (x)] & 0xff);
                pixels[width * y + x] =  Color.rgb(yuv, yuv, yuv);
            }
        }




        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
     //   saveBitmap(bmp, "1.png");
        drawOnSurface(bmp, corners);

    }

    private int sobelOperator(byte[] bytes, int width, int height, int x, int y){
        int sx = 0;
        int sy = 0;
        int v = 0;
        final int[] delta = {-1, 0, 1};
        for (int i = 0; i < SOBEL_SIZE; i++) {
            for(int j = 0; j < SOBEL_SIZE; j++) {
                v = (bytes[width * (y + delta[i]) + (x + delta[j]) ] & 0xff);
                sx += v * sobelx[i][j];
                sy += v * sobely[i][j];
            }
        }

        return (int) Math.round(Math.sqrt(sx*sx + sy*sy));
    }
    private void drawOnSurface(Bitmap bmp, List<Point> corners) {
        if(cameraSurfaceView != null)
        {
            // draw something
            cameraSurfaceView.setBmp(bmp);
            cameraSurfaceView.setCorners(corners);
        }
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
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setCameraSurfaceView(CameraSurfaceView cameraSurfaceView) {
        this.cameraSurfaceView = cameraSurfaceView;
        Log.w(this.getClass().getName(), "Set surface");
    }
}
