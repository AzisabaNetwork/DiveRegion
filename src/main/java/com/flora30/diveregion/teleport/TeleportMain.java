package com.flora30.diveregion.teleport;

import com.flora30.diveconstant.data.teleport.AreaRegion;
import com.flora30.diveconstant.data.teleport.TeleportObject;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.diveregion.layer.LayerMain;
import com.flora30.diveregion.penalty.PenaltyMain;
import com.flora30.diveregion.teleport.worldedit.UseWorldEdit;
import com.flora30.diveregion.teleport.worldedit.WorldEditRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TeleportMain {

    //範囲テレポート
    //プレイヤーが入る判定：PlayerMoveEvent：エリア：box照合
    //内部にエリアを登録しておく
    private static final Map<String, AreaRegion> areaTeleportMap = TeleportObject.INSTANCE.getAreaMap();

    //startはここから分岐
    public static void check(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if(data == null) return;
        Location loc = player.getLocation().clone();
        for(AreaRegion region : areaTeleportMap.values()){
            //エリアが違う場合を除く
            if(!region.getLayerName().equals(data.getLayerData().getLayer())){
                continue;
            }
            //範囲内のとき
            if(region.isInArea(loc)){
                //Bukkit.getLogger().info("範囲内判定");
                //npc条件が存在するとき
                int[] npcData = region.getNpcCondition();
                if(npcData[0] >= 0){
                    //プレイヤーのnpc進捗
                    int current = data.getNpcData().getTalkProgress(npcData[0]);
                    //npc条件を満たしていない時
                    if(current < npcData[1]){
                        //エラー座標に送る
                        doTeleport(player,region.getError());
                        player.sendMessage(ChatColor.RED +"通行条件を満たしていません");
                    }
                    else{
                        if (region.getTo() != null){
                            doTeleport(player,region.getTo());
                        }
                        return;
                    }
                }

            }
        }
    }

    public static void doTeleport(Player player, Location to){
        PenaltyMain.avoidPenalty(player);
        player.teleport(to);
    }

    //nameは個別入力
    public static void addAreaTeleport(Player player, String name){
        WorldEditRegion region = UseWorldEdit.getRegion(player);
        //エラーを除外
        if(region == null){
            player.sendMessage("範囲の取得に失敗しました");
            return;
        }

        AreaRegion areaTP = new AreaRegion(LayerMain.getLayerName(player.getLocation()),region.getMin_location(),region.getMax_location());
        areaTeleportMap.put(name,areaTP);
        new TeleportConfig().save();
    }


    public static AreaRegion getAreaTeleport(String name){
        return areaTeleportMap.get(name);
    }

    public static void putRegion(String name, AreaRegion areaRegion){
        areaTeleportMap.put(name, areaRegion);
    }

    public static Set<String> getIdSet(){
        return areaTeleportMap.keySet();
    }
}
