package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.utils.ScreenUtils;

public class GameMaster extends ApplicationAdapter {


    @Override
    public void create() {
        IOManager.getInstance();  // initializes the IOManager and sets up input

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0, 0.2f, 1);
        // to display inputs at top right corner
        IOManager.getInstance().getDynamicInput().drawInputText();
        //play music when opened
        IOManager.getInstance().getAudio().playMusic("BgMusic.mp3");
        super.render();
    }

    @Override
    public void dispose() {
        IOManager.getInstance().dispose();
    }
}
