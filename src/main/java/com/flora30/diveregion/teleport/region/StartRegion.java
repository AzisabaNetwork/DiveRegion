package com.flora30.diveregion.teleport.region;

import com.flora30.diveapi.tools.Mathing;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class StartRegion{
    private final BoundingBox box;
    private final Location loc1;
    private final Location loc2;
    private final List<Location> locations = new ArrayList<>();
    public StartRegion(Location point1, Location point2) {
        double x1 = point1.getX();
        double y1 = point1.getY();
        double z1 = point1.getZ();

        double x2 = point2.getX();
        double y2 = point2.getY();
        double z2 = point2.getZ();

        box = new BoundingBox(x1, y1, z1, x2, y2, z2);
        loc1 = point1;
        loc2 = point2;
    }

    public void addLocation(Location loc){
        locations.add(loc);
    }

    public Location getRandomLocation(){
        int random = Mathing.getRandomInt(locations.size()-1);
        try{
            return locations.get(random);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public boolean isInArea(Location loc){
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return box.contains(x,y,z);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }
}
