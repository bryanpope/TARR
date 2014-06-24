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

    enum Loot
    {
        Fuel,
        Food,
        Ammo,
        Guns,
        Tires,
        Medical_Supplies,
        Antitoxins
    }

    enum Vehicles
    {
        Motorcycle,
        Sidecar,
        Compact_Convertible,
        Compact_HardTop,
        Midsize_Convertible,
        Midsize_HardTop,
        Sports_Car_Convertible,
        Sports_Car_HardTop,
        StationWagon,
        Limousine,
        Van,
        Pickup_truck,
        Offroad_Convertible,
        Offroad_HardTop,
        Bus,
        Tractor,
        Construction_Vehicle,
        Flatbed_Truck,
        Trailer_Truck
    }

    enum People
    {
        Crony_Doctor,
        Crony_DrillSergeant,
        Crony_Politician,
        Agents,
        Healers,
        FG_Soldiers,
        FG_Hoodlums,
        FG_HomeGuards,
        FG_Civilians,
        FG_Cannibals,
        Resident_Police,
        Resident_Bureaucrats,
        Resident_Terrorists,
        Resident_Neutrals,
        Resident_Mutants,
        RG_Terrorists,
        RG_Cannibals
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

    public int numRows = 0;
    public int numCols = 0;

    public int AvatarX = 3;
    public int AvatarY = 8;



    private void drawOverWorldUI(int posX, int posY)
    {
        Graphics g = game.getGraphics();

        int tileWidth = 128;
        int tileHeight = 128;
        int index = 0;
        int numColumns = 3;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.overWorldUI, posX, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);            //Loot

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.overWorldUI, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);           //People

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.overWorldUI, posX, posY + tileHeight, srcX, srcY, tileWidth, tileHeight);           //Vehicles
    }

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
            //x = game.getInput().getTouchX(i);
            //y = game.getInput().getTouchY(i);
            x = event.x;
            y = event.y;

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
                //x = game.getInput().getTouchX(i);
                //y = game.getInput().getTouchY(i);

                // Calculate the distance moved
                final float dx = x - mLastTouchx;
                final float dy = y - mLastTouchy;

                // Move the object
                // check boundaries on X and Y
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
        g.clear(0);
        drawWorld(Assets.tmOverWorld);
        drawAvatar();
    }

    private void drawWorld(TiledMap world)
    {
        Graphics g = game.getGraphics();

        //int tilesheetsize = ((world.image.width/world.tileset.tileWidth) * (world.image.height/world.tileset.tileHeight));
        int tilesheetcol = (world.image.width/world.tileset.tileWidth);
        //int mapsize = (world.width * world.height);
        int destX, destY;
        int srcX, srcY;

        //Node node;
        //Node start = new Node(9, 22, 0, 0, null);
        //Node end = new Node(16, 29, 0, 0, null);
        //pathfinding = new Pathfinding();
        //node = pathfinding.IAmAPathAndILikeCheese(world, start, end);

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
        /*
            The following is debug code to test Pathfinding.
            It will draw red squares from the start node to the end node.
        */
        /*while(node != null)
        {
            g.drawRect(node.col * world.tileset.tileWidth, node.row * world.tileset.tileHeight, world.tileset.tileWidth, world.tileset.tileHeight, Color.RED);
            //System.out.println("Node Row " + node.col + ", Node Col " + node.row);
            node = node.parentNode;
        }*/
    }

    private void drawAvatar()
    {
        Graphics g = game.getGraphics();

        g.drawPixmap(world.image.pmImage, AvatarX, AvatarY, 1, 1, world.tileWidth, world.tileHeight);
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