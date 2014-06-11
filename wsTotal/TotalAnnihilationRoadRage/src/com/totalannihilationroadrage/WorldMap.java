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

    @Override
    public void present(float deltaTime)
    {
        Graphics g = game.getGraphics();

        //g.drawPixmap(Assets.background, 0, 0);
        drawWorld(Assets.tmOverWorld);
    }

    private void drawWorld(TiledMap world)
    {
        Graphics g = game.getGraphics();

        int tilesheetsize = ((world.image.width/world.tileset.tileWidth) * (world.image.height/world.tileset.tileHeight));
        int mapsize = (world.width * world.height);

        for (int i = 0; i < world.layers.size(); i++)
        {
            for (int index = 0; index < mapsize; index++)
            {
                for (int j = 0; j < tilesheetsize; j++ )
                {
                    if (world.layers.get(i).data.get(index) == ((j*world.tileset.tileWidth)/world.tileset.tileWidth))
                    {
                        g.drawPixmap(world.tileset.get(j),0,0);
                    }
                }
            }


        }

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
