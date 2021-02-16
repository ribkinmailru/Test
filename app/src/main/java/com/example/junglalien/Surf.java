package com.example.junglalien;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

public class Surf extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    public GameThread gameThread;
    public MenuThread menuthread;
    public Surf(Context context) {
        super(context);
        setOnTouchListener(this);
        getHolder().addCallback(this);
    }

    public Surf(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Surf(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        menuthread = new MenuThread(holder, getContext(),this);
        menuthread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && gameThread!=null){
            gameThread.bullets.add(new Enemy(gameThread.bullet, new PointF(gameThread.ship.position.x+17f,gameThread.ship.position.y)));
        }
        if(gameThread==null && menuthread.rect.contains(event.getX(),event.getY())){
            menuthread.StartGame();
        }
        return false;
    }
}
