package com.totalannihilationroadrage;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;

import java.util.List;

/**
 * Created by Brandon on 2014-06-27.
 */
public class HowToScreen2 extends Screen {
    public HowToScreen2(Game game) {
        super(game);
    }

    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();

        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_UP) {
                if(inBounds(event, 77, 982, 200, 200) ) {
                    game.setScreen(new HowToScreen(game));
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

        g.drawPixmap(Assets.howToScreen2, 0, 0);
        g.drawPixmap(Assets.previouspage, 77, 982);

    }

    public void pause() {
        Settings.save(game.getFileIO());
    }

    public void resume() {

    }

    public void dispose() {

    }
}


