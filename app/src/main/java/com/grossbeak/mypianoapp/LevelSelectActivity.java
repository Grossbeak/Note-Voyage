package com.grossbeak.mypianoapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import java.io.InputStream;
import com.caverock.androidsvg.SVG;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.TextView;

public class LevelSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select); // layout меню уровней

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

        ImageView lvlMenuBg = findViewById(R.id.lvl_menu_bg);
        try {
            InputStream is = getAssets().open("lvl_menu_bg.png");
            lvlMenuBg.setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (Exception ignored) {}

        // Устанавливаем SVG-иконки для кнопок уровней
        updateStars();

        // Обработчики нажатий на кнопки уровней
        // findViewById(R.id.btn_reset_progress).setOnClickListener(v -> {
        //     SharedPreferences prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
        //     prefs.edit().clear().apply();
        //     updateStars();
        // });

        ImageView btnMainMenu = findViewById(R.id.btn_main_menu);
        setSvgImage(btnMainMenu, "lvlbtn/main_menu.svg");
        btnMainMenu.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                setSvgImage(btnMainMenu, "lvlbtn/main_menu_pressed.svg");
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                setSvgImage(btnMainMenu, "lvlbtn/main_menu.svg");
            }
            return false;
        });
        btnMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(LevelSelectActivity.this, MainMenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        int[] btnIds = {R.id.btn_level_1, R.id.btn_level_2, R.id.btn_level_3, R.id.btn_level_4, R.id.btn_level_5, R.id.btn_level_6, R.id.btn_level_7, R.id.btn_level_8};
        for (int i = 0; i < btnIds.length; i++) {
            final int level = i + 1;
            final ImageView btn = findViewById(btnIds[i]);
            btn.setOnClickListener(v -> {
                // Проверка на разблокировку уровня
                SharedPreferences prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
                int totalStars = 0;
                for (int j = 1; j <= 8; j++) totalStars += prefs.getInt("level_"+j+"_stars", 0);
                boolean unlocked = true;
                if (level == 2 && totalStars < 1) unlocked = false;
                if (level == 3 && totalStars < 3) unlocked = false;
                if (level == 4 && totalStars < 5) unlocked = false;
                if (level == 5 && totalStars < 7) unlocked = false;
                if (level == 6 && totalStars < 9) unlocked = false;
                if (level == 7 && totalStars < 11) unlocked = false;
                if (level == 8 && totalStars < 21) unlocked = false;
                final boolean unlockedFinal = unlocked;
                // Анимация уменьшения
                btn.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).withEndAction(() -> {
                    btn.animate().scaleX(1f).scaleY(1f).setDuration(80).withEndAction(() -> {
                        if (unlockedFinal) {
                            startGame(level);
                        } else {
                            Toast.makeText(this, "Уровень заблокирован", Toast.LENGTH_SHORT).show();
                        }
                    }).start();
                }).start();
            });
        }

        TextView tvChooseLevel = findViewById(R.id.tv_choose_level);
        try {
            android.graphics.Typeface robotoRound = android.graphics.Typeface.createFromAsset(getAssets(), "Roboto-Round-Regular.ttf");
            tvChooseLevel.setTypeface(robotoRound);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void updateStars() {
        SharedPreferences prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
        int[] starCounts = {
            prefs.getInt("level_1_stars", 0),
            prefs.getInt("level_2_stars", 0),
            prefs.getInt("level_3_stars", 0),
            prefs.getInt("level_4_stars", 0),
            prefs.getInt("level_5_stars", 0),
            prefs.getInt("level_6_stars", 0),
            prefs.getInt("level_7_stars", 0),
            prefs.getInt("level_8_stars", 0)
        };
        int[] btnIds = {R.id.btn_level_1, R.id.btn_level_2, R.id.btn_level_3, R.id.btn_level_4, R.id.btn_level_5, R.id.btn_level_6, R.id.btn_level_7, R.id.btn_level_8};
        int totalStars = 0;
        for (int s : starCounts) totalStars += s;
        for (int i = 0; i < 8; i++) {
            ImageView btn = findViewById(btnIds[i]);
            if (i == 1 && totalStars < 1) {
                setSvgImage(btn, "lvlbtn/lvl2_block.svg");
            } else if (i == 2 && totalStars < 3) {
                setSvgImage(btn, "lvlbtn/lvl3_block.svg");
            } else if (i == 3 && totalStars < 5) {
                setSvgImage(btn, "lvlbtn/lvl4_block.svg");
            } else if (i == 4 && totalStars < 7) {
                setSvgImage(btn, "lvlbtn/lvl5_block.svg");
            } else if (i == 5 && totalStars < 9) {
                setSvgImage(btn, "lvlbtn/lvl6_block.svg");
            } else if (i == 6 && totalStars < 11) {
                setSvgImage(btn, "lvlbtn/lvl7_block.svg");
            } else if (i == 7 && totalStars < 21) {
                setSvgImage(btn, "lvlbtn/lvl8_block.svg");
            } else {
                String svgName = "lvlbtn/lvl" + (i+1) + "_" + starCounts[i] + "s.svg";
                setSvgImage(btn, svgName);
            }
        }
    }

    private void setSvgImage(ImageView imageView, String svgAssetPath) {
        try {
            InputStream inputStream = getResources().getAssets().open(svgAssetPath);
            SVG svg = SVG.getFromInputStream(inputStream);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
            imageView.setImageDrawable(drawable);
        } catch (Exception e) {
            imageView.setImageResource(android.R.color.darker_gray);
        }
    }

    private void startGame(int level) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
} 