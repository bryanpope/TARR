package com.totalannihilationroadrage;

import android.graphics.Color;
import android.graphics.Paint;

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

                if(inBounds(event, 1730, 982, 200, 200) ) {
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
        int fontSize = 72;
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 20;
        int numColumns = 4;
        int srcX, srcY;

        g.drawPixmap(Assets.howToScreen, 0, 0);

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, 77, 982, srcX, srcY, tileWidth, tileHeight);

        index = 22;
        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, 1730, 982, srcX, srcY, tileWidth, tileHeight);

        text = " When in tactical combat your goal is to destroy the enemy\n"
                + " forces. Select one of your vehicles to bring up your HUD\n" +
                " and select which direction you want to move. Acceleration \n" +
                " and breaking will affect your maneuverability meaning\n" +
                " how many times your vehicle can turn. For quick\n" +
                " movement use move all.";

        g.drawText(text, 0, 350, Color.WHITE, fontSize, Paint.Align.LEFT);
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
/*

When in tactical combat your goal is to destroy the enemy forces.
Select one of your vehicles to bring up your HUD and select which
direction you want to move. Acceleration and breaking will affect your
maneuverability meaning how many times your vehicle can turn.
For quick movement use move all.

After your movement phase the attack phase will begin.
If you’re within range the color around your vehicle will change
indicating that you can attack a vehicle in your line of sight.
Select one of the arrows to attack, or select skip in order
to move past the attack phase.

Continue until all forces are destroyed or you die a painful death.


 */