package com.flora30.diveregion.teleport.region;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

public class AreaRegion {
    private final String layer;
    private final BoundingBox box;
    private final Location loc1;
    private final Location loc2;
    private boolean onlyError;
    private Location to;
    private Location error;
    private final int[] npc = {-1,-1};

    public AreaRegion(String layerName, Location point1, Location point2){
        double x1 = point1.getX();
        double y1 = point1.getY();
        double z1 = point1.getZ();

        double x2 = point2.getX();
        double y2 = point2.getY();
        double z2 = point2.getZ();

        box = new BoundingBox(x1,y1,z1,x2,y2,z2);
        layer = layerName;
        loc1 = point1;
        loc2 = point2;
    }

    public void setOnlyError(boolean onlyError) {
        this.onlyError = onlyError;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public void setError(Location error) {
        this.error = error;
    }

    public String getLayerName() {
        return layer;
    }

    public boolean isInArea(Location loc){
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return box.contains(x,y,z);
    }

    public Location getTo() {
        return to;
    }

    public Location getError() {
        return error;
    }

    public void setNPC(int npcId, int progress){
        npc[0] = npcId;
        npc[1] = progress;
    }

    public boolean isOnlyError() {
        return onlyError;
    }

    public int[] getNpc() {
        return npc;
    }

    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }
}
