package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;

public class TiledMapLayer 
{
	String name;
	int width;
	int height;
	List< Integer > data;
	
	TiledMapLayer ()
	{
		data = new ArrayList< Integer >();
	}
	
	int getTile (int row, int col)
	{
		return data.get((row * width) + col);
	}
}
