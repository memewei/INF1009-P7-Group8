package io.github.some_example_name.lwjgl3.abstract_engine.io;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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
        currentInput = "";
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
        currentInput = "";
        return true;
    }

    //check if a key was pressed - needed for movement, tracks every frame
    public boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    // checks if key is pressed once only when it is pressed
    public boolean isKeyJustPressed(int keycode) {
        return Gdx.input.isKeyJustPressed(keycode);
    }

    //draw inputs for display purposes only
   public void drawInputText() {
       SpriteBatch batch;
       BitmapFont font;

       batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("game_font.fnt"));
        font.getData().setScale(0.2f);

        String input = currentInput;
        if (!input.isEmpty()) {
            float x = Gdx.graphics.getWidth() - 120; // top right
            float y = Gdx.graphics.getHeight() - 20;
            batch.begin();
            font.draw(batch, input, x, y);
            batch.end();
        }

    }
}
