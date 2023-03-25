package com.flora30.diveregion.teleport.region;

import org.bukkit.Location;

public class VoidRegion {
    //中心
    private Location centerPoint;
    //範囲
    private int range;
    //次層id
    private String next;
    //前層id
    private String before;

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setCenterPoint(Location centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getRange() {
        return range;
    }
    public Location getCenterPoint() {
        return centerPoint;
    }

    public String getNext() {
        return next;
    }
}
