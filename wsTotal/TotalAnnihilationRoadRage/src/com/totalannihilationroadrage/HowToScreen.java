package com.totalannihilationroadrage;
import android.graphics.Color;
import android.graphics.Paint;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;


import java.util.List;
/**
 * Created by Brandon on 2014-07-01.
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
                if(inBounds(event, 77, 950, 200, 200) ) {
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
                if(inBounds(event, 1730, 950, 200, 200) ) {
                    game.setScreen(new HowToScreen2(game));
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

        text =  "     TARR is a tactical combat simulator where you take \n" +
                "     control of a random number of ally vehicles against\n" +
                "     a random number of enemy vehicles. Your goal is to \n" +
                "     destroy the opposing forces, and bring victory to \n" +
                "     your gang. When the game starts you have the ability \n" +
                "     to select any of your vehicles, and when you select \n" +
                "     a vehicle a movement UI will appear around it. ";

        g.drawText(text, 0, 315, Color.WHITE, fontSize, Paint.Align.LEFT);

        title = "HOW TO PLAY";
        g.drawText(title, 970, 190, Color.rgb(252, 165, 15), titleSize, Paint.Align.CENTER);
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

