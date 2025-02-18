package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;

public class GdxInputHandler implements InputHandler {

    @Override
    public boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    @Override
    public boolean isKeyJustPressed(int keycode) {
        return Gdx.input.isKeyJustPressed(keycode);
    }
}
