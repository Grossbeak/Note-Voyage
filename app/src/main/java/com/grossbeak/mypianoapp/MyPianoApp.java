package com.grossbeak.mypianoapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

public class MyPianoApp extends Application {
    private int started = 0;
    private int resumed = 0;
    private boolean inMenu = false;
    private Handler pauseHandler = new Handler();
    private Runnable pauseRunnable = null;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            @Override
            public void onActivityStarted(Activity activity) {
                started++;
            }
            @Override
            public void onActivityResumed(Activity activity) {
                resumed++;
                // В меню — только если это одно из меню-активити
                inMenu = activity instanceof MainMenuActivity || activity instanceof LevelSelectActivity || activity instanceof AboutActivity || activity instanceof LevelCompleteActivity;
                if (inMenu) {
                    MusicManager.play(activity);
                    MusicManager.applySavedVolume(activity);
                }
                // Отменяем отложенную паузу, если она была запланирована
                if (pauseRunnable != null) {
                    pauseHandler.removeCallbacks(pauseRunnable);
                    pauseRunnable = null;
                }
            }
            @Override
            public void onActivityPaused(Activity activity) {
                resumed--;
                // Если все активити ушли в onPause (приложение свернуто)
                if (resumed == 0) {
                    // Запускаем отложенную паузу
                    pauseRunnable = new Runnable() {
                        @Override
                        public void run() {
                            MusicManager.pause();
                        }
                    };
                    pauseHandler.postDelayed(pauseRunnable, 500); // 500 мс задержка
                }
            }
            @Override
            public void onActivityStopped(Activity activity) {
                started--;
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }
} 