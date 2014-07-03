package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.EnumMap;

import android.graphics.Color;
import android.graphics.Paint;
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
    List<Node> Path = new ArrayList<Node>();
    int PathCounter = 0;

    //int CityArray_row = 0;
    //int CityArray_col = 0;

    GameState state = GameState.Running;
    TiledMap world;
    Pathfinding pathfinding = new Pathfinding();

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
    private boolean selectedVehicle = false;

    int fuel = 0;
    int food = 0;
    int ammo = 0;
    int guns = 0;
    int tires = 0;
    int medicalSupplies = 0;
    int antitoxins = 0;
    int vehicles = 0;
    int people = 0;

    boolean drawScreen = false;

    TacticalCombatWorld tcWorld;
    List< TacticalCombatVehicle > tcvPlayer = new ArrayList< TacticalCombatVehicle >();
    List< TacticalCombatVehicle > tcvEnemy = new ArrayList< TacticalCombatVehicle >();


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

        int relativeX = posX - cameraX;
        int relativeY = posY - cameraY;
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 0;
        int numColumns = 3;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.overWorldUI, relativeX, relativeY - tileHeight, srcX, srcY, tileWidth, tileHeight);            //Loot

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.overWorldUI, relativeX + tileWidth, relativeY, srcX, srcY, tileWidth, tileHeight);           //People

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.overWorldUI, relativeX, relativeY + tileHeight, srcX, srcY, tileWidth, tileHeight);           //Vehicles
    }

    public WorldMap(Game game, TiledMap TiledMap)
    {
        super(game);
        world = TiledMap;

        numRows = (game.getGraphics().getHeight()/ world.tileHeight) + 1;
        numCols = (game.getGraphics().getWidth() / world.tileWidth) + 1;

        for (int i = 0; i < 10; ++i)
        {
            tcvPlayer.add(setupVehicle(true));
            //tcvPlayer.get(i).facing = Direction.SOUTHWEST;
            tcvEnemy.add(setupVehicle(false));
            //tcvEnemy.get(i).facing = Direction.NORTHEAST;
        }

        avatar_placement();

    }

    public void avatar_placement()
    {
        Random rand = new Random();

        int tilesheetcol = (world.image.width/world.tileset.tileWidth);
        int xpos = 0;
        int ypos = 0;

        int object_layer = 1;
        for (int index = 0; index < world.layers.get(object_layer).data.size(); index++) //indexes through the tiledmap
        {
            int t_element = world.layers.get(object_layer).data.get(index);
            //srcX = (t_element % tilesheetcol * world.tileWidth);
            //srcY = (t_element / tilesheetcol * world.tileWidth);
            xpos = destX * world.tileset.tileWidth;
            ypos = destY * world.tileset.tileHeight;
            if (t_element == 22)
            {
                CityArray.add(new int[] {xpos, ypos});
            }
            destX++;

            if (destX >= world.width)
            {
                destX = 0;
                destY++;
            }

        }

        Rand_pos = rand.nextInt(CityArray.size());
        AvatarX = CityArray.get(Rand_pos)[0];
        AvatarY = CityArray.get(Rand_pos)[1];
    }

    public boolean inBoundaryCheck(int touchXPos, int touchYPos, int boxX, int boxY, int boxWidth, int boxHeight)
    {
        //logic
        if((touchXPos >= boxX) && (touchXPos <= boxX + boxWidth) &&
                (touchYPos >= boxY) && (touchYPos <= boxY + boxHeight))
        {
            return true;
        }
        else
            return false;

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

                if (selectedVehicle)
                {
                    Node Avatar_Node = new Node((AvatarY/world.tileHeight), (AvatarX/world.tileWidth), 0, 0, null, null);
                    Node Destination_Node = new Node(((int)mLastTouchy + cameraY)/world.tileHeight, ((int)mLastTouchx + cameraX)/world.tileWidth, 0, 0, null, null);
                    Path = pathfinding.IAmAPathAndILikeCheese(world, Avatar_Node, Destination_Node);
                }
            }

            if (event.type == TouchEvent.TOUCH_UP)
            {
                int tileWidth = 128;
                int tileHeight = 128;
                if (!event.wasDragged)
                {
                    selectedVehicle = isVehicleTouched(event);
                }
                if(inBoundaryCheck(event.x, event.y,  g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, tileWidth, tileHeight))
                {//checks if the inventory button has been selected
                    updateInventory(touchEvents);
                }
            }

            if(event.type == TouchEvent.TOUCH_DRAGGED)
            {
                drawScreen = false;
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


        if ((Path != null) && (Path.size()) != 0)
        {
            MovePlayer();
        }



    }

    public void MovePlayer()
    {

        if(PathCounter < Path.size())
        {
            AvatarX = Path.get(PathCounter).col;
            AvatarY = Path.get(PathCounter).row;
            PathCounter++;
        }
        else
        {
            PathCounter = 0;
            AutoLoot();

            Random rand = new Random();
            int TC = rand.nextInt(100);

            if (TC < 5)
            {
                startTacticalCombat();
            }
            return;
        }
    }

    private void startTacticalCombat ()
    {
        tcWorld = new TacticalCombatWorld(Assets.tmHighway, tcvPlayer, tcvEnemy);
        game.setScreen(new TacticalCombatScreen(game, tcWorld, tcWorld.tmBattleGround));
    }

    private TacticalCombatVehicle setupVehicle (boolean isPlayer)
    {
        Random random = new Random();
        GangMembers aGangE = new GangMembers();
        aGangE.armsmasters = random.nextInt(10) + 1;
        aGangE.bodyguards = random.nextInt(10) + 1;
        aGangE.commandos = random.nextInt(10) + 1;
        aGangE.dragoons = random.nextInt(10) + 1;
        aGangE.escorts = random.nextInt(10) + 1;
        GangMembers aGangI = new GangMembers();
        aGangI.armsmasters = random.nextInt(10) + 1;
        aGangI.bodyguards = random.nextInt(10) + 1;
        aGangI.commandos = random.nextInt(10) + 1;
        aGangI.dragoons = random.nextInt(10) + 1;
        aGangI.escorts = random.nextInt(10) + 1;
        VehicleStatsCurrent aVehicle = new VehicleStatsCurrent(Assets.vehicleStats.vehicles.get(random.nextInt(19)));
        TacticalCombatVehicle aTacticalVehicle = new TacticalCombatVehicle(aVehicle, aGangE, aGangI, isPlayer);

        return aTacticalVehicle;
    }

    @Override
    public void present(float deltaTime)
    {
        Graphics g = game.getGraphics();

        //g.drawPixmap(Assets.background, 0, 0);
        g.clear(0);
        drawWorld(Assets.tmOverWorld);
        drawAvatar();
        drawInventoryButton();

        if(selectedVehicle)
        {
            drawOverWorldUI(AvatarX,AvatarY);
        }
        if(drawScreen)
        {
            drawInventoryScreen();
        }
    }

    private void drawWorld(TiledMap world)
    {
        Graphics g = game.getGraphics();

        //int tilesheetsize = ((world.image.width/world.tileset.tileWidth) * (world.image.height/world.tileset.tileHeight));
        int tilesheetcol = (world.image.width/world.tileset.tileWidth);
        //int mapsize = (world.width * world.height);

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
    }

    private void drawAvatar()
    {
        Graphics g = game.getGraphics();

        g.drawPixmap(world.image.pmImage, AvatarX - cameraX, AvatarY - cameraY, 1, 1, world.tileWidth, world.tileHeight);
    }

    public boolean isVehicleTouched(Input.TouchEvent event)
    {
        //int tileWidth = 128;
        //int tileHeight = 128;
        int x, y;

        if(inBoundaryCheck(event.x, event.y, AvatarX - cameraX, AvatarY - cameraY, world.tileWidth, world.tileHeight))
        {
            return true;
        }

        return false;
    }

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

    private void drawInventoryButton()
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 59;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, srcX, srcY, tileWidth, tileHeight);            //skip
    }

    private void updateInventory(List<Input.TouchEvent> touchEvents)
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;

        int len = touchEvents.size();

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y,  g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, tileWidth, tileHeight))
                {
                    drawScreen = true;
                    touchEvents.remove(i);
                    break;
                }
            }
        }
    }

    private void drawInventoryScreen()
    {
        //used to tell what crew each player vehicle has
        Graphics g = game.getGraphics();
        int fontSize = 72;
        int rectWidth = 800;
        int rectHeight = fontSize * 12;
        int xPos = (int)((g.getWidth() * 0.5) - (rectWidth * 0.5));
        int yPos = (int)((g.getHeight() * 0.5) - (rectHeight * 0.5));
        int line = 0;
        String text;

        g.drawRect(xPos, yPos, rectWidth, rectHeight, Color.BLACK);

        xPos += fontSize * 1.5;


        line += 3;
        yPos = line * fontSize;

        text = "Inventory";
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Fuel: " + fuel;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Food: " + food;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Ammo: " + ammo;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Guns: " + guns;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Tires: " + tires;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Medical Supplies: " + medicalSupplies;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Antitoxins: " + antitoxins;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "Vehicles: " + vehicles;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;

        text = "People: " + people;
        yPos = line * fontSize;
        g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        ++line;
    }

}