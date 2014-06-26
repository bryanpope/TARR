package com.totalannihilationroadrage;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;

import java.util.List;

/**
 * Created by Brandon on 2014-06-26.
 */

public class GameOverScreen extends Screen
{
    public GameOverScreen(Game game) {
        super(game);
    }

    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                if (inBounds(event, 744, 688, 438, 57)) {
                    game.setScreen(new WorldMap(game, Assets.tmOverWorld));
                    return;
                }
            }
        }
    }
    private boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height) {
        if(event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1)
            return true;
        else
            return false;
    }

    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawPixmap(Assets.gameOver, 0, 0);
        g.drawPixmap(Assets.gameOvermenu, 744, 688);

    }

    public void pause() {
        Settings.save(game.getFileIO());
    }

    public void resume() {

    }

    public void dispose() {

    }
}
