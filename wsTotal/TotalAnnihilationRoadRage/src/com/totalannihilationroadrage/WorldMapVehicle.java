package com.totalannihilationroadrage;

/**
 * Created by Lord_Oni on 6/25/2014.
 */
public class WorldMapVehicle
{
    int xPos;
    int yPos;
    Direction facing;

    WorldMapVehicle(boolean isPlayer)
    {
        facing = isPlayer ? Direction.EAST : Direction.WEST;
    }
}
