package com.totalannihilationroadrage;

import android.graphics.Color;
import android.graphics.Paint;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;

import java.util.List;

/**
 * Created by Brandon on 2014-07-13.
 */
public class HowToScreen4 extends Screen {
    public HowToScreen4(Game game) {
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
                    game.setScreen(new HowToScreen3(game));
                    return;
                }

                if(inBounds(event, 1730, 950, 200, 200) ) {
                    game.setScreen(new HowToScreen5(game));
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
//              "     All vehicles will have a yellow box around them\n" +
        text =  "     When in the attack phase select a vehicle that\n" +
                "     has a yellow box around it, any friendly vehicle\n" +
                "     that is not within range will have a cream colour\n" +
                "     box around them, while the in range vehicles will\n" +
                "     have a yellow box around them. When you select one\n" +
                "     of your vehicles that are within range, any enemy\n" +
                "     within your range will change from pink to bright\n" +
                "     red showing you who you can attack. Press down on\n" +
                "     a vehicle you wish to attack, then slide your finger\n" +
                "     to one of the three buttons drawn.";

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