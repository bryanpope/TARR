package com.totalannihilationroadrage;

public class VehicleStatsCurrent 
{
	final VehicleStatsBase statsBase;
	final int id;
	int structure;
	int tires;
    private static int counter;

    VehicleStatsCurrent (VehicleStatsBase sBase)
    {
        id = counter++;
        statsBase = sBase;
        structure = statsBase.structure;
        tires = statsBase.tires;
    }
}
