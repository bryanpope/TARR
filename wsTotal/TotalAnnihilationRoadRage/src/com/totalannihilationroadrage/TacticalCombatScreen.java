package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/11/2014.
 */

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Screen;

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
	}

	private void drawTacticalMap(TiledMap tMap)
	{
		Graphics g = game.getGraphics();

	   // int tilesheetsize = (tMap.image.width / tMap.tileset.tileWidth) * (tMap.image.height / tMap.tileset.tileHeight);
		int tileSheetCol = tMap.image.width / tMap.tileset.tileWidth;
		//int mapsize = tMap.width * tMap.height;
		int destX, destY;
		int srcX, srcY;

		for (int i = 0; i < tMap.layers.size(); i++)  //picks the layer
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
			int t_element = tMap.layers.get(i).data.get(index) - 1;
			srcY = (t_element / tileSheetCol) * tMap.tileset.tileWidth;
			srcX = (t_element % tileSheetCol) * tMap.tileset.tileHeight;
			g.drawPixmap(tMap.image.pmImage, destX * tMap.tileset.tileWidth, destY * tMap.tileset.tileHeight, srcX, srcY, tMap.tileset.tileWidth, tMap.tileset.tileHeight);
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
