package com.grossbeak.mypianoapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import com.caverock.androidsvg.SVG;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import java.io.InputStream;
import android.view.MotionEvent;
import android.view.ViewGroup;
import java.util.HashMap;
import androidx.appcompat.widget.AppCompatButton;
import android.content.SharedPreferences;

public class TrainingActivity extends AppCompatActivity {
    private ImageView[] whiteKeys = new ImageView[14];
    private ImageView[] blackKeys = new ImageView[10];
    // MIDI номера для 2 октав: C3 (48) ... B4 (71)
    private int[] midiNumbers = new int[14];
    private int[] midiNumbersBlack = new int[10];
    // Названия нот для белых и чёрных клавиш (по буквам, для SVG)
    private String[] whiteNoteNames = {"c","d","e","f","g","a","h","c","d","e","f","g","a","h"};
    private String[] blackNoteNames = {"c#","d#","f#","g#","a#","c#","d#","f#","g#","a#"};
    private boolean sustainOn = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicManager.pause();
        setContentView(R.layout.activity_training);

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

        // Фон
        ImageView trainingBg = findViewById(R.id.training_bg);
        try {
            InputStream is = getAssets().open("main_menu_bg.png");
            trainingBg.setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (Exception ignored) {}

        // Кнопка назад
        ImageView btnBack = findViewById(R.id.btn_training_back);
        setSvgImage(btnBack, "lvlbtn/back.svg");
        btnBack.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                setSvgImage(btnBack, "lvlbtn/back_pressed.svg");
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                setSvgImage(btnBack, "lvlbtn/back.svg");
            }
            return false;
        });
        btnBack.setOnClickListener(v -> finish());

        AppCompatButton btnSustain = findViewById(R.id.btn_sustain);
        btnSustain.setBackgroundResource(R.drawable.bg_sustain_off);
        // Масштабируем текст в зависимости от высоты кнопки
        btnSustain.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int btnHeight = btnSustain.getHeight();
            if (btnHeight > 0) {
                float textSize = btnHeight * 0.45f; // 45% высоты кнопки
                btnSustain.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        });
        btnSustain.setOnClickListener(v -> {
            sustainOn = !sustainOn;
            if (sustainOn) {
                btnSustain.setBackgroundResource(R.drawable.bg_sustain_on);
            } else {
                btnSustain.setBackgroundResource(R.drawable.bg_sustain_off);
            }
        });

        // Заполняем массивы midi номеров
        // Белые клавиши: C3 (48) ... B4 (71)
        for (int i = 0; i < 14; i++) {
            midiNumbers[i] = 48 + i; // C3=48, D3=50, ..., B4=71
        }
        // Черные клавиши: C#, D#, F#, G#, A# для каждой октавы
        int[] blackOffsets = {1, 3, 6, 8, 10};
        int idx = 0;
        for (int oct = 0; oct < 2; oct++) {
            int base = 48 + oct * 7;
            for (int j = 0; j < blackOffsets.length; j++) {
                midiNumbersBlack[idx++] = base + blackOffsets[j];
            }
        }
        // Белые клавиши
        String[] whiteIds = {
            "key_c3","key_d3","key_e3","key_f3","key_g3","key_a3","key_b3",
            "key_c4","key_d4","key_e4","key_f4","key_g4","key_a4","key_b4"
        };
        String[] whiteSvgNames = {
            "c3","d3","e3","f3","g3","a3","h3",
            "c4","d4","e4","f4","g4","a4","h4"
        };
        for (int i = 0; i < 14; i++) {
            int resId = getResources().getIdentifier(whiteIds[i], "id", getPackageName());
            whiteKeys[i] = findViewById(resId);
            setSvgImage(whiteKeys[i], "piano_keys/white.svg");
            final int midi = midiNumbers[i];
            final String svgName = whiteSvgNames[i];
            whiteKeys[i].setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        setSvgImage((ImageView)v, "piano_keys/" + svgName + "_pressed.svg");
                        playNoteWithFadeOverlay(midi);
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                    case android.view.MotionEvent.ACTION_OUTSIDE:
                        setSvgImage((ImageView)v, "piano_keys/white.svg");
                        break;
                }
                return true;
            });
        }
        // Черные клавиши
        String[] blackIds = {
            "key_c3_sharp","key_d3_sharp","key_f3_sharp","key_g3_sharp","key_a3_sharp",
            "key_c4_sharp","key_d4_sharp","key_f4_sharp","key_g4_sharp","key_a4_sharp"
        };
        String[] blackSvgNames = {
            "c#3","d#3","f#3","g#3","a#3",
            "c#4","d#4","f#4","g#4","a#4"
        };
        for (int i = 0; i < 10; i++) {
            int resId = getResources().getIdentifier(blackIds[i], "id", getPackageName());
            blackKeys[i] = findViewById(resId);
            setSvgImage(blackKeys[i], "piano_keys/black.svg");
            final int midi = midiNumbersBlack[i];
            final String svgName = blackSvgNames[i];
            blackKeys[i].setAlpha(1f);
            blackKeys[i].setOnTouchListener((v, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    if (v instanceof com.grossbeak.mypianoapp.TopGravityImageView) {
                        com.grossbeak.mypianoapp.TopGravityImageView tgv = (com.grossbeak.mypianoapp.TopGravityImageView) v;
                        if (!tgv.isPointInsideDrawable(event.getX(), event.getY())) return false;
                    }
                    setSvgImage((ImageView)v, "piano_keys/" + svgName + "_pressed.svg");
                    playNoteWithFadeOverlay(midi);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL || event.getAction() == android.view.MotionEvent.ACTION_OUTSIDE) {
                    new Handler().postDelayed(() -> setSvgImage((ImageView)v, "piano_keys/black.svg"), 300);
                }
                return true;
            });
        }

        // --- SWIPE по клавишам ---
        View pianoFrame = findViewById(R.id.training_piano_frame);
        // Карта: клавиша -> была ли нажата этим пальцем
        final HashMap<ImageView, Boolean> keyPressed = new HashMap<>();
        // Для быстрого поиска: ImageView -> индекс
        final HashMap<ImageView, Integer> whiteKeyIndex = new HashMap<>();
        final HashMap<ImageView, Integer> blackKeyIndex = new HashMap<>();
        for (int i = 0; i < 14; i++) whiteKeyIndex.put(whiteKeys[i], i);
        for (int i = 0; i < 10; i++) blackKeyIndex.put(blackKeys[i], i);

        pianoFrame.setOnTouchListener((v, event) -> {
            int[] location = new int[2];
            pianoFrame.getLocationOnScreen(location);
            int pointerCount = Math.min(event.getPointerCount(), 10);
            // Для отслеживания, какие клавиши сейчас нажаты какими pointer'ами
            HashMap<ImageView, Integer> keyToPointer = new HashMap<>();
            // Для каждого pointer'а ищем клавишу
            for (int p = 0; p < pointerCount; p++) {
                float x = event.getX(p);
                float y = event.getY(p);
                boolean found = false;
                // Сначала чёрные
                for (int i = 0; i < 10; i++) {
                    ImageView key = blackKeys[i];
                    if (key instanceof com.grossbeak.mypianoapp.TopGravityImageView) {
                        int[] keyLoc = new int[2];
                        int[] frameLoc = new int[2];
                        key.getLocationOnScreen(keyLoc);
                        pianoFrame.getLocationOnScreen(frameLoc);
                        float localX = x + frameLoc[0] - keyLoc[0];
                        float localY = y + frameLoc[1] - keyLoc[1];
                        if (!((com.grossbeak.mypianoapp.TopGravityImageView) key).isPointInsideDrawable(localX, localY)) continue;
                    } else {
                        if (!isPointInsideView(x, y, key, pianoFrame)) continue;
                    }
                    found = true;
                    if (keyPressed.get(key) == null || !keyPressed.get(key)) {
                        String svgName = blackSvgNames[i];
                        setSvgImage(key, "piano_keys/" + svgName + "_pressed.svg");
                        playNoteWithFadeOverlay(midiNumbersBlack[i]);
                        keyPressed.put(key, true);
                    }
                    keyToPointer.put(key, p);
                    break; // только одна чёрная может быть под pointer'ом
                }
                if (!found) {
                    // Белые
                    for (int i = 0; i < 14; i++) {
                        ImageView key = whiteKeys[i];
                        if (isPointInsideView(x, y, key, pianoFrame)) {
                            if (keyPressed.get(key) == null || !keyPressed.get(key)) {
                                String svgName = whiteSvgNames[i];
                                setSvgImage(key, "piano_keys/" + svgName + "_pressed.svg");
                                playNoteWithFadeOverlay(midiNumbers[i]);
                                keyPressed.put(key, true);
                            }
                            keyToPointer.put(key, p);
                            break;
                        }
                    }
                }
            }
            // Отпускаем те клавиши, которые больше не под пальцами
            for (int i = 0; i < 14; i++) {
                ImageView key = whiteKeys[i];
                if ((keyPressed.get(key) != null && keyPressed.get(key)) && !keyToPointer.containsKey(key)) {
                    final ImageView finalKey = key;
                    new Handler().postDelayed(() -> setSvgImage(finalKey, "piano_keys/white.svg"), 300);
                    keyPressed.put(key, false);
                }
            }
            for (int i = 0; i < 10; i++) {
                ImageView key = blackKeys[i];
                if ((keyPressed.get(key) != null && keyPressed.get(key)) && !keyToPointer.containsKey(key)) {
                    final ImageView finalKey = key;
                    new Handler().postDelayed(() -> setSvgImage(finalKey, "piano_keys/black.svg"), 300);
                    keyPressed.put(finalKey, false);
                }
            }
            // Если палец ушёл с клавиатуры — сбросить все
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                for (int i = 0; i < 14; i++) {
                    final ImageView finalKey = whiteKeys[i];
                    new Handler().postDelayed(() -> setSvgImage(finalKey, "piano_keys/white.svg"), 300);
                    keyPressed.put(finalKey, false);
                }
                for (int i = 0; i < 10; i++) {
                    final ImageView finalKey = blackKeys[i];
                    new Handler().postDelayed(() -> setSvgImage(finalKey, "piano_keys/black.svg"), 300);
                    keyPressed.put(finalKey, false);
                }
            }
            return true;
        });

        // Отключаем индивидуальные onTouchListener у клавиш (оставляем только swipe)
        for (int i = 0; i < 14; i++) whiteKeys[i].setOnTouchListener(null);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Для наложения звуков на клавишах
    private void playNoteWithFadeOverlay(int midiNumber) {
        try {
            String fileName = String.format("note_sounds/%03d.wav", midiNumber);
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            // Получаем громкость из настроек
            SharedPreferences prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
            float pianoVolume = prefs.getInt("piano_volume", 100) / 100f;
            mp.setVolume(pianoVolume, pianoVolume);
            mp.prepare();
            mp.start();
            if (sustainOn) {
                mp.setOnCompletionListener(m -> mp.release());
            } else {
                new Handler().postDelayed(() -> {
                    final int fadeDuration = 1500;
                    final int fadeSteps = 20;
                    final float delta = 1f / fadeSteps;
                    final Handler fadeHandler = new Handler();
                    for (int i = 1; i <= fadeSteps; i++) {
                        final float volume = pianoVolume * (1f - (delta * i));
                        fadeHandler.postDelayed(() -> {
                            try { mp.setVolume(volume, volume); } catch (Exception ignored) {}
                        }, i * fadeDuration / fadeSteps);
                    }
                    fadeHandler.postDelayed(() -> {
                        try { if (mp.isPlaying()) mp.stop(); } catch (Exception ignored) {}
                        mp.release();
                    }, fadeDuration + 100);
                }, 1500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // Проверка, находится ли точка (x, y) внутри view
    private boolean isPointInsideView(float x, float y, View view, View container) {
        int[] loc = new int[2];
        int[] contLoc = new int[2];
        view.getLocationOnScreen(loc);
        container.getLocationOnScreen(contLoc);
        float vx = loc[0] - contLoc[0];
        float vy = loc[1] - contLoc[1];
        float vw = view.getWidth();
        float vh = view.getHeight();
        return x >= vx && x <= (vx + vw) && y >= vy && y <= (vy + vh);
    }
} 