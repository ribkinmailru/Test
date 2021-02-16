package com.example.junglalien;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

public class Enemy {
    Bitmap enemy;
    PointF position;
    float width;
    float height;
    public Matrix matrix;
    public RectF rect;

    public Enemy(Bitmap enemy,PointF position) {
        this.enemy = enemy;
        height = enemy.getHeight();
        width = enemy.getWidth();
        matrix = new Matrix();
        this.position = position;
        rect = new RectF(position.x,position.y, position.x+width, position.y+height);
        matrix.setTranslate(position.x,position.y);
    }
}
