package de.host.mobsys.starrun.control;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import androidx.annotation.RawRes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.host.mobsys.starrun.R;

/**
 * Class to load, play and stop sound effects and music.
 */
public class Sounds {
    private final Context context;
    private final MediaPlayer player;
    private final SoundPool soundPool;
    private final Map<Integer, Integer> resToSoundIds = new HashMap<>();

    private boolean isMusicPaused = false;

    public Sounds(Context context) {
        this.context = context;

        AudioAttributes attributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();

        player = new MediaPlayer();
        player.setAudioAttributes(attributes);
        player.setLooping(true);
        player.setVolume(0.5f, 0.5f);

        soundPool = new SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(attributes)
            .build();

        prepareSounds();
    }

    private void prepareSounds() {
        loadSound(R.raw.countdown);
        loadSound(R.raw.countdown_end);
        loadSound(R.raw.death);
        loadSound(R.raw.explosion);
        loadSound(R.raw.no_power_up);
        loadSound(R.raw.shield);
        loadSound(R.raw.shrink);
    }

    private void loadSound(@RawRes int audioId) {
        resToSoundIds.put(audioId, soundPool.load(context, audioId, 1));
    }

    @SuppressWarnings("ConstantConditions")
    public void playSound(@RawRes int audioId) {
        soundPool.play(resToSoundIds.get(audioId), 1, 1, 1, 0, 1.0f);
    }

    public void playMusic(@RawRes int audioId) {
        try {
            player.setDataSource(context.getResources().openRawResourceFd(audioId));
            player.prepareAsync();
            player.setOnPreparedListener(MediaPlayer::start);
            isMusicPaused = false;
        } catch (IOException e) {
            throw new RuntimeException("Could not load audio resource", e);
        }
    }

    public void resumeMusic() {
        if (isMusicPaused) {
            player.start();
            isMusicPaused = false;
        }
    }

    public void pauseMusic() {
        player.pause();
        isMusicPaused = true;
    }

    public boolean isMusicPlaying() {
        return player.isPlaying();
    }

    public void release() {
        isMusicPaused = false;
        player.release();
        soundPool.release();
    }
}
