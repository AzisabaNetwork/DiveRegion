package com.flora30.diveregion.teleport;

import com.flora30.diveconstant.DiveConstant;
import com.flora30.diveconstant.data.teleport.RelateParticle;
import com.flora30.diveconstant.data.teleport.TeleportObject;
import com.flora30.diveconstant.data.teleport.VoidRegion;
import com.flora30.divelib.DiveLib;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.diveregion.DiveRegion;
import com.flora30.diveregion.penalty.PenaltyMain;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VoidTP {
    //奈落テレポート
    //layerName | VoidRegion
    private static final Map<String, VoidRegion> locationMap = TeleportObject.INSTANCE.getVoidMap();

    //パーティクル座標|パーティクル
    private static final Set<RelateParticle> teleportParticles = TeleportObject.INSTANCE.getVoidParticles();
    public static Location returnPoint;

    public static void setParticle(){
        RelateParticle particle1 = new RelateParticle(1,0,0, Particle.END_ROD,0);
        RelateParticle particle2 = new RelateParticle(-1,0,0,Particle.END_ROD,0);
        RelateParticle particle3 = new RelateParticle(0,0,1,Particle.END_ROD,0);
        RelateParticle particle4 = new RelateParticle(1,0,-1,Particle.END_ROD,0);
        RelateParticle particle5 = new RelateParticle(0.7,0,0.7,Particle.END_ROD,0);
        RelateParticle particle6 = new RelateParticle(-0.7,0,0.7,Particle.END_ROD,0);
        RelateParticle particle7 = new RelateParticle(0.7,0,-0.7,Particle.END_ROD,0);
        RelateParticle particle8 = new RelateParticle(-0.7,0,-0.7,Particle.END_ROD,0);
        addParticle(particle1);
        addParticle(particle2);
        addParticle(particle3);
        addParticle(particle4);
        addParticle(particle5);
        addParticle(particle6);
        addParticle(particle7);
        addParticle(particle8);
    }

    public static void check(Player player){
        //判定
        if (player.getLocation().getY() > 50.1){
            upperCheck(player);
            return;
        }

        Location loc = player.getLocation().clone();
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if(data == null) return;
        //現在のregion
        VoidRegion region = locationMap.get(data.getLayerData().getLayer());
        if (region == null){
            return;
        }
        Location currentCenter = region.getCenterPoint();
        //テレポート先=「次」
        String nextKey = region.getNext();

        //次が無い＝帰還
        if (nextKey == null){
            player.setFallDistance(0);
            //ルートチェスト処理
            //ItemAPI.lootRandomSpawn(player, returnPoint);
            doTeleport(player,returnPoint);
            return;
        }

        Location nextCenter = locationMap.get(nextKey).getCenterPoint();
        //ルートチェスト処理
        //ItemAPI.lootRandomSpawn(player,nextCenter);
        //相対座標で次のテレポート先を取得
        double moveX = nextCenter.getX() - currentCenter.getX();
        double moveY = 150;
        double moveZ = nextCenter.getZ() - currentCenter.getZ();
        loc.add(moveX,moveY,moveZ);
        //テレポート実行
        doTeleport(player,loc);

        //階層型テレポートの後に何かを表示するならここ
        for (int i = 0; i < 10; i++) {
            DiveRegion.plugin.delayedTask(i * 2, () -> ItemAPI.displayAllRope(player,true));
        }
        playEffect(player);
    }

    public static void upperCheck(Player player){
        //判定
        if (player.getLocation().getY() <= 230){
            return;
        }

        Location loc = player.getLocation().clone();
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        //現在のregion
        VoidRegion region = locationMap.get(data.getLayerData().getLayer());
        if (region == null){
            return;
        }
        Location currentCenter = region.getCenterPoint();
        //テレポート先=「前」
        String beforeKey = region.getBefore();
        // 前が無い = オースへ帰る
        if (beforeKey == null){
            player.setFallDistance(0);
            doTeleport(player,returnPoint);
            return;
        }
        Location beforeCenter = locationMap.get(beforeKey).getCenterPoint();
        //相対座標で次のテレポート先を取得
        double moveX = beforeCenter.getX() - currentCenter.getX();
        double moveY = -150;
        double moveZ = beforeCenter.getZ() - currentCenter.getZ();
        loc.add(moveX,moveY,moveZ);
        //テレポート実行
        doTeleport(player,loc);

        //階層型テレポートの後に何かを表示するならここ
        for (int i = 0; i < 10; i++) {
            DiveRegion.plugin.delayedTask(i * 2, () -> ItemAPI.displayAllRope(player,true));
        }
        playEffect(player);
    }

    public static void putRegion(Player player, int range){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        String layer = data.getLayerData().getLayer();
        VoidRegion region = new VoidRegion(
                player.getLocation(),
                range,
                null,
                null
        );
        locationMap.put(layer,region);
        new TeleportConfig().saveVoid(layer);
    }
    public static void putRegion(String layer, VoidRegion region){
        locationMap.put(layer,region);
    }

    public static void playEffect(Player player){
        if (teleportParticles.size() == 0){
            setParticle();
        }
        //10tick後にパーティクル＋サウンド
        DiveLib.plugin.delayedTask(10,() -> {
        for(RelateParticle relate : teleportParticles){
            relate.spawnParticle(player);
        }
        player.playSound(player.getLocation().add(0,5,0), Sound.BLOCK_PORTAL_TRAVEL,1,1);
        });
    }


    public static void doTeleport(Player player, Location to){
        PenaltyMain.avoidPenalty(player);
        player.teleport(to);
    }

    public static VoidRegion getRegion(String id){
        return locationMap.get(id);
    }

    public static Set<String> getIdSet(){
        return locationMap.keySet();
    }

    public static void addParticle(RelateParticle relate){
        teleportParticles.add(relate);
    }
}