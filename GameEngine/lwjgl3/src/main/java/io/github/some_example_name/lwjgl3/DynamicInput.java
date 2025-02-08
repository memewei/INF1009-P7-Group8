package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class DynamicInput extends InputAdapter {
    private String currentInput = "";  // stores the currently pressed key/mouse button
    private final Vector2 mousePosition = new Vector2();

    @Override
    public boolean keyDown(int keycode) {
        currentInput = "Key: " + Input.Keys.toString(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        currentInput = ""; // clear when key is released
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mousePosition.set(screenX, screenY);
        currentInput = "Mouse: Button " + button;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        currentInput = ""; // clear when mouse button is released
        return true;
    }

    public String getCurrentInput() {
        return currentInput;
    }

    //draw inputs for display purposes only
   public void drawInputText() {
       SpriteBatch batch;
       BitmapFont font;

       batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1f);

        String input = getCurrentInput();
        if (!input.isEmpty()) {
            float x = Gdx.graphics.getWidth() - 120; // top right
            float y = Gdx.graphics.getHeight() - 20;
            batch.begin();
            font.draw(batch, input, x, y);
            batch.end();
        }

    }
}
