package com.totalannihilationroadrage;

public class TacticalCombatVehicle 
{
	VehicleStatsCurrent vehicle;
	GangMembers interior;
	GangMembers exterior;
	int xPos;
	int yPos;
	int speedCurrent;
	Direction facing;
    int brake;
    int accelerate;
	int maneuverability;
    boolean isAccelerated = true;
    boolean isBraked = true;
    boolean isTurnedLeft = true;
    boolean isTurnedRight = true;
    boolean isStraight = true;
    boolean isRight = false;
    boolean isLeft = false;

    TacticalCombatVehicle (VehicleStatsCurrent v, GangMembers i, GangMembers e, boolean isPlayer)
    {
        vehicle = v;
        interior = new GangMembers(i);
        exterior = new GangMembers(e);
        speedCurrent = 30;
        facing = isPlayer ? Direction.EAST : Direction.WEST;
        maneuverability = v.statsBase.maneuverability;
    }

    void accelerate()
    {
        if(accelerate < vehicle.statsBase.acceleration)
        {
            speedCurrent += 10;
            accelerate++;
        }

    }

    void brake()
    {
        if(brake < vehicle.statsBase.braking)
        {
            speedCurrent -= 10;
            brake++;
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
}
