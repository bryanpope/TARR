package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import java.util.List;

import android.graphics.Color;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input.TouchEvent;
import com.framework.Pixmap;
import com.framework.Screen;
import com.totalannihilationroadrage.Settings;
import com.totalannihilationroadrage.World;

public class WorldMap extends Screen
{
    enum GameState
    {
        Ready,
        Running,
        Paused
    }

    GameState state = GameState.Ready;
    World world;

    public WorldMap(Game game)
    {
        super(game);
        world = new World();
    }

    public void update(float deltaTime)
    {
    }

    public void present(float deltaTime)
    {
    }

    @Override
    public void pause() {
        if(state == GameState.Running)
            state = GameState.Paused;

        if(world.gameOver) {
            Settings.addScore(world.score);
            Settings.save(game.getFileIO());
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
