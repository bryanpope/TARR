package com.totalannihilationroadrage;

import android.graphics.Color;
import android.graphics.Paint;

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
                if(inBounds(event, 77, 950, 200, 200) ) {
                    game.setScreen(new HowToScreen(game));
                    return;
                }

                if(inBounds(event, 1730, 950, 200, 200) ) {
                    game.setScreen(new HowToScreen3(game));
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

        index = 22;
        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, 1730, 950, srcX, srcY, tileWidth, tileHeight);

        text =  "     You can move forward, turn left or right based on \n" +
                "     the amount of maneuverability available for that \n" +
                "     vehicle, break and acceleration which will adjust \n" +
                "     your speed which will affect your maneuverability. \n" +
                "     When a friendly vehicle is selected a gang inventory \n" +
                "     bar will appear at the bottom of the screen showing \n" +
                "     you what your crew for that vehicle looks like. ";

        g.drawText(text, 0, 315, Color.WHITE, fontSize, Paint.Align.LEFT);

        title = "HOW TO PLAY";
        g.drawText(title, 970, 190, Color.rgb(252, 165, 15), titleSize, Paint.Align.CENTER);
    }

    public void pause() {
        Settings.save(game.getFileIO());
    }

    public void resume() {

    }

    public void dispose() {

    }
}