package com.example.junglalien;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.SurfaceHolder;

public class MenuThread extends Thread {
    public boolean run;
    long prev;
    SurfaceHolder surfaceHolder;
    Bitmap bk,egg;
    Matrix matrix1,matrix2,eggs;
    int change;
    Surf surf;
    Context context;
    public RectF rect;

    public MenuThread(SurfaceHolder holder, Context context, Surf surf) {
        this.context = context;
        surfaceHolder = holder;
        this.surf = surf;
        run = true;
        bk = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.bk),GameActivity.width,GameActivity.height,true);
        egg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.egg),180,220,true);
        matrix1 = new Matrix();
        matrix2 = new Matrix();
        eggs = new Matrix();
        eggs.setTranslate(GameActivity.width/2f-egg.getWidth()/2f, GameActivity.height/2f-egg.getHeight()/2f);
        rect = new RectF(GameActivity.width/2f-egg.getWidth()/2f,GameActivity.height/2f-egg.getHeight()/2f, GameActivity.width/2f-egg.getWidth()/2f+egg.getWidth(),GameActivity.height/2f-egg.getHeight()/2f+egg.getHeight());
        matrix2.setTranslate(0, -GameActivity.height);
    }

    public void run(){
        Canvas canvas;
        while (run) {
            long now = System.currentTimeMillis();
            long diff = now - prev;
            if (diff > 30) {
                prev = now;
                matrix1.postTranslate(0, GameActivity.height / 200f);
                matrix2.postTranslate(0, GameActivity.height / 200f);
                change++;
                if (change == 200) {
                    matrix1.setTranslate(0,-GameActivity.height);
                } else if (change == 400) {
                    matrix2.setTranslate(0,-GameActivity.height);
                    change=0;
                }
            }
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    canvas.drawBitmap(bk, matrix1, null);
                    canvas.drawBitmap(bk, matrix2, null);
                    canvas.drawBitmap(egg, eggs,null);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }


    public void StartGame(){
        run = false;
        surf.gameThread = new GameThread(surfaceHolder, context,surf);
        surf.gameThread.start();
    }
}
