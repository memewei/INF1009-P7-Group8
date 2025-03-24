package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import io.github.some_example_name.lwjgl3.abstract_engine.control.ControlMode;

public class IOManager {
    private static IOManager instance;
    private AudioOutput audio;
    private DynamicInput dynamicInput;
    private ControlMode controlMode = ControlMode.KEYBOARD;

    private IOManager() {
        //initialize non-Gdx components
        audio = new AudioOutput();
    }

    public static IOManager getInstance() {
        if (instance == null) {
            instance = new IOManager();
        }
        return instance;
    }


    // Separate method for Gdx-dependent initialization.
    //Must be called after LibGDX is initialized (in GameMaster.create()).
    public void init() {
        dynamicInput = new DynamicInput();
        Gdx.input.setInputProcessor(dynamicInput);
    }

    public AudioOutput getAudio() {
        return audio;
    }

    public DynamicInput getDynamicInput() {
        return dynamicInput;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlMode mode) {
        this.controlMode = mode;
    }

    public void dispose() {
        if (audio != null) {
            audio.dispose();
        }
        dynamicInput = null;
    }
}
