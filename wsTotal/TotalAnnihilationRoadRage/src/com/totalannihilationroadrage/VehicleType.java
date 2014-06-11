package com.totalannihilationroadrage;

public enum VehicleType 
{
	MOTORCYCLE,
	SIDECAR,
	COMPACT_CONVERTIBLE,
	COMPACT_HARDTOP,
	MIDSIZE_CONVERTIBLE,
	MIDSIZE_HARDTOP,
	SPORTS_CAR_CONVERTIBLE,
	SPORTS_CAR_HARDTOP,
	STATION_WAGON,
	LIMOUSINE,
	VAN,
	PICKUP_TRUCK,
	OFFROAD_CONVERTIBLE,
	OFFROAD_HARDTOP,
	BUS,
	TRACTOR,
	CONSTRUCTION_VEHICLE,
	FLATBED_TRUCK,
	TRAILER_TRUCK,
    UNKNOWN_VEHICLE;

    public static VehicleType getVehicleType (String vehicleTypeName)
    {
        if (vehicleTypeName.equals("Motorcycle")) { return MOTORCYCLE; }

        if (vehicleTypeName.equals("Sidecar")) { return SIDECAR; }

        if (vehicleTypeName.equals("Compact Convertible")) { return COMPACT_CONVERTIBLE; }

        if (vehicleTypeName.equals("Compact Hardtop")) { return COMPACT_HARDTOP; }

        if (vehicleTypeName.equals("Midsize Convertible")) { return MIDSIZE_CONVERTIBLE; }

        if (vehicleTypeName.equals("Midsize Hardtop")) { return MIDSIZE_HARDTOP; }

        if (vehicleTypeName.equals("Sports Car Convertible")) { return SPORTS_CAR_CONVERTIBLE; }

        if (vehicleTypeName.equals("Sports Car Hardtop")) { return SPORTS_CAR_HARDTOP; }

        if (vehicleTypeName.equals("Limousine")) { return LIMOUSINE; }

        if (vehicleTypeName.equals("Van")) { return VAN; }

        if (vehicleTypeName.equals("Pickup Truck")) { return PICKUP_TRUCK; }

        if (vehicleTypeName.equals("Offroad Convertible")) { return OFFROAD_CONVERTIBLE; }

        if (vehicleTypeName.equals("Offroad Hardtop")) { return OFFROAD_HARDTOP; }

        if (vehicleTypeName.equals("Bus")) { return BUS; }

        if (vehicleTypeName.equals("Tractor")) { return TRACTOR; }

        if (vehicleTypeName.equals("Construction Vehicle")) { return CONSTRUCTION_VEHICLE; }

        if (vehicleTypeName.equals("Flatbed Truck")) { return FLATBED_TRUCK; }

        if (vehicleTypeName.equals("Trailer Truck")) { return TRAILER_TRUCK; }

        return UNKNOWN_VEHICLE;
    }
}
