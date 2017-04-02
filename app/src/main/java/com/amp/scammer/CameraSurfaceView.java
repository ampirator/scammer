package com.amp.scammer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by ampirator on 09.04.2016.
 */
public class CameraSurfaceView extends SurfaceView {

    private Bitmap bmp;
    private Paint bmpPaint;
    private Matrix matrix;
    private List<Point> corners;

    public CameraSurfaceView(Context context) {
        super(context);
        bmpPaint = new Paint();
        bmpPaint.setColor(Color.GREEN);
        bmpPaint.setStyle(Paint.Style.STROKE);

        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (bmp == null) {
            return;
        }

        int ky = canvas.getHeight() / bmp.getWidth();

        int kx = canvas.getWidth() / bmp.getHeight();

        matrix = new Matrix();
       // ;
       // canvas.translate( 0,-canvas.getHeight());

        //canvas.rotate(90,0,  0);


      //  canvas.translate( 0,canvas.getHeight()/2);
        canvas.rotate(90,canvas.getWidth() /2,  canvas.getWidth()/2);
        //matrix.setRotate(90, bmp.getHeight() / 2, bmp.getHeight() / 2);
        canvas.drawBitmap(bmp, 0, 0, bmpPaint);
        for(Point p: corners) {
            canvas.drawRect(p.x - 5, p.y - 5, p.x + 5, p.y + 5, bmpPaint);
        }
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
        postInvalidate();
    }

    public void setCorners(List<Point> corners) {
        this.corners = corners;
    }
}
