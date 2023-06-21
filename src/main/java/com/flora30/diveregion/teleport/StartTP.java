package com.flora30.diveregion.teleport;

import com.flora30.divelib.data.teleport.StartRegion;
import com.flora30.divelib.data.teleport.TeleportObject;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.diveregion.penalty.PenaltyMain;
import com.flora30.diveregion.teleport.worldedit.UseWorldEdit;
import com.flora30.diveregion.teleport.worldedit.WorldEditRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StartTP {
    //String = layerID
    private static final Map<String, StartRegion> teleportMap = TeleportObject.INSTANCE.getStartMap();

    public static void check(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if(data == null) return;
        Location loc = player.getLocation().clone();
        for(String id : teleportMap.keySet()){
            //エリアが違う場合を除く
            if(!id.equals(data.getLayerData().getLayer())){
                continue;
            }

            StartRegion region = teleportMap.get(id);
            //範囲内のとき
            if(region.isInArea(loc)){
                doTeleport(player,region.getRandomLocation());
                return;
            }
        }
    }


    public static void doTeleport(Player player, Location to){
        PenaltyMain.avoidPenalty(player);
        player.teleport(to);
    }

    public static void putRegion(String id, StartRegion region){
        teleportMap.put(id,region);
    }

    public static void putRegion(Player player, String layer){
        WorldEditRegion region = UseWorldEdit.getRegion(player);
        //エラーを除外
        if(region == null){
            player.sendMessage("範囲の取得に失敗しました");
            return;
        }

        StartRegion startTP = new StartRegion(region.getMin_location(),region.getMax_location());
        teleportMap.put(layer,startTP);
    }

    public static StartRegion getRegion(String id){
        return teleportMap.get(id);
    }


    public static Set<String> getIdSet(){
        return teleportMap.keySet();
    }
}
