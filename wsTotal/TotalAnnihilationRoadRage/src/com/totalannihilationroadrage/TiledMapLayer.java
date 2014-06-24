package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;

public class TiledMapLayer 
{
	String name;  //name of the layer
	int width;  //the number of tiles wide for the layer
	int height;  //the number of tiles high for the layer
	List< Integer > data;  //the index of each tile.
	
	public TiledMapLayer ()
	{
		data = new ArrayList< Integer >();
	}
	
	public int getTile (int row, int col)
	{
         int test = data.size();
        if (((row * width) + col) >= test)
        {
            System.out.print("Something bad!");
        }
		return data.get((row * width) + col) - 1;
	}

    /*public boolean isPassable (int row, int col)
    {
        int tile = getTile(row, col);

        if (tile == 2)
        {
            return false;
        }
        return true;
    }
    */
}
