package com.flora30.diveregion.teleport;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveregion.penalty.PenaltyMain;
import com.flora30.diveregion.teleport.region.StartRegion;
import com.flora30.diveregion.teleport.worldedit.UseWorldEdit;
import com.flora30.diveregion.teleport.worldedit.WorldEditRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StartTP {
    //String = layerID
    private static final Map<String, StartRegion> teleportMap = new HashMap<>();

    public static void check(Player player){
        PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());
        if(data == null) return;
        Location loc = player.getLocation().clone();
        for(String id : teleportMap.keySet()){
            //エリアが違う場合を除く
            if(!id.equals(data.layerData.layer)){
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
