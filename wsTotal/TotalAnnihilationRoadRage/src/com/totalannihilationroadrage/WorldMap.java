package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import java.util.List;

import android.graphics.Color;

import com.framework.Game;
import com.framework.Graphics;
import com.totalannihilationroadrage.Pathfinding;
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

    GameState state = GameState.Running;
    World world;
    Pathfinding pathfinding;

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
        int destX, destY;
        int srcX, srcY;
        Node start = new Node(3, 3, 0, 0, null);
        Node end = new Node(25, 25, 0, 0, null);
        //pathfinding.IAmAPathAndILikeCheese(world, start, end, world);

        for (int i = 0; i < world.layers.size(); i++)  //picks the layer
        {
        //int i = 0;
            destX = destY = 0;
            for (int index = 0; index < world.layers.get(i).data.size(); index++) //indexes through the tiledmap
            {
                int t_element = world.layers.get(i).data.get(index) - 1;
                srcY = (t_element/tilesheetcol*world.tileset.tileWidth);
                srcX = (t_element%tilesheetcol*world.tileset.tileHeight);
                g.drawPixmap(world.image.pmImage, destX * world.tileset.tileWidth, destY * world.tileset.tileHeight, srcX, srcY, world.tileset.tileWidth, world.tileset.tileHeight);
                destX++;
                if (destX >= world.width)
                {
                    destX = 0;
                    destY++;
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