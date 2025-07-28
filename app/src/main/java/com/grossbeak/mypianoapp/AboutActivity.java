package com.grossbeak.mypianoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    private Button btnDebug;
    private boolean debugEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        setContentView(root);

        btnDebug = new Button(this);
        btnDebug.setText("Enable debug");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(0,0,32,32);
        btnDebug.setLayoutParams(params);
        root.addView(btnDebug);

        SharedPreferences prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
        debugEnabled = prefs.getBoolean("debug_note_hint", false);
        updateButton();

        btnDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugEnabled = !debugEnabled;
                prefs.edit().putBoolean("debug_note_hint", debugEnabled).apply();
                updateButton();
            }
        });
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

    private void updateButton() {
        btnDebug.setText(debugEnabled ? "Disable debug" : "Enable debug");
    }
} 