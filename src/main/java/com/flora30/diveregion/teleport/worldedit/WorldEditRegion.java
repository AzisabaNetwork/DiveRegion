package com.flora30.diveregion.teleport.worldedit;

import org.bukkit.Location;

public class WorldEditRegion{

    private final Location min_location;
    private final Location max_location;

    public WorldEditRegion(Location min, Location max){
        min_location = min;
        max_location = max;
    }

    public Location getMax_location() {
        return max_location;
    }

    public Location getMin_location() {
        return min_location;
    }
}