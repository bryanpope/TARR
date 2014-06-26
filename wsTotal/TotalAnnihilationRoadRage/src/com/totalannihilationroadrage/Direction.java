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

    private static Direction[] vals = values();

    public static Direction turnRight(Direction dir)
    {
        if(dir == Direction.NORTHWEST)
        {
            return Direction.NORTH;
        }
        return vals[(dir.ordinal() + 1) % vals.length];
    }

    public static Direction turnLeft(Direction dir)
    {
        if(dir == Direction.NORTH)
        {
            return Direction.NORTHWEST;
        }

        return vals[(dir.ordinal() - 1) % vals.length];
    }

    public static RotationTransformation getRotationTransformation (Direction dir)
    {
        RotationTransformation rotTrans = new RotationTransformation();

        switch (dir)
        {
            case NORTH:
                rotTrans.angle = 270.0f;
                rotTrans.isFlippedHorizontally = true;
                break;
            case NORTHEAST:
                rotTrans.angle = 315.0f;
                break;
            case EAST:
                rotTrans.angle = 0.0f;
                break;
            case SOUTHEAST:
                rotTrans.angle = 45.0f;
                break;
            case SOUTH:
                rotTrans.angle = 90.0f;
                break;
            case SOUTHWEST:
                rotTrans.angle = 135.0f;
                rotTrans.isFlippedHorizontally = true;
                break;
            case WEST:
                rotTrans.angle = 180.0f;
                rotTrans.isFlippedHorizontally = true;
                break;
            case NORTHWEST:
                rotTrans.angle = 225.0f;
                rotTrans.isFlippedHorizontally = true;
                break;
        }
        return rotTrans;
    }

    /*public static float getAngle (Direction dir)
    {
        switch (dir)
        {
            case NORTH:
                return -90.0f;
            case NORTHEAST:
                return 315.0f;
            case EAST:
                return 0.0f;
            case SOUTHEAST:
                return 45.0f;
            case SOUTH:
                return 90.0f;
            //return 270.0f;
            case SOUTHWEST:
                return -225.0f;
            //return 225.0f;
            case WEST:
                return -180.0f;
            case NORTHWEST:
                return -135.0f;
            //return -135.0f;
        }
        return 0.0f;
    }*/
}
