package com.grossbeak.mypianoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SnowfallView extends View {
    private static final String TAG = "SnowfallView";
    private static final int SNOWFLAKE_SIZE = 48; // px
    private static final int SPAWN_INTERVAL = 400; // ms
    private static final int FRAME_DELAY = 16; // ms (~60fps)

    private Bitmap snowflakeBitmap;
    private final List<Snowflake> snowflakes = new ArrayList<>();
    private final Random random = new Random();
    private long lastSpawnTime = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = true;

    public SnowfallView(Context context) {
        super(context);
        init(context);
    }

    public SnowfallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnowfallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Log.d(TAG, "init called");
        new Thread(() -> {
            try {
                InputStream inputStream = context.getAssets().open("droptile.svg");
                SVG svg = SVG.getFromInputStream(inputStream);
                Bitmap bmp = Bitmap.createBitmap(SNOWFLAKE_SIZE, SNOWFLAKE_SIZE, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                svg.setDocumentWidth(SNOWFLAKE_SIZE);
                svg.setDocumentHeight(SNOWFLAKE_SIZE);
                svg.renderToCanvas(canvas);
                snowflakeBitmap = bmp;
                postInvalidate();
                Log.d(TAG, "SVG loaded successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error loading SVG: " + e.getMessage());
            }
        }).start();
        startAnimation();
    }

    private void startAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    invalidate();
                    handler.postDelayed(this, FRAME_DELAY);
                }
            }
        }, FRAME_DELAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long now = System.currentTimeMillis();
        if (snowflakeBitmap != null && now - lastSpawnTime > SPAWN_INTERVAL) {
            spawnSnowflake();
            lastSpawnTime = now;
        }
        Iterator<Snowflake> iterator = snowflakes.iterator();
        while (iterator.hasNext()) {
            Snowflake snowflake = iterator.next();
            snowflake.y += snowflake.speed;
            snowflake.x += snowflake.drift;
            snowflake.angle += snowflake.rotationSpeed;
            snowflake.alpha = (int) (255 * (1 - (snowflake.y / (float) getHeight())));
            if (snowflake.alpha < 0) snowflake.alpha = 0;
            Paint paint = new Paint();
            paint.setAlpha(snowflake.alpha);
            Matrix matrix = new Matrix();
            matrix.postTranslate(-snowflakeBitmap.getWidth() / 2f, -snowflakeBitmap.getHeight() / 2f);
            matrix.postRotate(snowflake.angle);
            matrix.postTranslate(snowflake.x, snowflake.y);
            canvas.drawBitmap(snowflakeBitmap, matrix, paint);
            if (snowflake.y > getHeight() + SNOWFLAKE_SIZE) {
                iterator.remove();
            }
        }
    }

    private void spawnSnowflake() {
        if (getWidth() == 0 || getHeight() == 0) return;
        float x = random.nextInt(getWidth() - SNOWFLAKE_SIZE) + SNOWFLAKE_SIZE / 2f;
        float y = -SNOWFLAKE_SIZE;
        float speed = 2f + random.nextFloat() * 3f;
        float drift = -0.5f + random.nextFloat();
        float angle = random.nextFloat() * 360f;
        float rotationSpeed = -1f + random.nextFloat() * 2f;
        snowflakes.add(new Snowflake(x, y, speed, drift, angle, rotationSpeed, 255));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        running = false;
        handler.removeCallbacksAndMessages(null);
    }

    private static class Snowflake {
        float x, y, speed, drift, angle, rotationSpeed;
        int alpha;
        Snowflake(float x, float y, float speed, float drift, float angle, float rotationSpeed, int alpha) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.drift = drift;
            this.angle = angle;
            this.rotationSpeed = rotationSpeed;
            this.alpha = alpha;
        }
    }
}





