package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Screen;
import com.totalannihilationroadrage.Pathfinding;

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
    //Pathfinding pathfinding;

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
        /*Node start = new Node(3, 3, 0, 0, null);
        Node end = new Node(25, 25, 0, 0, null);
        pathfinding.IAmAPathAndILikeCheese(tMap.layers, start, end, tMap.layers);*/

		for (int i = 0; i < tMap.layers.size(); i++)  //picks the layer
		{
			destX = destY = 0;
			for (int index = 0; index < tMap.layers.get(i).data.size(); index++) //indexes through the tiledmap
			{
				int t_element = tMap.layers.get(i).data.get(index) - 1;
				srcY = (t_element / tileSheetCol) * tMap.tileset.tileWidth;
				srcX = (t_element % tileSheetCol) * tMap.tileset.tileHeight;
				g.drawPixmap(tMap.image.pmImage, destX * tMap.tileset.tileWidth, destY * tMap.tileset.tileHeight, srcX, srcY, tMap.tileset.tileWidth + 1, tMap.tileset.tileHeight);
				destX++;
				if (destX >= tMap.width)
				{
					destX = 0;
					destY++;
				}
			}
		}
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
        int tileWidth = 32;
        int tileHeight = 32;

        g.drawPixmap(Assets.roadTileSheet,posX - 32, posY, 32, 224, tileWidth, tileHeight);            //break
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY, 96, 192, tileWidth, tileHeight);            //accelerate
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY - 32, 64, 192, tileWidth, tileHeight);       //left turn
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY + 32, 32, 192, tileWidth, tileHeight);       //right turn
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY, 0, 256, tileWidth, tileHeight);            //move straight
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY + 32, 0, 192, tileWidth, tileHeight);       // move right
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY - 32, 96, 160, tileWidth, tileHeight);      //move left
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
