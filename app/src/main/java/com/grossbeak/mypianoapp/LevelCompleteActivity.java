package com.grossbeak.mypianoapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.os.Handler;
import android.content.Intent;

public class LevelCompleteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_complete);

        // Включаем полноэкранный режим
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            decorView.getWindowInsetsController().hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            decorView.getWindowInsetsController().setSystemBarsBehavior(
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(flags);
        }

        ImageView bg = findViewById(R.id.level_complete_bg);
        try {
            bg.setImageBitmap(android.graphics.BitmapFactory.decodeStream(getAssets().open("lvl_menu_bg.png")));
        } catch (Exception ignored) {}

        int level = getIntent().getIntExtra("level", 0);
        View overlay = findViewById(R.id.overlay_dark);
        TextView tvQuote = findViewById(R.id.tv_quote);
        TextView tvComplete = findViewById(R.id.tv_level_complete);

        if (level == 8) {
            // Вместо затемнения и цитаты — возвращаемся в GameActivity с флагом
            Intent intent = new Intent(LevelCompleteActivity.this, GameActivity.class);
            intent.putExtra("show_final_effect", true);
            intent.putExtra("level", 8);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            overlay.setVisibility(View.GONE);
            tvQuote.setVisibility(View.GONE);
            tvComplete.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(LevelCompleteActivity.this, LevelSelectActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 1500);
        }
    }

    private void playNote(int midiNumber) {
        try {
            String fileName = String.format("note_sounds/%03d.wav", midiNumber);
            android.content.res.AssetFileDescriptor afd = getAssets().openFd(fileName);
            android.media.MediaPlayer mp = new android.media.MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            // Используем громкость пианино из настроек
            android.content.SharedPreferences prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
            float pianoVolume = prefs.getInt("piano_volume", 100) / 100f;
            mp.setVolume(pianoVolume, pianoVolume);
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(m -> mp.release());
        } catch (Exception e) {
            e.printStackTrace();
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
        // MusicManager.pause(); // удалено, чтобы музыка не прерывалась при переходах между меню
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
} 