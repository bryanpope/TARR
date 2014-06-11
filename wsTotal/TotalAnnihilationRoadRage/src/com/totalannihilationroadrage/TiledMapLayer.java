package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;

public class TiledMapLayer 
{
	String name;
	int width;
	int height;
	List< Integer > data;
	
	public TiledMapLayer ()
	{
		data = new ArrayList< Integer >();
	}
	
	public int getTile (int row, int col)
	{
		return data.get((row * width) + col);
	}

    public boolean isPassable (int row, int col)
    {
        int tile = getTile(row, col);

        return true;
    }
}
