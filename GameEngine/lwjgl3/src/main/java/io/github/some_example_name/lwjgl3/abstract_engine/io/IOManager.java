package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;

import io.github.some_example_name.lwjgl3.abstract_engine.audio.AudioOutput;

public class IOManager {
    private static IOManager instance;
    private final AudioOutput audio;
    private DynamicInput dynamicInput;

    private IOManager() {
        audio = new AudioOutput(); // initialize audio
        dynamicInput = new DynamicInput(); // initialize dynamic input
        Gdx.input.setInputProcessor(dynamicInput); // set input processor
    }

    public static IOManager getInstance() {
        if (instance == null) { //ensures only 1 IOManager is created
            instance = new IOManager();
        }
        return instance;
    }

    public DynamicInput getDynamicInput() {
        return dynamicInput;
    }

    public AudioOutput getAudio() {
        return audio;
    }

    public void dispose() {
        audio.dispose(); // ensure cleanup of audio resources
        dynamicInput = null; // clean up dynamic input
    }
}
