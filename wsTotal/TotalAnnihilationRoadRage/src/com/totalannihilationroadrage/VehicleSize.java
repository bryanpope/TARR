package com.totalannihilationroadrage;

public enum VehicleSize 
{
	SMALL,
	MEDIUM,
	LARGE,
    UNKNOWN_SIZE;

    public static VehicleSize getVehicleSize (String vehicleSizeName)
    {
        if (vehicleSizeName.equals("S")) { return SMALL; }

        if (vehicleSizeName.equals("M")) { return MEDIUM; }

        if (vehicleSizeName.equals("L")) { return LARGE; }

        return UNKNOWN_SIZE;
    }
}
