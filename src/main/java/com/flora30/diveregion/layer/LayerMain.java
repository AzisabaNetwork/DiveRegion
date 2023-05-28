package com.flora30.diveregion.layer;

import com.flora30.divelib.data.player.LayerData;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.LayerChangeEvent;
import com.flora30.diveconstant.data.Layer;
import com.flora30.diveconstant.data.LayerObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class LayerMain {

    private static final Map<String, Layer> layerMap = LayerObject.INSTANCE.getLayerMap();

    public static void layerCheck(Player player) {
        Location location = player.getLocation();
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if(data == null) return;
        LayerData layerData = data.getLayerData();
        for(String name : layerMap.keySet()){
            Layer layer = layerMap.get(name);
            if(layer.isInRange(location)){
                //layer更新時=event
                if (!name.equals(layerData.getLayer()) || !layerData.getVisitedLayers().contains(name)){
                    Bukkit.getLogger().info("layer更新判定："+name);
                    layerData.setLayer(name);
                    LayerChangeEvent event = new LayerChangeEvent(player.getUniqueId(), name, false);
                    Bukkit.getPluginManager().callEvent(event);
                    layerData.getVisitedLayers().add(name);
                }
                //Bukkit.getLogger().info("layer更新なし："+player.getDisplayName()+" -> "+data.layerData.layer);
                return;
            }
        }
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
}
