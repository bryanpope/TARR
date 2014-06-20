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

    public static int camera_toprow = 5;
    public static int camera_leftcol = 3;
    public static int camera_offsetx = 0;
    public static int camera_offsety = 0;


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

        Node node;
        Node start = new Node(9, 22, 0, 0, null);
        Node end = new Node(16, 29, 0, 0, null);
        pathfinding = new Pathfinding();
        node = pathfinding.IAmAPathAndILikeCheese(world, start, end);

        int numRows = g.getHeight() / world.tileHeight;
        int numCols = g.getWidth() / world.tileWidth;

        for (int i = 0; i < world.layers.size(); i++)  //picks the layer
        {

            destX = destY = 0;
            for (int index = 0; index < world.layers.get(i).data.size(); index++) //indexes through the tiledmap
            {
                for (int row = camera_toprow; (row - camera_toprow) < numRows; ++row)
                {
                    for (int col = camera_leftcol; (col - camera_leftcol) < numCols; ++col)
                    {
                        destX = ((col - camera_leftcol) * world.tileWidth) + camera_offsetx;
                        destY = ((row - camera_toprow) * world.tileHeight) + camera_offsety;
                        int t_element = world.layers.get(i).getTile(row,col);
                        srcX = (t_element % tilesheetcol * world.tileset.tileHeight);
                        srcY = (t_element / tilesheetcol * world.tileset.tileWidth);
                        g.drawPixmap(world.image.pmImage, destX, destY, srcX, srcY, world.tileset.tileWidth, world.tileset.tileHeight);
                    }
                }
            }
        }

        /*for (int i = 0; i < tMap.layers.size(); i++)  //picks the layer
		{
			destX = destY = 0;
			for (int index = 0; index < tMap.layers.get(i).data.size(); index++) //indexes through the tiledmap
			{
				int t_element = tMap.layers.get(i).data.get(index) - 1;
				srcY = (t_element / tileSheetCol) * tMap.tileset.tileWidth;
				srcX = (t_element % tileSheetCol) * tMap.tileset.tileHeight;
				g.drawPixmap(tMap.image.pmImage, destX * tMap.tileset.tileWidth, destY * tMap.tileset.tileHeight, srcX, srcY, tMap.tileset.tileWidth, tMap.tileset.tileHeight);
				destX++;
				if (destX >= tMap.width)
				{
					destX = 0;
					destY++;
				}
			}
		}*/
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