package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import android.graphics.Color;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;
import com.totalannihilationroadrage.Pathfinding;

import java.util.ArrayList;
import java.util.List;

public class TacticalCombatScreen extends Screen
{
	enum GameState
	{
		Ready,
		Running,
		Paused
	}

	GameState state = GameState.Ready;
	TacticalCombatWorld tcWorld;
    Pathfinding pathfinding;

    private int cameraTopRow = 0;     // For scrolling purposes, the start column in the tile map to display from
    private int cameraLeftCol = 0;
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;

	public TacticalCombatScreen(Game game, TacticalCombatWorld tacticalCombatWorld)
	{
		super(game);
		tcWorld = tacticalCombatWorld;
	}

	public void update(float deltaTime)
	{
	}

	@Override
	public void present(float deltaTime)
	{
		Graphics g = game.getGraphics();
		//g.drawPixmap(Assets.background, 0, 0);
        g.clear(0);
		drawTacticalMap(tcWorld.tmBattleGround);
        drawVehicles(tcWorld.tcvsPlayer, tcWorld.tmBattleGround);
        drawVehicles(tcWorld.tcvsEnemy, tcWorld.tmBattleGround);
	}

	private void drawTacticalMap(TiledMap tMap)
	{
		Graphics g = game.getGraphics();

	   // int tilesheetsize = (tMap.image.width / tMap.tileset.tileWidth) * (tMap.image.height / tMap.tileset.tileHeight);
		int tileSheetCol = tMap.image.width / tMap.tileset.tileWidth;
		//int mapsize = tMap.width * tMap.height;
		int destX, destY;
		int srcX, srcY;
        Node node;
        Node start = new Node(1, 1, 0, 0, null);
        Node end = new Node(6, 8, 0, 0, null);
        pathfinding = new Pathfinding();
        node = pathfinding.IAmAPathAndILikeCheese(tMap, start, end);

        int numRows = g.getHeight() / tMap.tileHeight;
        int numCols = g.getWidth() / tMap.tileWidth;
        int indexTile;

        for (int i = 0; i < tMap.layers.size(); i++)
        {
            for (int row = cameraTopRow; (row - cameraTopRow) < numRows; ++row)
            {
                for (int col = cameraLeftCol; (col - cameraLeftCol) < numCols; ++col)
                {
                    destX = ((col - cameraLeftCol) * tMap.tileWidth) + cameraOffsetX;
                    destY = ((row - cameraTopRow) * tMap.tileHeight) + cameraOffsetY;
                    indexTile = tMap.layers.get(i).getTile(row, col);
                    srcX = (indexTile % tileSheetCol) * tMap.tileWidth;
                    srcY = (indexTile / tileSheetCol) * tMap.tileWidth;
                    g.drawPixmap(tMap.image.pmImage, destX, destY, srcX, srcY, tMap.tileWidth, tMap.tileHeight);
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
            g.drawRect(node.col * tMap.tileset.tileWidth, node.row * tMap.tileset.tileHeight, tMap.tileset.tileWidth, tMap.tileset.tileHeight, Color.RED);
            //System.out.println("Node Row " + node.col + ", Node Col " + node.row);
            node = node.parentNode;
        }
	}

    public boolean inBoundaryCheck(int touchXPos, int touchYPos, int boxX, int boxY, int boxWidth, int boxHeight)
    {
        //logic
        if(touchXPos >= boxX + boxWidth && touchXPos <= boxX + boxWidth && touchYPos >= boxY + boxHeight && touchYPos <= boxY + boxHeight)
        {
            return true;
        }
        else
            return false;

    }

	private void drawVehicles(List< TacticalCombatVehicle > vehicles, TiledMap tMap)
	{
		Graphics g = game.getGraphics();

		int tileSheetCol = tMap.image.width / tMap.tileset.tileWidth;
		int destX, destY;
		int srcX, srcY;

		for (int i = 0; i < vehicles.size(); ++i)
		{
			destX = vehicles.get(i).xPos * tMap.tileset.tileWidth;
			destY = vehicles.get(i).yPos * tMap.tileset.tileHeight;
			int t_element = vehicles.get(i).vehicle.statsBase.type.ordinal() + Assets.vehicleStats.INDEX_START_CAR_TILES;
			srcY = (t_element / tileSheetCol) * tMap.tileset.tileWidth;
			srcX = (t_element % tileSheetCol) * tMap.tileset.tileHeight;
			g.drawPixmap(Assets.vehicleStats.tileSheetVehicles, destX, destY, srcX, srcY, tMap.tileset.tileWidth, tMap.tileset.tileHeight);

            drawUIPhaseMovement(destX, destY);
		}
	}

    private void drawUIPhaseMovement (int posX, int posY)
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 23;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY, srcX, srcY, tileWidth, tileHeight);            //move straight

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY - tileHeight, srcX, srcY, tileWidth, tileHeight);      //move left

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY + tileHeight, srcX, srcY, tileWidth, tileHeight);       // move right

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);       //left turn

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY + tileHeight, srcX, srcY, tileWidth, tileHeight);       //right turn

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //accelerate

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX - tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //break


        /*g.drawPixmap(Assets.roadTileSheet, posX - tileWidth, posY, 32, 224, tileWidth, tileHeight);            //break
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, 96, 192, tileWidth, tileHeight);            //accelerate
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY - 32, 64, 192, tileWidth, tileHeight);       //left turn
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY + 32, 32, 192, tileWidth, tileHeight);       //right turn
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY, 0, 256, tileWidth, tileHeight);            //move straight
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY + 32, 0, 192, tileWidth, tileHeight);       // move right
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY - 32, 96, 160, tileWidth, tileHeight);      //move left
        */
    }

    public void updateMove(List<Input.TouchEvent> touchEvents, int posX, int posY)
    {
        //update the moving
        int tileWidth = 128;
        int tileHeight = 128;
        int accelerate = 5;

        int len = touchEvents.size();

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y, posX + (tileWidth * 2), posY, tileWidth, tileHeight))
                {
                    //Move straight
                    posX += tileWidth;
                }
                if(inBoundaryCheck(event.x, event.y, posX + (tileWidth * 2), posY - tileHeight, tileWidth, tileHeight))
                {
                    //Move left
                    posX += tileWidth;
                    posY -= tileHeight;
                }
                if(inBoundaryCheck(event.x, event.y,posX + (tileWidth * 2), posY + tileHeight, tileWidth, tileHeight))
                {
                    //Move right
                    posX += tileWidth;
                    posY += tileHeight;
                }
                if(inBoundaryCheck(event.x, event.y, posX + tileWidth, posY - tileHeight, tileWidth, tileHeight))
                {
                    //Left turn
                }
                if(inBoundaryCheck(event.x, event.y, posX + tileWidth, posY + tileHeight, tileWidth, tileHeight))
                {
                    //right turn
                }
                if(inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight))
                {
                    //accelerate
                    accelerate += 5;
                }
                if(inBoundaryCheck(event.x, event.y, posX - tileWidth, posY, tileWidth, tileHeight))
                {
                    //break
                    accelerate -= 5;
                }
            }
        }
    }

    private void drawUIPhaseFire(int posX, int posY)
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 19;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY + tileHeight, srcX, srcY, tileWidth, tileHeight);            //attack down

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX - tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //attack left

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);            //attack up

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //attack right

        /*
        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 32, 160, tileWidth, tileHeight);    //up arrow
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY, 64, 160, tileWidth, tileHeight);    //right arrow
        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 96, 128, tileWidth, tileHeight);   //down arrow
        g.drawPixmap(Assets.roadTileSheet,posX - 32, posY, 0, 160, tileWidth, tileHeight);    //left arrow
        */
    }

    private void updateFire(List<Input.TouchEvent> touchEvents, int posX, int posY)
    {
        //update the fire attack
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
                if(inBoundaryCheck(event.x, event.y, posX, posY - tileHeight, tileWidth, tileHeight))
                {
                    //attack up is selected
                }
                if (inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight))
                {
                    //attack right is selected
                }
                if(inBoundaryCheck(event.x, event.y, posX,  posY + tileHeight, tileWidth, tileHeight))
                {
                    //attack down is selected
                }
                if(inBoundaryCheck(event.x, event.y, posX - tileWidth, posY, tileWidth, tileHeight))
                {
                    //attack left is selected
                }
            }
        }
    }

    private void drawUIPhaseCrewTransfer(int posX, int posY)
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 30;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY + tileHeight, srcX, srcY, tileWidth, tileHeight);            //transfer down

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);            //transfer up

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //skip
        /*
        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 0, 224, tileWidth, tileHeight);    //transfer crew down
        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 96, 224, tileWidth, tileHeight);    //transfer crew up
        */
    }

    private void updateCrewDeploymentUI(List<Input.TouchEvent> touchEvents, int posX, int posY)
    {
        //update the crew deployment
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
                if(inBoundaryCheck(event.x, event.y, posX, posY - tileHeight, tileWidth, tileHeight))
                {
                    //transfer up image is selected
                }
                if(inBoundaryCheck(event.x, event.y, posX, posY + tileHeight, tileWidth, tileHeight))
                {
                    //transfer down image is selected
                }
                if(inBoundaryCheck(event.x, event.y, posX + tileWidth, posY + tileHeight, tileWidth, tileHeight))
                {
                    //skip image is selected
                }
            }
        }
    }

    @Override
	public void pause()
	{
		if(state == GameState.Running)
		{
			state = GameState.Paused;
		}
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
