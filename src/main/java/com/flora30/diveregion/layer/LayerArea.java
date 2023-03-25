package com.flora30.diveregion.layer;

public class LayerArea {
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private String world;

    public LayerArea(int x1, int x2, int z1, int z2, String world){
        setMinX(x1);
        setMaxX(x2);
        setMinZ(z1);
        setMaxZ(z2);
        setWorld(world);
    }


    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public String getWorld() {
        return world;
    }

    private void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    private void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    private void setMinX(int minX) {
        this.minX = minX;
    }

    private void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    private void setWorld(String world) {
        this.world = world;
    }
}
