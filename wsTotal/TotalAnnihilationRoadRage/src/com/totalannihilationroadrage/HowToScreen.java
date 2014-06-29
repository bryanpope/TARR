package com.totalannihilationroadrage;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Brandon on 2014-06-27.
 */

public class HowToScreen extends Screen {
    public HowToScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();

        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_UP) {
                if(inBounds(event, 77, 982, 200, 200) ) {
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
                /*
                if(inBounds(event, 1843, 982, 200, 200) ) {
                    game.setScreen(new HowToScreen2(game));
                    return;
                }
                */
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

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 20;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.howToScreen, 0, 0);
        g.drawPixmap(Assets.roadTileSheet, 77, 982, srcX, srcY, tileWidth, tileHeight);
        // g.drawPixmap(Assets.nextpage, 1843, 982);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}