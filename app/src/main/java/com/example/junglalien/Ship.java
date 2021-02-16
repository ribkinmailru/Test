package com.example.junglalien;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

public class Ship {
    Bitmap ship;
    PointF position;
    public Matrix matrix;
    float width;
    float height;
    public RectF rect;
    public Ship(Bitmap ship){
        this.ship = ship;
        width = ship.getWidth();
        height = ship.getHeight();
        position = new PointF(GameActivity.width/2f-ship.getWidth()/2f,GameActivity.height-height-10f);
        matrix = new Matrix();
        matrix.setTranslate(position.x, position.y);
        rect = new RectF(position.x,position.y, position.x+width, position.y+height);
    }
}
