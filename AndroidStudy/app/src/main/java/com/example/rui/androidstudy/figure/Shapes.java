package com.example.rui.androidstudy.figure;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by zonelue003 on 2017/7/7.
 */

abstract class Shapes {
    abstract void onDraw(Canvas canvas, Paint paint);
}

class Line extends Shapes {
    float startX;
    float startY;
    float stopX;
    float stopY;

    public Line(float startX, float startY, float stopX, float stopY) {
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    @Override
    void onDraw(Canvas canvas, Paint paint) {
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}

class Rect extends Shapes {
    float left;
    float top;
    float right;
    float bottom;

    public Rect(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    void onDraw(Canvas canvas, Paint paint) {
        canvas.drawRect(left, top, right, bottom, paint);
    }
}