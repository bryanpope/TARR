package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.EnumMap;

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

    enum Inventory
    {
        Fuel,
        Food,
        Ammo,
        Guns,
        Tires,
        Medical_Supplies,
        Antitoxins,
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

    int [] Have_Inventory = new int[Inventory.values().length];
    List <int[]> CityArray = new ArrayList<int[]>();
    //int CityArray_row = 0;
    //int CityArray_col = 0;

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
    public int srcX = 0;
    public int srcY = 0;
    public int destX = 0;
    public int destY = 0;

    public int AvatarX = 0;
    public int AvatarY = 0;
    public int Rand_pos = 0;
    private WorldMapVehicle selectedVehicle = null;

    private void AutoLoot()
    {
        Random rand = new Random();

        int AL = rand.nextInt(2) + 1;

        switch (AL)
        {
            case '1': randomLoot();
                break;

            case '2': //randomPeople();
                break;
        }
    }

    private void randomLoot()
    {
        Random rand = new Random();

        int RL = rand.nextInt(26) + 1;
        int quantity = 0;
        switch(RL)
        {
            case 0:
            case 2:
            {
                quantity = rand.nextInt(200) + 1;
                break;
            }

            case 1:
            {
                quantity = rand.nextInt(900) + 1;
                break;
            }

            case 3:
            case 4:
            case 6:
            {
                quantity = rand.nextInt(30) + 1;
                break;
            }

            case 5:
            {
                quantity = rand.nextInt(75) + 1;
                break;
            }

            case 7:
            case 8:
            {
                quantity = rand.nextInt(4) + 1;
                break;
            }

            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            {
                quantity = rand.nextInt(2);
                break;
            }
        }

        Have_Inventory[RL] += quantity;
    }

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
        avatar_placement();
    }

    public void avatar_placement()
    {
        Random rand = new Random();

        int tilesheetcol = (world.image.width/world.tileset.tileWidth);

        for (int i = 0; i < world.layers.size(); i++)  //picks the layer
        {
            for (int index = 0; index < world.layers.get(i).data.size(); index++) //indexes through the tiledmap
            {
                int t_element = world.layers.get(i).data.get(index) - 1;
                //srcX = (t_element % tilesheetcol * world.tileWidth);
                //srcY = (t_element / tilesheetcol * world.tileWidth);
                destX = world.tileset.tileWidth;
                destY = world.tileset.tileHeight;
                if (t_element == 21)
                {
                    CityArray.add(new int[] {destX, destY});
                }
                destX++;

                if (destX >= world.width)
                {
                    destX = 0;
                    destY++;
                }

            }
        }

        Rand_pos = rand.nextInt(CityArray.size());
        AvatarX = CityArray.get(Rand_pos)[0];
        AvatarY = CityArray.get(Rand_pos)[1];
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
                if (!event.wasDragged)
                {
                    //selectedVehicle = isVehicleTouched(event);
                }
            }

            if(event.type == TouchEvent.TOUCH_DRAGGED)
            {

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

    /*
    public WorldMapVehicle isVehicleTouched(Input.TouchEvent event)
    {
        //int tileWidth = 128;
        //int tileHeight = 128;
        int x, y;

        for(int j = 0; j < world.layers.size(); ++j)
        {
            x = (world.data.get(j).xPos * tcWorld.tmBattleGround.tileWidth) - cameraX;
            y = (tcWorld.tcvsPlayer.get(j).yPos * tcWorld.tmBattleGround.tileHeight) - cameraY;
            if(inBoundaryCheck(event.x, event.y, x, y, tcWorld.tmBattleGround.tileWidth, tcWorld.tmBattleGround.tileHeight))
            {
                return tcWorld.tcvsPlayer.get(j);
            }
        }
        return null;
    }
*/
    @Override
    public void pause()
    {
        if(state == GameState.Running)
            state = GameState.Paused;
    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }
}