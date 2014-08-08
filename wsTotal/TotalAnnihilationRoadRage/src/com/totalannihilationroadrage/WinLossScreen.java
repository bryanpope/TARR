package com.totalannihilationroadrage;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input.TouchEvent;
import com.framework.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WinLossScreen extends Screen
{
    private boolean mPlayerWon;

    public WinLossScreen(Game game, boolean playerWon)
    {
        super(game);
        mPlayerWon = playerWon;
    }

    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();       
        
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP)
            {
                if(inBounds(event, 871, 985, 393, 112) ) {
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        if(event.x > x && event.x < x + width - 1 && 
           event.y > y && event.y < y + height - 1) 
            return true;
        else
            return false;
    }

    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        
        if (mPlayerWon)
        {
            g.drawPixmap(Assets.background, 0, 0);
            g.drawPixmap(Assets.mainMenu, 871, 985);
        }
        else
        {
            g.drawPixmap(Assets.gameOver, 0, 0);
            g.drawPixmap(Assets.menu, 871, 985);
        }
    }

    public void pause()
    {
        Settings.save(game.getFileIO());
    }

    public void resume()
    {
        Assets.music.play();
    }

    public void dispose()
    {
        Assets.music.stop();
    }
}

