package com.grossbeak.mypianoapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer player;
    private static boolean prepared = false;

    public static void play(Context context) {
        if (player == null) {
            try {
                player = new MediaPlayer();
                AssetFileDescriptor afd = context.getAssets().openFd("bg_music.mp3");
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                player.setLooping(true);
                player.setOnCompletionListener(mp -> {
                    mp.seekTo(0);
                    mp.start();
                });
                player.prepare();
                prepared = true;
            } catch (Exception e) {
                e.printStackTrace();
                prepared = false;
            }
        }
        if (player != null && prepared && !player.isPlaying()) {
            player.start();
        }
    }

    public static void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public static void stop() {
        if (player != null) {
            try { player.stop(); } catch (Exception ignored) {}
            player.release();
            player = null;
            prepared = false;
        }
    }

    public static void release() {
        if (player != null) {
            try { player.release(); } catch (Exception ignored) {}
            player = null;
            prepared = false;
        }
    }

    public static void setMusicVolume(float volume) {
        if (player != null) {
            player.setVolume(volume, volume);
        }
    }

    public static void applySavedVolume(Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("piano_game", Context.MODE_PRIVATE);
        int vol = prefs.getInt("music_volume", 100);
        setMusicVolume(vol / 100f);
    }
} 