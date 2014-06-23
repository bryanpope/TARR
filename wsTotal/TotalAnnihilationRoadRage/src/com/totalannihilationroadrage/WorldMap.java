package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.text.method.Touch;
import android.view.MotionEvent;

import com.framework.Game;
import com.framework.Graphics;

import com.framework.Input;
import com.framework.impl.MultiTouchHandler;
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
    TiledMap world;
    Pathfinding pathfinding;

    public int camera_toprow = 0;
    public int camera_leftcol = 0;
    public int cameraX;
    public int cameraY;
    public int camera_offsetx = 0;
    public int camera_offsety = 0;

    public float mLastTouchx = 0;
    public float mLastTouchy = 0;

    public int pointerId;
    public int numRows = 0;
    public int numCols = 0;



    public WorldMap(Game game, TiledMap TiledMap)
    {
        super(game);
        world = TiledMap;
        //world = new World();
        numRows = (game.getGraphics().getHeight()/ world.tileHeight) + 1;
        numCols = (game.getGraphics().getWidth() / world.tileWidth) + 1;
    }

    public void update(float deltaTime)
    {
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int maxCameraX = (world.width * world.tileWidth) - g.getWidth();
        int maxCameraY = (world.height * world.tileHeight) - g.getHeight();
        float x, y;

        int len = touchEvents.size();
        for(int i = 0; i < len; i++)
        {
            TouchEvent event = touchEvents.get(i);
            x = game.getInput().getTouchX(i);
            y = game.getInput().getTouchY(i);

            if (event.type == TouchEvent.TOUCH_DOWN)
            {


                // Remember where we started
                mLastTouchx = x;
                mLastTouchy = y;
            }

            if (event.type == TouchEvent.TOUCH_UP)
            {

            }

            if(event.type == TouchEvent.TOUCH_DRAGGED)
            {
                x = game.getInput().getTouchX(i);
                y = game.getInput().getTouchY(i);

                // Calculate the distance moved
                final float dx = x - mLastTouchx;
                final float dy = y - mLastTouchy;

                // Move the object
                cameraX -= Math.round(dx);
                if (cameraX < 0)
                {
                    cameraX = 0;
                }
                if (cameraX > maxCameraX)
                {
                    cameraX = maxCameraX;
                }

                cameraY -= Math.round(dy);
                if (cameraY < 0)
                {
                    cameraY = 0;
                }
                if (cameraY > maxCameraY)
                {
                    cameraY = maxCameraY;
                }

                camera_leftcol = cameraX / world.tileWidth;
                camera_offsetx = -(cameraX % world.tileWidth);
                camera_toprow = cameraY /world.tileHeight;
                camera_offsety = -(cameraY % world.tileWidth);

                numRows = g.getHeight() / world.tileHeight;
                numRows += (camera_offsety < 0) ? 1 : 0;

                numCols = g.getWidth() / world.tileWidth;
                numCols += (camera_offsetx < 0) ? 1 : 0;
            }

            mLastTouchx = x;
            mLastTouchy = y;
        }
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


        //int tilesheetsize = ((world.image.width/world.tileset.tileWidth) * (world.image.height/world.tileset.tileHeight));
        int tilesheetcol = (world.image.width/world.tileset.tileWidth);
        //int mapsize = (world.width * world.height);
        int destX, destY;
        int srcX, srcY;

        Node node;
        Node start = new Node(9, 22, 0, 0, null);
        Node end = new Node(16, 29, 0, 0, null);
        pathfinding = new Pathfinding();
        node = pathfinding.IAmAPathAndILikeCheese(world, start, end);

        //int numRows = g.getHeight() / world.tileHeight;
        //int numCols = g.getWidth() / world.tileWidth;

        for (int i = 0; i < world.layers.size(); i++)  //picks the layer
        {
            for (int row = camera_toprow; (row - camera_toprow) < numRows; ++row)
            {
                for (int col = camera_leftcol; (col - camera_leftcol) < numCols; ++col)
                {
                    destX = ((col - camera_leftcol) * world.tileWidth) + camera_offsetx;
                    destY = ((row - camera_toprow) * world.tileHeight) + camera_offsety;
                    int t_element = world.layers.get(i).getTile(row,col);
                    srcX = (t_element % tilesheetcol * world.tileWidth);
                    srcY = (t_element / tilesheetcol * world.tileWidth);
                    g.drawPixmap(world.image.pmImage, destX, destY, srcX, srcY, world.tileWidth, world.tileHeight);
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
    public void pause()
    {
        if(state == GameState.Running)
            state = GameState.Paused;
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}