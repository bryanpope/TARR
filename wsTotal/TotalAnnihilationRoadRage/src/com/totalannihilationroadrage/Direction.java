package com.totalannihilationroadrage;

public enum Direction 
{
	NORTH,
	NORTHEAST,
	EAST,
	SOUTHEAST,
	SOUTH,
	SOUTHWEST,
	WEST,
	NORTHWEST;

    public static float getAngle (Direction dir)
    {
        switch (dir)
        {
            case NORTH:
                return 90.0f;
            case NORTHEAST:
                return 45.0f;
            case EAST:
                return 0.0f;
            case SOUTHEAST:
                return 315.0f;
            case SOUTH:
                return 270.0f;
            case SOUTHWEST:
                return 225.0f;
            case WEST:
                return 180.0f;
            case NORTHWEST:
                return 135.0f;
        }
        return 0.0f;
    }
}
