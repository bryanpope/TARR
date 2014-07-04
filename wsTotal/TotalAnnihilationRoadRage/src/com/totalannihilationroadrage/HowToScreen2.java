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

        text =  " In tactical combat select one of your vehicles to bring up \n" +
                " your HUD and select which direction you want to move. \n" +
                " Acceleration and breaking will affect your maneuverability \n" +
                " meaning how many times your vehicle can turn. For quick \n" +
                " movement use move all. If you’re within range of an enemy \n" +
                " the colour around your vehicle will change to bright yellow \n" +
                " indicating that you can attack a vehicle in your line of sight.\n" +
                " Enemy vehicles will turn bright red if they are within line of \n"+
                " sight of one of your vehicles";

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