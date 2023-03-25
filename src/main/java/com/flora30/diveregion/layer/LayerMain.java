package com.flora30.diveregion.layer;

import com.flora30.diveapi.DiveAPI;
import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.data.player.LayerData;
import com.flora30.diveapi.event.LayerChangeEvent;
import com.flora30.diveapi.plugins.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class LayerMain {

    private static final Map<String, Layer> layerMap = new HashMap<>();

    public void setLayer(String name, Layer layer){
        layerMap.put(name, layer);
    }

    public static void layerCheck(Player player) {
        Location location = player.getLocation();
        PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());
        if(data == null) return;
        LayerData layerData = data.layerData;
        for(String name : layerMap.keySet()){
            Layer layer = layerMap.get(name);
            if(layer.isInRange(location)){
                //layer更新時=event
                if (!name.equals(layerData.layer) || !layerData.visitedLayers.contains(name)){
                    Bukkit.getLogger().info("layer更新判定："+name);
                    layerData.layer = (name);
                    LayerChangeEvent event = new LayerChangeEvent(player.getUniqueId(), name, false);
                    Bukkit.getPluginManager().callEvent(event);
                    layerData.visitedLayers.add(name);
                }
                //Bukkit.getLogger().info("layer更新なし："+player.getDisplayName()+" -> "+data.layerData.layer);
                return;
            }
        }
    }


    public static Layer getLayer(String name){
        return layerMap.get(name);
    }

    public static String getLayerName(Location location){
        for(String name : layerMap.keySet()){
            Layer layer = layerMap.get(name);
            if(layer.isInRange(location)){
                return name;
            }
        }
        return null;
    }

    public boolean isLayer(String name){
        return layerMap.containsKey(name);
    }
}
