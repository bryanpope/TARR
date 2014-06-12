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
        int tilesheetcol = (world.image.width/world.tileset.tileWidth);
        int mapsize = (world.width * world.height);

        for (int i = 0; i < world.layers.size(); i++)  //picks the layer
        {
            int dest_x = 0;
            int dest_y = 0;
            for (int index = 0; index < world.layers.get(i).data.size(); index++) //indexes through the tiledmap
            {
                int t_element = world.layers.get(i).data.get(index);
                g.drawPixmap(world.image.pmImage, dest_x * 64, dest_y * 64, (t_element/tilesheetcol*world.tileset.tileWidth),(t_element%tilesheetcol*world.tileset.tileHeight),world.tileset.tileWidth,world.tileset.tileHeight);
                dest_x++;
                if (dest_x > world.width)
                {
                    dest_x = 0;
                    dest_y++;
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
