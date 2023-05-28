package com.flora30.diveregion.teleport;

import com.flora30.diveconstant.data.teleport.AreaRegion;
import com.flora30.diveconstant.data.teleport.StartRegion;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.FirstJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportTrigger {
    public static void onMove(PlayerMoveEvent e){
        StartTP.check(e.getPlayer());
        VoidTP.check(e.getPlayer());
        TeleportMain.check(e.getPlayer());
    }

    public static void onCommand(Player player,String subCommand,String sub2, String sub3){
        switch (subCommand){
            case "curse":
                PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
                player.sendMessage("curse = "+data.getLayerData().getCurse());
                break;
            case "void":
                VoidTP.putRegion(player,Integer.parseInt(sub2));
                break;
            case "area":
                if (sub3.equals("to")){
                    AreaRegion areaRegion = TeleportMain.getAreaTeleport(sub2);
                    if (areaRegion == null){
                        Bukkit.getLogger().info("[DiveCore-Teleport]region取得に失敗しました");
                        return;
                    }
                    areaRegion.setTo(player.getLocation().clone());
                    player.sendMessage("Toを登録しました");
                }
                else if(sub3.equals("error")){
                    AreaRegion areaRegion = TeleportMain.getAreaTeleport(sub2);
                    if (areaRegion == null){
                        Bukkit.getLogger().info("[DiveCore-Teleport]region取得に失敗しました");
                        return;
                    }
                    areaRegion.setError(player.getLocation().clone());
                    player.sendMessage("Errorを登録しました");
                }
                else{
                    TeleportMain.addAreaTeleport(player,sub2);
                }
                break;
            case "start":
                if (sub3.equals("addStart")){
                    StartRegion startRegion = StartTP.getRegion(sub2);
                    if (startRegion == null){
                        Bukkit.getLogger().info("[DiveCore-Teleport]region取得に失敗しました");
                        return;
                    }
                    startRegion.getLocations().add(player.getLocation().clone());
                    player.sendMessage("スタート座標を追加しました");
                    return;
                }
                StartTP.putRegion(player,sub2);
                break;
        }
    }

    /**
     * 初期地点へGO
     */
    public static void onFirstJoin(FirstJoinEvent event) {
        assert VoidTP.returnPoint.getWorld() != null;
        event.getPlayer().teleport(VoidTP.returnPoint.getWorld().getSpawnLocation());
    }

    public static void onRespawn(PlayerRespawnEvent event){
        event.setRespawnLocation(VoidTP.returnPoint);
    }
}
