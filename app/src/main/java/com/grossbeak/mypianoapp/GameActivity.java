package com.grossbeak.mypianoapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import java.io.InputStream;
import com.caverock.androidsvg.SVG;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.content.res.AssetFileDescriptor;
import com.grossbeak.mypianoapp.HeightFitImageView;
import android.view.ViewGroup;
import androidx.core.content.res.ResourcesCompat;

public class GameActivity extends AppCompatActivity {
    private int level = 1;
    private TextView tvInfo;
    private int[] keyIds = {R.id.key_c, R.id.key_d, R.id.key_e, R.id.key_f, R.id.key_g, R.id.key_a, R.id.key_b};
    // Вместо soundIds используем midi-номера нот
    private int[] midiNumbers = {60, 62, 64, 65, 67, 69, 71}; // C4, D4, E4, F4, G4, A4, B4
    private List<Integer> sequence = new ArrayList<>();
    private int userStep = 0;
    private boolean isGameActive = false;
    private Random random = new Random();
    private ImageView[] whiteKeys;
    private String[] whiteKeyNames = {"c", "d", "e", "f", "g", "a", "h"};
    private ImageView[] blackKeys;
    private int attemptsLeft = 3;
    private int correctAnswers = 0;
    private int currentNoteIdx = -1;
    private String[] noteNames = {"C", "D", "E", "F", "G", "A", "H"}; // B -> H для отображения
    private int[] enabledWhiteKeysForLevel = {0, 1, 2}; // только C, D, E на 1 уровне
    private int[] enabledWhiteKeysForLevel2 = {3, 4, 5, 6}; // F, G, A, B
    private TextView tvDebugNote, tvNotesLeft;
    private HeightFitImageView btnPlayNote;
    private int notesToGuess = 3;
    private int notesGuessed = 0;
    private int notesTotal = 3;
    private int[] notesSequence;
    private boolean waitingForGuess = false;
    private MediaPlayer currentPlayer;
    // Универсальный MediaPlayer для fade-out (чтобы не было наложения)
    private MediaPlayer fadePlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicManager.pause();
        setContentView(R.layout.activity_game);

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
        level = getIntent().getIntExtra("level", 1);
        // 1. Сначала инициализация notesTotal, notesToGuess, notesGuessed
        if (level == 3) {
            notesTotal = 4;
            notesToGuess = 4;
            notesGuessed = 0;
        } else if (level == 2) {
            notesTotal = 4;
            notesToGuess = 4;
            notesGuessed = 0;
        } else if (level == 4) {
            notesTotal = 7;
            notesToGuess = 7;
            notesGuessed = 0;
        } else if (level == 5) {
            notesTotal = 4;
            notesToGuess = 4;
            notesGuessed = 0;
        } else if (level == 6) {
            notesTotal = 4;
            notesToGuess = 4;
            notesGuessed = 0;
        } else if (level == 7) {
            notesTotal = 4;
            notesToGuess = 4;
            notesGuessed = 0;
        } else if (level == 8) {
            notesTotal = 7;
            notesToGuess = 7;
            notesGuessed = 0;
        } else {
            notesTotal = 3;
            notesToGuess = 3;
            notesGuessed = 0;
        }
        // 2. Затем массивы notesSequence, whiteKeys, blackKeys, svg
        notesSequence = new int[notesTotal];
        whiteKeys = new ImageView[] {
            findViewById(R.id.key_c),
            findViewById(R.id.key_d),
            findViewById(R.id.key_e),
            findViewById(R.id.key_f),
            findViewById(R.id.key_g),
            findViewById(R.id.key_a),
            findViewById(R.id.key_b)
        };
        blackKeys = new ImageView[] {
            findViewById(R.id.key_c_sharp),
            findViewById(R.id.key_d_sharp),
            findViewById(R.id.key_f_sharp),
            findViewById(R.id.key_g_sharp),
            findViewById(R.id.key_a_sharp)
        };
        String[] whiteSvgNames = {"c4", "d4", "e4", "f4", "g4", "a4", "h4"};
        String[] blackSvgNames = {"c#4", "d#4", "f#4", "g#4", "a#4"};
        boolean[] whiteEnabled = new boolean[whiteKeys.length];
        boolean[] blackEnabled = new boolean[blackKeys.length];
        // 3. Теперь можно использовать массивы в циклах и обработчиках
        tvInfo = findViewById(R.id.tv_info);
        tvDebugNote = findViewById(R.id.tv_debug_note);
        tvNotesLeft = findViewById(R.id.tv_notes_left);
        // Удалён код установки кастомного шрифта, теперь используется xml
        tvNotesLeft.setText("Notes left: " + notesToGuess);
        int prevNote = -1;
        for (int i = 0; i < notesTotal; i++) {
            int[] available;
            if (level == 1) available = enabledWhiteKeysForLevel;
            else if (level == 2) available = enabledWhiteKeysForLevel2;
            else if (level == 3) available = new int[]{0, 1, 2, 100, 101}; // 0:C, 1:D, 2:E, 100:C#, 101:D#
            else if (level == 4) available = new int[]{0, 1, 100, 101}; // 0:C, 1:D, 100:C#, 101:D#
            else if (level == 5) available = new int[]{3, 4, 5, 6, 102, 103, 104}; // 3:F, 4:G, 5:A, 6:H, 102:F#, 103:G#, 104:A#
            else if (level == 6) available = new int[]{0, 1, 2, 3, 4, 5, 6}; // все белые
            else if (level == 7) available = new int[]{0, 1, 2, 3, 4, 5, 6, 100, 101, 102, 103, 104}; // все белые и все чёрные
            else if (level == 8) available = new int[]{0, 1, 2, 3, 4, 5, 6, 100, 101, 102, 103, 104}; // все белые и все чёрные
            else available = new int[]{0,1,2,3,4,5,6};
            List<Integer> pool = new ArrayList<>();
            for (int n : available) {
                if (n != prevNote) pool.add(n);
            }
            int note;
            if (level == 3) {
                int idx = (int)(Math.random() * pool.size());
                note = pool.get(idx);
            } else {
                note = pool.get((int)(Math.random() * pool.size()));
            }
            notesSequence[i] = note;
            prevNote = note;
        }
        tvInfo.setText("Level: " + level);
        // tvAttempts.setText("Попытки: 3"); // удалено, счетчик попыток больше не используется
        SharedPreferences debugPrefs = getSharedPreferences("piano_game", MODE_PRIVATE);
        boolean debugEnabled = debugPrefs.getBoolean("debug_note_hint", false);
        tvDebugNote.setText("");

        ImageView gameBg = findViewById(R.id.game_bg);
        try {
            InputStream is = getAssets().open("lvl_menu_bg.png");
            gameBg.setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (Exception ignored) {}

        // Белые клавиши теперь ImageView
        // Включаем только нужные белые клавиши и делаем их полупрозрачными если неактивны
        for (int i = 0; i < whiteKeys.length; i++) {
            boolean enabled = false;
            if (level == 1) enabled = (i >= 0 && i <= 2); // C, D, E
            else if (level == 2) enabled = (i >= 3 && i <= 6); // F, G, A, H
            else if (level == 3) enabled = (i >= 0 && i <= 2); // C, D, E
            else if (level == 4) enabled = (i >= 0 && i <= 2); // C, D, E (белые)
            else if (level == 5) enabled = (i >= 3 && i <= 6); // F, G, A, H (белые)
            else if (level == 6) enabled = true; // все белые активны
            else if (level == 7) enabled = true; // все белые активны
            else if (level == 8) enabled = true; // все белые активны
            else enabled = true;
            whiteEnabled[i] = enabled;
            whiteKeys[i].setEnabled(enabled);
            if (!enabled) {
                setSvgImage(whiteKeys[i], "piano_keys/gray.svg");
                whiteKeys[i].setAlpha(1f);
            } else {
                setSvgImage(whiteKeys[i], "piano_keys/white.svg");
                whiteKeys[i].setAlpha(1f);
            }
        }
        // Обработка нажатий на белые клавиши
        for (int i = 0; i < whiteKeys.length; i++) {
            final int idx = i;
            final String svgName = whiteSvgNames[i];
            whiteKeys[i].setOnTouchListener((v, event) -> {
                if (!whiteKeys[idx].isEnabled()) return false;
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        setSvgImage(whiteKeys[idx], "piano_keys/" + svgName + "_pressed.svg");
                        playNoteWithFadeOverlay(midiNumbers[idx]);
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        new Handler().postDelayed(() -> setSvgImage(whiteKeys[idx], whiteEnabled[idx] ? "piano_keys/white.svg" : "piano_keys/gray.svg"), 300);
                        if (waitingForGuess) {
                            boolean correct = false;
                            if (level == 3) {
                                // Белые: idx 0,1,2 — C,D,E
                                if ((idx == 0 && currentNoteIdx == 0) ||
                                    (idx == 1 && currentNoteIdx == 1) ||
                                    (idx == 2 && currentNoteIdx == 2)) {
                                    correct = true;
                                }
                            } else if (level == 4) {
                                // Белые: idx 0,1,2,3 — C,D,E,F
                                if ((idx == 0 && currentNoteIdx == 0) ||
                                    (idx == 1 && currentNoteIdx == 1) ||
                                    (idx == 2 && currentNoteIdx == 2)) {
                                    correct = true;
                                }
                            } else if (level == 5) {
                                if ((idx == 3 && currentNoteIdx == 3) ||
                                    (idx == 4 && currentNoteIdx == 4) ||
                                    (idx == 5 && currentNoteIdx == 5) ||
                                    (idx == 6 && currentNoteIdx == 6)) {
                                    correct = true;
                                }
                            } else if (level == 6) {
                                if (idx == currentNoteIdx) {
                                    correct = true;
                                }
                            } else if (level == 7) {
                                if (idx == currentNoteIdx) {
                                    correct = true;
                                }
                            } else if (level == 8) {
                                if (idx == currentNoteIdx) {
                                    correct = true;
                                }
                            } else {
                                if (idx == currentNoteIdx) correct = true;
                            }
                            if (correct) {
                                notesGuessed++;
                                tvInfo.setText("Right!");
                            } else {
                                tvInfo.setText("Wrong!");
                            }
                            nextNote();
                            waitingForGuess = false;
                        }
                        break;
                }
                return true;
            });
        }
        // Обработка нажатий на чёрные клавиши
        for (int i = 0; i < blackKeys.length; i++) {
            boolean enabled = false;
            if (level == 3) enabled = (i == 0 || i == 1); // C# (0), D# (1)
            else if (level == 4) enabled = (i == 0 || i == 1); // Только C#, D# активны
            else if (level == 5) enabled = (i >= 2 && i <= 4); // Только F#, G#, A# активны
            else if (level == 6) enabled = false; // все чёрные неактивны
            else if (level == 7) enabled = true; // все чёрные активны
            else if (level == 8) enabled = true; // все чёрные активны
            else if (level > 5) enabled = true;
            blackEnabled[i] = enabled;
            blackKeys[i].setEnabled(enabled);
            // Всегда явно задаём SVG
            if (!enabled) {
                setSvgImage(blackKeys[i], "piano_keys/gray.svg");
                blackKeys[i].setAlpha(1f);
            } else {
                setSvgImage(blackKeys[i], "piano_keys/black.svg");
                blackKeys[i].setAlpha(1f);
            }
        }
        // Обработка нажатий на чёрные клавиши
        for (int i = 0; i < blackKeys.length; i++) {
            final int idx = i;
            final String svgName = blackSvgNames[i];
            blackKeys[i].setOnTouchListener((v, event) -> {
                if (!blackKeys[idx].isEnabled()) return false;
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    if (v instanceof com.grossbeak.mypianoapp.TopGravityImageView) {
                        com.grossbeak.mypianoapp.TopGravityImageView tgv = (com.grossbeak.mypianoapp.TopGravityImageView) v;
                        if (!tgv.isPointInsideDrawable(event.getX(), event.getY())) return false;
                    }
                    setSvgImage(blackKeys[idx], "piano_keys/" + svgName + "_pressed.svg");
                    playNoteWithFadeOverlay(getBlackMidiNumber(idx));
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                    new Handler().postDelayed(() -> setSvgImage(blackKeys[idx], blackEnabled[idx] ? "piano_keys/black.svg" : "piano_keys/gray.svg"), 300);
                    if (waitingForGuess) {
                        boolean correct = false;
                        if (level == 3) {
                            // Чёрные: idx 0 (C#), 1 (D#)
                            if ((idx == 0 && currentNoteIdx == 100) || (idx == 1 && currentNoteIdx == 101)) {
                                correct = true;
                            }
                        } else if (level == 4) {
                            if ((idx == 0 && currentNoteIdx == 100) || (idx == 1 && currentNoteIdx == 101)) {
                                correct = true;
                            }
                        } else if (level == 5) {
                            if ((idx == 2 && currentNoteIdx == 102) || (idx == 3 && currentNoteIdx == 103) || (idx == 4 && currentNoteIdx == 104)) {
                                correct = true;
                            }
                        } else if (level == 7 || level == 8) {
                            if (idx == currentNoteIdx - 100) {
                                correct = true;
                            }
                        }
                        if (correct) {
                            notesGuessed++;
                            tvInfo.setText("Right!");
                        } else {
                            tvInfo.setText("Wrong!");
                        }
                        nextNote();
                        waitingForGuess = false;
                    }
                }
                return true;
            });
        }
        // Кнопка "Играть ноту"
        btnPlayNote = findViewById(R.id.btn_play_note);
        setSvgImage(btnPlayNote, "lvlbtn/play_note.svg");
        btnPlayNote.setOnTouchListener((v, event) -> {
            if (!btnPlayNote.isEnabled()) return false;
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    setSvgImage(btnPlayNote, "lvlbtn/play_note_pressed.svg");
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    setSvgImage(btnPlayNote, "lvlbtn/play_note.svg");
                    break;
            }
            return false;
        });
        btnPlayNote.setOnClickListener(v -> {
            if (notesToGuess > 0) {
                int noteIdx = notesSequence[notesTotal - notesToGuess];
                currentNoteIdx = noteIdx;
                int midi;
                if (level == 3) {
                    if (noteIdx == 100) midi = 61; // C#4
                    else if (noteIdx == 101) midi = 63; // D#4
                    else midi = midiNumbers[noteIdx];
                } else if (level == 4) {
                    if (noteIdx == 100) midi = 61; // C#4
                    else if (noteIdx == 101) midi = 63; // D#4
                    else midi = midiNumbers[noteIdx];
                } else if (level == 5) {
                    if (noteIdx == 102) midi = 66; // F#4
                    else if (noteIdx == 103) midi = 68; // G#4
                    else if (noteIdx == 104) midi = 70; // A#4
                    else midi = midiNumbers[noteIdx];
                } else if (level == 6) {
                    midi = midiNumbers[noteIdx];
                } else if (level == 7) {
                    int[] blackMidi = {61, 63, 66, 68, 70};
                    if (noteIdx >= 100 && noteIdx <= 104) midi = blackMidi[noteIdx - 100];
                    else midi = midiNumbers[noteIdx];
                } else if (level == 8) {
                    int[] blackMidi = {61, 63, 66, 68, 70};
                    if (noteIdx >= 100 && noteIdx <= 104) midi = blackMidi[noteIdx - 100];
                    else midi = midiNumbers[noteIdx];
                } else {
                    midi = midiNumbers[noteIdx];
                }
                playNoteWithFade(midi);
                if (debugEnabled) {
                    String debugText;
                    if (level == 3) {
                        if (noteIdx == 0) debugText = "C";
                        else if (noteIdx == 1) debugText = "D";
                        else if (noteIdx == 2) debugText = "E";
                        else if (noteIdx == 100) debugText = "C#";
                        else if (noteIdx == 101) debugText = "D#";
                        else debugText = "?";
                        tvDebugNote.setText(debugText);
                    } else if (level == 4) {
                        if (noteIdx == 0) debugText = "C";
                        else if (noteIdx == 1) debugText = "D";
                        else if (noteIdx == 2) debugText = "E";
                        else if (noteIdx == 100) debugText = "C#";
                        else if (noteIdx == 101) debugText = "D#";
                        else debugText = "?";
                        tvDebugNote.setText(debugText);
                    } else if (level == 5) {
                        if (noteIdx == 3) debugText = "F";
                        else if (noteIdx == 4) debugText = "G";
                        else if (noteIdx == 5) debugText = "A";
                        else if (noteIdx == 6) debugText = "H";
                        else if (noteIdx == 102) debugText = "F#";
                        else if (noteIdx == 103) debugText = "G#";
                        else if (noteIdx == 104) debugText = "A#";
                        else debugText = "?";
                        tvDebugNote.setText(debugText);
                    } else if (level == 6) {
                        tvDebugNote.setText(noteNames[noteIdx]);
                    } else if (level == 7 || level == 8) {
                        String[] allNames = {"C", "D", "E", "F", "G", "A", "H", "C#", "D#", "F#", "G#", "A#"};
                        int idxName = noteIdx;
                        if (noteIdx >= 100 && noteIdx <= 104) idxName = 7 + (noteIdx - 100);
                        tvDebugNote.setText(allNames[idxName]);
                    } else {
                        tvDebugNote.setText(noteNames[noteIdx]);
                    }
                } else {
                    tvDebugNote.setText("");
                }
                waitingForGuess = true;
            }
        });

        ImageView btnBack = findViewById(R.id.btn_back);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Не запускаем MusicManager.play(this) в уровнях
        if (getIntent().getBooleanExtra("show_final_effect", false)) {
            showFinalEffect();
        }
    }

    private void showFinalEffect() {
        // Затемняем экран и показываем цитату
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        View overlay = new View(this);
        overlay.setBackgroundColor(0xCC000000);
        overlay.setClickable(true);
        root.addView(overlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView quote = new TextView(this);
        quote.setText("Music can change the world.\n- Ludwig van Beethoven");
        quote.setTextColor(0xFFFFFFFF);
        quote.setTextSize(32);
        android.graphics.Typeface robotoRound = ResourcesCompat.getFont(this, R.font.roboto_round_regular);
        quote.setTypeface(robotoRound, android.graphics.Typeface.BOLD);
        quote.setShadowLayer(8, 0, 0, 0xFF000000);
        quote.setGravity(android.view.Gravity.CENTER);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(quote, lp);
        // Маленькая подпись внизу
        TextView subtitle = new TextView(this);
        subtitle.setText("Beethoven: Ode an die Freude");
        subtitle.setTextColor(0xCCFFFFFF);
        subtitle.setTextSize(16);
        subtitle.setGravity(android.view.Gravity.CENTER | android.view.Gravity.BOTTOM);
        subtitle.setPadding(0, 0, 0, 48); // отступ снизу
        subtitle.setTypeface(robotoRound);
        root.addView(subtitle, lp);
        // Анимация нажатия клавиш
        Handler handler = new Handler();
        // Мелодия: 'Ода к радости' Бетховена (адаптация)
        int[] melodyMidi = {64, 64, 65, 67, 67, 65, 64, 62, 60, 60, 62, 64, 64, 62, 62};
        int[] melodyIdx = {2, 2, 3, 4, 4, 3, 2, 1, 0, 0, 1, 2, 2, 1, 1};
        String[] whiteSvgNames = {"c4", "d4", "e4", "f4", "g4", "a4", "h4"};
        int noteDelay = 400;
        int melodyPause = 500;
        handler.postDelayed(() -> {
            // Один прогон мелодии с кастомными паузами
            int[] customDelays = new int[melodyIdx.length - 1];
            for (int i = 0; i < customDelays.length; i++) {
                if (i == 11) { // между E (11) и E (12)
                    customDelays[i] = (int)(noteDelay * 1.33);
                } else if (i == 12) { // между E (12) и D (13)
                    customDelays[i] = (int)(noteDelay * 1.33);
                } else if (i == 13) { // между D (13) и D (14)
                    customDelays[i] = (int)(noteDelay * 0.5);
                } else {
                    customDelays[i] = noteDelay;
                }
            }
            int time = 0;
            for (int i = 0; i < melodyIdx.length; i++) {
                final int idx = melodyIdx[i];
                final int midiNum = melodyMidi[i];
                handler.postDelayed(() -> {
                    setSvgImage(whiteKeys[idx], "piano_keys/" + whiteSvgNames[idx] + "_pressed.svg");
                    playNoteWithFadeOverlay(midiNum);
                    handler.postDelayed(() -> setSvgImage(whiteKeys[idx], "piano_keys/white.svg"), 150);
                }, time);
                if (i < customDelays.length) time += customDelays[i];
            }
            // После прогона — 0.5 сек паузы и fade-переход
            handler.postDelayed(() -> {
                Intent intent = new Intent(GameActivity.this, LevelSelectActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, time + 1500);
        }, melodyPause);
    }

    private void startGame() {
        sequence.clear();
        userStep = 0;
        isGameActive = false;
        tvInfo.setText("Слушайте и повторяйте!");
        for (int i = 0; i < level; i++) {
            sequence.add(random.nextInt(keyIds.length));
        }
        playSequence(0);
    }

    private void playSequence(int idx) {
        if (idx >= sequence.size()) {
            isGameActive = true;
            userStep = 0;
            return;
        }
        int soundIdx = sequence.get(idx);
        playNoteWithFade(midiNumbers[soundIdx]);
        new Handler().postDelayed(() -> playSequence(idx + 1), 800);
    }

    // Универсальный MediaPlayer для fade-out (чтобы не было наложения)
    private void playNoteWithFade(int midiNumber) {
        // Останавливаем предыдущий fadePlayer, если есть
        if (fadePlayer != null) {
            try { fadePlayer.stop(); } catch (Exception ignored) {}
            fadePlayer.release();
            fadePlayer = null;
        }
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
            fadePlayer = mp;

            // Fade out через 2 секунды
            new Handler().postDelayed(() -> {
                final int fadeDuration = 2000;
                final int fadeSteps = 20;
                final float delta = 1f / fadeSteps;
                final Handler fadeHandler = new Handler();
                for (int i = 1; i <= fadeSteps; i++) {
                    final float volume = pianoVolume * (1f - (delta * i));
                    fadeHandler.postDelayed(() -> {
                        try { mp.setVolume(volume, volume); } catch (Exception ignored) {}
                    }, i * fadeDuration / fadeSteps);
                }
                // Через 2 сек после fade out — стоп и release
                fadeHandler.postDelayed(() -> {
                    try {
                        if (mp.isPlaying()) mp.stop();
                    } catch (Exception ignored) {}
                    mp.release();
                    if (fadePlayer == mp) fadePlayer = null;
                }, fadeDuration + 100);
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            // Fade out через 2 секунды
            new Handler().postDelayed(() -> {
                final int fadeDuration = 2000;
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
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextNote() {
        notesToGuess--;
        tvNotesLeft.setText("Notes left: " + notesToGuess);
        // waitingForGuess = false; // теперь сбрасывается в обработчике клавиш
        if (notesToGuess == 0) {
            btnPlayNote.setEnabled(false);
            finishLevel();
        }
    }

    private void finishLevel() {
        int stars;
        if (level == 3) {
            if (notesGuessed == 4) stars = 3;
            else if (notesGuessed == 3) stars = 2;
            else if (notesGuessed == 2) stars = 1;
            else stars = 0;
        } else if (level == 2) {
            if (notesGuessed == 4) stars = 3;
            else if (notesGuessed == 3) stars = 2;
            else if (notesGuessed == 2) stars = 1;
            else stars = 0;
        } else if (level == 4) {
            if (notesGuessed == 7) stars = 3;
            else if (notesGuessed >= 6) stars = 2;
            else if (notesGuessed >= 3) stars = 1;
            else stars = 0;
        } else if (level == 5) {
            if (notesGuessed == 4) stars = 3;
            else if (notesGuessed == 3) stars = 2;
            else if (notesGuessed == 2) stars = 1;
            else stars = 0;
        } else if (level == 6) {
            if (notesGuessed == 4) stars = 3;
            else if (notesGuessed == 3) stars = 2;
            else if (notesGuessed == 2) stars = 1;
            else stars = 0;
        } else if (level == 7) {
            if (notesGuessed == 4) stars = 3;
            else if (notesGuessed == 3) stars = 2;
            else if (notesGuessed == 2) stars = 1;
            else stars = 0;
        } else if (level == 8) {
            if (notesGuessed == 7) stars = 3;
            else if (notesGuessed >= 6) stars = 2;
            else if (notesGuessed >= 3) stars = 1;
            else stars = 0;
        } else {
            stars = notesGuessed;
            if (stars < 0) stars = 0;
            if (stars > 3) stars = 3;
        }
        saveStars(level, stars);
        if (level == 8 && notesGuessed == notesTotal) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("show_final_effect", true);
            intent.putExtra("level", 8);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent intent = new Intent(this, LevelSelectActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    private void saveStars(int level, int stars) {
        SharedPreferences prefs = getSharedPreferences("piano_game", MODE_PRIVATE);
        int prev = prefs.getInt("level_"+level+"_stars", 0);
        if (stars > prev) {
            prefs.edit().putInt("level_"+level+"_stars", stars).apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentPlayer != null) {
            currentPlayer.release();
            currentPlayer = null;
        }
        if (fadePlayer != null) {
            try { fadePlayer.stop(); } catch (Exception ignored) {}
            fadePlayer.release();
            fadePlayer = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private int getBlackMidiNumber(int idx) {
        // C#4=61, D#4=63, F#4=66, G#4=68, A#4=70
        int[] midi = {61, 63, 66, 68, 70};
        return midi[idx];
    }
} 