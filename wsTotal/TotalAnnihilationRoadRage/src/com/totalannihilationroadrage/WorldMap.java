package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import com.framework.Game;
import com.framework.Graphics;

import com.totalannihilationroadrage.Pathfinding;
import com.totalannihilationroadrage.Node;
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
        List<Integer> impassable = new ArrayList<Integer>();
        impassable.add(22);
        Node node;
        Node start = new Node(6, 1, 0, 0, null);
        Node end = new Node(16, 15, 0, 0, null);
        pathfinding = new Pathfinding();
        node = pathfinding.IAmAPathAndILikeCheese(world, start, end, impassable);

        for (int i = 0; i < world.layers.size(); i++)  //picks the layer
        {
        //int i = 0;
            destX = destY = 0;
            for (int index = 0; index < world.layers.get(i).data.size(); index++) //indexes through the tiledmap
            {
                int t_element = world.layers.get(i).data.get(index) - 1;
                srcY = (t_element / tilesheetcol * world.tileset.tileWidth);
                srcX = (t_element % tilesheetcol * world.tileset.tileHeight);
                g.drawPixmap(world.image.pmImage, destX * world.tileset.tileWidth, destY * world.tileset.tileHeight, srcX, srcY, world.tileset.tileWidth, world.tileset.tileHeight);
                destX++;
                if (destX >= world.width) {
                    destX = 0;
                    destY++;
                }
            }
        }

        /*
            The following is debug code to test Pathfinding.
            It will draw red squares from the start node to the end node.
        */
        while(node != null)
        {
            g.drawRect(node.col * world.tileset.tileWidth, node.row * world.tileset.tileHeight, world.tileset.tileWidth, world.tileset.tileHeight, Color.RED);
            //System.out.println("Node Row " + node.col + ", Node Col " + node.row);
            node = node.parentNode;
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