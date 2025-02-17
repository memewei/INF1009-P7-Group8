package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

public class AudioOutput {
    private final Map<String, Sound> soundEffects = new HashMap<>();
    private Music backgroundMusic;
    private float volume = 1.0f;
    private boolean isMusicPlaying = false; // track music state

    public void playSound(String file) {
        Sound sound = soundEffects.computeIfAbsent(file, f -> Gdx.audio.newSound(Gdx.files.internal(f)));
        sound.stop(); //stop sounds that are playing already so that sounds do not overlap
        sound.play(volume);
    }

    public void playMusic(String file) {
        // check if the music is already playing
        if (isMusicPlaying) {
            return;
        }

        // stop any music before starting a new one
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }

        // load and start music file
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(file));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(volume);
        backgroundMusic.play();

        // mark that the music is playing
        isMusicPlaying = true;
    }

    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            isMusicPlaying = false;  // reset flag when music stops
        }
    }

    public void setVolume(float v) {
        volume = v;
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(volume);
        }
    }

    public void dispose() {
        for (Sound sound : soundEffects.values()) {
            sound.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}
