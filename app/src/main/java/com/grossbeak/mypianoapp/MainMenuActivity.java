package com.grossbeak.mypianoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.graphics.drawable.Drawable;
import com.caverock.androidsvg.SVG;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

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
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageView mainMenuBg = findViewById(R.id.main_menu_bg);
        try {
            InputStream is = getAssets().open("main_menu_bg.png");
            mainMenuBg.setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (Exception ignored) {}

        ImageView btnPlay = findViewById(R.id.btn_play);
        ImageView btnTraining = findViewById(R.id.btn_training);
        ImageView btnAbout = findViewById(R.id.btn_about);
        ImageView decorTop = findViewById(R.id.decor_top);
        ImageView decorBottom = findViewById(R.id.decor_bottom);
        ImageView menuSplash = findViewById(R.id.menu_splash);
        FrameLayout centerMenuFrame = findViewById(R.id.center_menu_frame);

        setSvgImage(btnPlay, "menubtn/play.svg");
        setSvgImage(btnTraining, "menubtn/training.svg");
        setSvgImage(btnAbout, "menubtn/about.svg");
        setSvgImage(decorTop, "menubtn/black_decor.svg");
        setSvgImage(decorBottom, "menubtn/black_decor.svg");
        setSvgImage(menuSplash, "menubtn/menu_splash.svg");

        btnPlay.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setSvgImage(btnPlay, "menubtn/play_pressed.svg");
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                setSvgImage(btnPlay, "menubtn/play.svg");
            }
            return false;
        });
        btnTraining.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setSvgImage(btnTraining, "menubtn/training_pressed.svg");
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                setSvgImage(btnTraining, "menubtn/training.svg");
            }
            return false;
        });
        btnAbout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setSvgImage(btnAbout, "menubtn/about_pressed.svg");
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                setSvgImage(btnAbout, "menubtn/about.svg");
            }
            return false;
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, LevelSelectActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, TrainingActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        centerMenuFrame.setOnApplyWindowInsetsListener((v, insets) -> {
            int leftInset = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                if (insets.getDisplayCutout() != null) {
                    leftInset = insets.getDisplayCutout().getSafeInsetLeft();
                }
            }
            // 8dp в px
            int minInset = (int) (8 * getResources().getDisplayMetrics().density + 0.5f);
            int finalInset = Math.max(leftInset, minInset);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) v.getLayoutParams();
            params.setMarginStart(finalInset);
            v.setLayoutParams(params);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.play(this);
        // Удаляю fade-анимацию, чтобы не перекрывать slide
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    private void setSvgImage(ImageView imageView, String svgAssetPath) {
        try {
            InputStream inputStream = getAssets().open(svgAssetPath);
            SVG svg = SVG.getFromInputStream(inputStream);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
            imageView.setImageDrawable(drawable);
        } catch (Exception e) {
            imageView.setImageResource(android.R.color.darker_gray);
        }
    }
} 