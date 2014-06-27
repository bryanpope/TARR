package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;
import com.framework.impl.Vector;

public class TacticalCombatVehicle
{
	VehicleStatsCurrent vehicle;
	GangMembers interior;
	GangMembers exterior;
	int xPos;
	int yPos;
    int turningCounter;
	int speedCurrent;
	Direction facing;
    int brake;
    int accelerate;
	int maneuverability;
    boolean isSpeedChanged = false;
    boolean isAccelerated = false;
    boolean isBraked = false;
    //boolean isTurnedLeft = true;
    //boolean isTurnedRight = true;
    //boolean isStraight = true;
    //boolean isRight = false;
    //boolean isLeft = false;
    boolean isMoved = false;
    boolean isAttacked = false;
    List<Node> thePath = new ArrayList<Node>();
    int target;

    TacticalCombatVehicle (VehicleStatsCurrent v, GangMembers i, GangMembers e, boolean isPlayer)
    {
        vehicle = v;
        interior = new GangMembers(i);
        exterior = new GangMembers(e);
        speedCurrent = 30;
        brake = 0;
        accelerate = 0;
        facing = isPlayer ? Direction.EAST : Direction.WEST;
        maneuverability = v.statsBase.maneuverability;
    }

    void accelerate()
    {
        if(accelerate < vehicle.statsBase.acceleration)
        {
            speedCurrent += 10;
            accelerate++;
            isSpeedChanged = true;
        }
    }

    boolean allowAcceleration()
    {
        if(!isSpeedChanged && accelerate < 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean allowBreaking()
    {
        if(!isSpeedChanged && brake < 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean allowMoving ()
    {
        return !isMoved;
    }

    void brake()
    {
        if(brake < vehicle.statsBase.braking)
        {
            speedCurrent -= 10;
            brake++;
            isSpeedChanged = true;
        }

    }

    void move ()
    {
        Vector vectorDirection = Direction.getDirectionVector(facing);
        xPos = xPos + vectorDirection.x;
        yPos = yPos + vectorDirection.y;
        isMoved = true;
        isAccelerated = true;
        isBraked = true;
        isSpeedChanged = true;
    }

    boolean allowTurning()
    {
        if(!isMoved && (maneuverability > turningCounter))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    void currentManeuverability()
    {
        if(speedCurrent > 30)
        {
            maneuverability -= 1;
        }
        else if (speedCurrent < 30)
        {
            maneuverability++;
            if(maneuverability > vehicle.statsBase.maneuverability)
            {
                maneuverability = vehicle.statsBase.maneuverability;
            }
        }
    }

    void resetValues()
    {
        brake = 0;
        accelerate = 0;
        //speedCurrent = 30;
        //maneuverability = vehicle.statsBase.maneuverability;
        isAccelerated = false;
        isBraked = false;
        //isTurnedLeft = true;
        //isTurnedRight = true;
        //isStraight = true;
        //isRight = false;
        //isLeft = false;
        isMoved = false;
        isAttacked = false;
        isSpeedChanged = false;
    }

    void turnLeft()
    {
        facing = Direction.turnLeft(facing);
        turningCounter++;
    }

    void turnRight()
    {
        facing = Direction.turnRight(facing);
        turningCounter++;
    }

}
