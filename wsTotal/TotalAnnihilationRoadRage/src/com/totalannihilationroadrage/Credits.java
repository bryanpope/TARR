package com.totalannihilationroadrage;

import android.graphics.Color;
import android.graphics.Paint;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;

import java.util.List;

/**
 * Created by Brandon on 2014-06-29.
 */
public class Credits extends Screen {
    public Credits(Game game) {
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
                if(inBounds(event, 77, 950, 200, 200) ) {
                    game.setScreen(new MainMenuScreen(game));
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

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        String text;
        String title;
        int fontSize = 72;
        int titleSize = 200;
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 20;
        int numColumns = 4;
        int srcX, srcY;

        g.drawPixmap(Assets.howToScreen, 0, 0);

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, 77, 950, srcX, srcY, tileWidth, tileHeight);

        text = "Bryan Pope - Lead Programmer\n\n" +
               "Andrew Best - Pathfinding and AI\n\n" +
               "Brandon Coulthard - UI/Graphics and Sound\n\n" +
               "Carson Braun - Lead Artist";

        g.drawText(text, 970, 400, Color.WHITE, fontSize, Paint.Align.CENTER);

        title = "Credits";
        g.drawText(title, 970, 200, Color.WHITE, titleSize, Paint.Align.CENTER);
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
