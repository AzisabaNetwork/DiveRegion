package com.flora30.diveregion.spawner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Spawner {
    private final Map<String,Double> mobMap = new HashMap<>();

    public void putMob(String mobName, Double rate){
        mobMap.put(mobName,rate);
    }

    public double getRate(String mobName){
        return mobMap.get(mobName);
    }

    public Set<String> getMobList(){
        return mobMap.keySet();
    }
}
