package com.example.junglalien;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;

public class GameThread extends Thread {
    public boolean run;
    SurfaceHolder surfaceHolder;
    ArrayList<Enemy> enemies;
    public ArrayList<Enemy> bullets;
    Bitmap f_ship, f_enemy, bullet, bk,live;
    Ship ship;
    long prev, prev1, spawn_time;
    boolean move;
    int direction;
    Matrix back, back2;
    Paint test;
    float speed_fall;
    float max_spawn_time;
    float min_spawn_time;
    float first;
    Surf surf;
    boolean moved;
    Matrix live1,live2,live3;
    Context context;

    public GameThread(SurfaceHolder surfaceHolder, Context context, Surf surf) {
        this.context = context;
        this.surf = surf;
        this.surfaceHolder = surfaceHolder;
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        run = true;
        speed_fall = 4f;
        GameManager.score = 0;
        f_ship = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ufo), 100, 70, true);
        f_enemy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.egg), 60, 80, true);
        live = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.egg), 40, 50, true);
        bullet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lazer), 60, 110, true);
        bk = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bk), GameActivity.width, GameActivity.height, true);
        ship = new Ship(f_ship);
        spawn_time = 1500;
        max_spawn_time = 800;
        min_spawn_time = 300;
        back = new Matrix();
        back2 = new Matrix();
        live1 = new Matrix();
        live2 = new Matrix();
        live3 = new Matrix();
        live1.setTranslate(GameActivity.width-3*live.getWidth()-20f,20);
        live2.setTranslate(GameActivity.width-2*live.getWidth()-10f,20);
        live3.setTranslate(GameActivity.width-live.getWidth(),20);
        back2.setTranslate(0, -GameActivity.height);
        SensorManager sensor = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor acselerometr = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        GameManager.lives = 3;
        test = new Paint();
        test.setColor(Color.WHITE);
        test.setTextSize(40);
        sensor.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[0] > 1) {
                    move = true;
                    direction = -1;
                } else if (event.values[0] < -1) {
                    move = true;
                    direction = 1;
                } else {
                    move = false;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, acselerometr, SensorManager.SENSOR_DELAY_GAME);
    }

    public void run() {
        Canvas canvas;
        while (run) {
            long now = System.currentTimeMillis();
            long diff = now - prev;
            long diff1 = now - prev1;
            if (diff > spawn_time) {
                prev = now;
                spawn_time = (long) (min_spawn_time + Math.random() * max_spawn_time);
                enemies.add(new Enemy(f_enemy, new PointF((float) (Math.random() * (GameActivity.width - f_enemy.getWidth())), -f_enemy.getHeight())));
            }
            if (diff1 > 10) {
                back.postTranslate(0, speed_fall);
                back2.postTranslate(0, speed_fall);
                first += speed_fall;
                if (first > GameActivity.height && !moved) {
                    Log.d("first", String.valueOf(first));
                    float dif = first - GameActivity.height;
                    Log.d("dif", String.valueOf(dif));
                    back.setTranslate(0, -GameActivity.height + dif);
                    moved = true;
                } else if (first > 2 * GameActivity.height) {
                    Log.d("first", String.valueOf(first));
                    float dif = first - 2 * GameActivity.height;
                    Log.d("dif", String.valueOf(dif));
                    back2.setTranslate(0, -GameActivity.height + dif);
                    first = dif;
                    moved = false;
                }
                prev1 = diff1;
                float t = direction * 14;
                if (move && ship.position.x + t < GameActivity.width - ship.width && ship.position.x + t > 0) {
                    ship.matrix.postTranslate(t, 0);
                    ship.rect.offset(t, 0);
                    ship.position.offset(t, 0);
                }
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                    if (enemy.position.y > GameActivity.height - enemy.height) {
                        enemies.remove(enemy);
                        GameOver();
                    }
                    enemy.matrix.postTranslate(0, speed_fall);
                    enemy.position.offset(0, speed_fall);
                    enemy.rect.offset(0, speed_fall);
                }
                for (int i = 0; i < bullets.size(); i++) {
                    Enemy enemy = bullets.get(i);
                    enemy.matrix.postTranslate(0, -4*speed_fall);
                    enemy.position.offset(0, -4*speed_fall);
                    enemy.rect.offset(0, -4*speed_fall);
                    if (enemy.position.y < 0 - enemy.height) {
                        bullets.remove(enemy);
                    }
                    for (int p = 0; p < enemies.size(); p++) {
                        Enemy s = enemies.get(p);
                        if (s.rect.intersect(enemy.rect)) {
                            enemies.remove(s);
                            bullets.remove(enemy);
                            speed_fall += 0.06;
                            if (min_spawn_time > 50) {
                                min_spawn_time -= 1;
                                max_spawn_time -= 1;
                            }
                            GameManager.score++;
                        }
                    }
                }
            }

            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    canvas.drawBitmap(bk, back, null);
                    canvas.drawBitmap(bk, back2, null);
                    for (Enemy i : enemies) {
                        canvas.drawBitmap(i.enemy, i.matrix, null);
                    }
                    for (int i = 0; i < bullets.size(); i++) {
                        Enemy enemy = bullets.get(i);
                        canvas.drawBitmap(enemy.enemy, enemy.matrix, null);
                    }
                    canvas.drawBitmap(ship.ship, ship.matrix, null);
                    if(GameManager.lives==3){
                        canvas.drawBitmap(live, live1, null);
                        canvas.drawBitmap(live, live2, null);
                        canvas.drawBitmap(live, live3, null);
                    }else if(GameManager.lives==2){
                        canvas.drawBitmap(live, live2, null);
                        canvas.drawBitmap(live, live3, null);
                    }else if(GameManager.lives == 1){
                        canvas.drawBitmap(live, live3, null);
                    }else{
                        run = false;
                        surf.menuthread = new MenuThread(surfaceHolder,context,surf);
                        surf.menuthread.start();
                        surf.gameThread = null;
                    }
                    canvas.drawText("Score: " + GameManager.score, 35, 50, test);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }


    public void GameOver() {
        GameManager.lives--;
    }
}
