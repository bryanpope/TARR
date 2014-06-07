package com.TotalApocalypse_RoadRage;

import java.util.List;

import android.graphics.Color;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.mrnom.Settings;
import com.badlogic.androidgames.mrnom.World;


public class OverviewMap extends Screen 
{
	enum GameState
	{
		Ready,
		Running,
		Paused
	}
	
	GameState state = GameState.Ready;
    World world;
    
    public OverviewMap(Game game) 
    {
        super(game);
        world = new World();
    }
    
    public void update(float deltaTime)
    {
    }

    public void present(float deltaTime)
    {
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
	
