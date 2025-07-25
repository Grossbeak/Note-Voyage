package com.grossbeak.mypianoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import java.io.InputStream;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    private SeekBar seekMusic, seekPiano;
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
        seekMusic = findViewById(R.id.seek_music_volume);
        seekPiano = findViewById(R.id.seek_piano_volume);

        // Фон
        ImageView settingsBg = findViewById(R.id.settings_bg);
        try {
            InputStream is = getAssets().open("main_menu_bg.png");
            android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeStream(is);
            settingsBg.setImageBitmap(bmp);
        } catch (Exception ignored) {}

        // Кнопка назад
        com.grossbeak.mypianoapp.HeightFitImageView btnBack = findViewById(R.id.btn_settings_back);
        try {
            InputStream inputStream = getAssets().open("lvlbtn/back.svg");
            com.caverock.androidsvg.SVG svg = com.caverock.androidsvg.SVG.getFromInputStream(inputStream);
            android.graphics.drawable.Drawable drawable = new android.graphics.drawable.PictureDrawable(svg.renderToPicture());
            btnBack.setLayerType(com.grossbeak.mypianoapp.HeightFitImageView.LAYER_TYPE_SOFTWARE, null);
            btnBack.setImageDrawable(drawable);
        } catch (Exception ignored) {}
        btnBack.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                try {
                    InputStream inputStream = getAssets().open("lvlbtn/back_pressed.svg");
                    com.caverock.androidsvg.SVG svg = com.caverock.androidsvg.SVG.getFromInputStream(inputStream);
                    android.graphics.drawable.Drawable drawable = new android.graphics.drawable.PictureDrawable(svg.renderToPicture());
                    btnBack.setImageDrawable(drawable);
                } catch (Exception ignored) {}
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                try {
                    InputStream inputStream = getAssets().open("lvlbtn/back.svg");
                    com.caverock.androidsvg.SVG svg = com.caverock.androidsvg.SVG.getFromInputStream(inputStream);
                    android.graphics.drawable.Drawable drawable = new android.graphics.drawable.PictureDrawable(svg.renderToPicture());
                    btnBack.setImageDrawable(drawable);
                } catch (Exception ignored) {}
            }
            return false;
        });
        btnBack.setOnClickListener(v -> finish());

        seekMusic.setMax(100);
        seekPiano.setMax(100);
        seekMusic.setProgress(prefs.getInt("music_volume", 100));
        seekPiano.setProgress(prefs.getInt("piano_volume", 100));
        // Кастомный чекбокс debug
        ImageView checkboxDebug = findViewById(R.id.checkbox_debug);
        boolean debugEnabled = prefs.getBoolean("debug_note_hint", false);
        checkboxDebug.setImageResource(debugEnabled ? R.drawable.checkbox_debug_checked : R.drawable.checkbox_debug_unchecked);
        checkboxDebug.setOnClickListener(v -> {
            boolean newValue = !prefs.getBoolean("debug_note_hint", false);
            prefs.edit().putBoolean("debug_note_hint", newValue).apply();
            checkboxDebug.setImageResource(newValue ? R.drawable.checkbox_debug_checked : R.drawable.checkbox_debug_unchecked);
        });
        // Применяем сохранённую громкость музыки при запуске
        MusicManager.setMusicVolume(seekMusic.getProgress() / 100f);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt("music_volume", progress).apply();
                MusicManager.setMusicVolume(progress / 100f);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekPiano.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt("piano_volume", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        // Кнопка сброса прогресса (SVG)
        com.grossbeak.mypianoapp.HeightFitImageView btnReset = findViewById(R.id.btn_reset_progress);
        try {
            InputStream inputStream = getAssets().open("menubtn/reset.svg");
            com.caverock.androidsvg.SVG svg = com.caverock.androidsvg.SVG.getFromInputStream(inputStream);
            android.graphics.drawable.Drawable drawable = new android.graphics.drawable.PictureDrawable(svg.renderToPicture());
            btnReset.setLayerType(com.grossbeak.mypianoapp.HeightFitImageView.LAYER_TYPE_SOFTWARE, null);
            btnReset.setImageDrawable(drawable);
        } catch (Exception ignored) {}
        btnReset.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                try {
                    InputStream inputStream = getAssets().open("menubtn/reset_pressed.svg");
                    com.caverock.androidsvg.SVG svg = com.caverock.androidsvg.SVG.getFromInputStream(inputStream);
                    android.graphics.drawable.Drawable drawable = new android.graphics.drawable.PictureDrawable(svg.renderToPicture());
                    btnReset.setImageDrawable(drawable);
                } catch (Exception ignored) {}
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                try {
                    InputStream inputStream = getAssets().open("menubtn/reset.svg");
                    com.caverock.androidsvg.SVG svg = com.caverock.androidsvg.SVG.getFromInputStream(inputStream);
                    android.graphics.drawable.Drawable drawable = new android.graphics.drawable.PictureDrawable(svg.renderToPicture());
                    btnReset.setImageDrawable(drawable);
                } catch (Exception ignored) {}
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    // Сбрасываем только прогресс по уровням, не трогаем громкость и debug
                    SharedPreferences.Editor editor = prefs.edit();
                    for (int i = 1; i <= 8; i++) {
                        editor.remove("level_" + i + "_stars");
                    }
                    editor.apply();
                    Toast.makeText(this, "Progress reset", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        // Включаем полноэкранный режим
        View decorView = getWindow().getDecorView();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            decorView.getWindowInsetsController().hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
            decorView.getWindowInsetsController().setSystemBarsBehavior(
                android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(flags);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.play(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
} 