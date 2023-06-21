package com.flora30.diveregion.penalty;

import com.flora30.divelib.data.player.LayerData;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.HelpEvent;
import com.flora30.divelib.event.HelpType;
import com.flora30.divelib.util.PacketUtil;
import com.flora30.divelib.data.LayerObject;
import com.flora30.divelib.data.penalty.Penalty;
import com.flora30.diveregion.DiveRegion;
import com.flora30.diveregion.teleport.VoidTP;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PenaltyMain {

    //layerID | 負荷
    private static final Map<String, List<Penalty>> penaltyMap = LayerObject.INSTANCE.getPenaltyMap();

    /**
     * プレイヤーの移動によって上昇負荷を増減させて、呪いの発生を判定する
     */
    public static void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if(data == null) return;
        LayerData layerData = data.getLayerData();
        //ペナルティがない時
        if(penaltyMap.get(layerData.getLayer()) == null || penaltyMap.get(layerData.getLayer()).isEmpty()){
            avoidPenalty(player);
        }
        // その他の条件
        if (e.getTo() == null) return;
        if (!VoidTP.getIdSet().contains(layerData.getLayer())) return;

        double fromY = e.getFrom().getY();
        double toY = e.getTo().getY();

        // layerNameから取得できるワールドにいない場合（実例：リスポーン直後）
        Location centerPoint = VoidTP.getRegion(layerData.getLayer()).getCenterPoint();
        if (e.getFrom().getWorld() != null && !e.getFrom().getWorld().equals(centerPoint.getWorld())) return;

        // 現在の階層の中心からの距離
        double distance = e.getFrom().distance(centerPoint.clone().add(0,fromY,0));
        // 距離に応じた負荷倍率（y= 1.5 - x/1000）
        double curseRate = 1.5 - (distance / 900);
        //Bukkit.getLogger().info("curseRate = "+curseRate+"(distance = "+distance+")");
        // この移動で変化する負荷量
        double curseAmount = curseRate * (toY - fromY);

        // 負荷を変化させる（to が fromより低い場合、自動的に減少する）
        // 最小値は 0
        layerData.setCurse(Math.max(layerData.getCurse()+curseAmount,0));

        // 1階層に4回ほど(25)で発動
        if (layerData.getCurse() >= 25) {
            executePenalty(player);
            layerData.setCurse(0);
        }

        displayCount(player);
    }

    public static void removeDisplay(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data != null && data.getBossBar() != null){
            data.getBossBar().removePlayer(player);
            data.setBossBar(null);
        }
    }

    /**
     * 全員のボスバー表示を強制的に消去する
     * 通常の動作中に使わない
     */
    public static void removeAllDisplay(){
        for (Player player : Bukkit.getOnlinePlayers()){
            removeDisplay(player);
        }
    }

    public static void displayCount(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        double curse = data.getLayerData().getCurse();
        BossBar currentBar = data.getBossBar();
        //bossbarを表示する必要があるかで分岐
        if(curse < 0.1){
            //表示する必要が無いとき
            if(!Objects.isNull(currentBar)){
                currentBar.removePlayer(player);
                data.setBossBar(null);
            }
        }
        else{
            double percent = curse / 25.0;
            double remain = 25.0 - curse;
            //上限は1.0
            if(percent > 1.0){
                percent = 1.0;
            }
            String title = ChatColor.DARK_RED + "上昇負荷まで >> " + (new BigDecimal(remain).setScale(1, RoundingMode.HALF_UP));
            if(Objects.isNull(currentBar)){
                //ない時
                Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.CurseGUI));
                BossBar bar = Bukkit.createBossBar(title, BarColor.PURPLE, BarStyle.SEGMENTED_10);
                bar.setProgress(0);
                data.setBossBar(bar);
                bar.addPlayer(player);
            }
            else{
                //既にある時
                currentBar.setTitle(title);
                currentBar.setProgress(percent);
            }
        }
    }

    public static void executePenalty(Player player){
        //この時点で負荷発生は確定
        LayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLayerData();
        String layerName = data.getLayer();

        // 演出をする
        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS,1,1);
        Location effectLoc = player.getLocation().clone().add(0,1,0);
        player.spawnParticle(Particle.SOUL,effectLoc,50,0.4,0.4,0.4,0.1);
        PacketUtil.INSTANCE.setBorderPacket(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,60,0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20,3));
        DiveRegion.plugin.delayedTask(6,() -> player.spawnParticle(Particle.SOUL,effectLoc,50,0.4,0.4,0.4,0.1));
        DiveRegion.plugin.delayedTask(12,() -> player.spawnParticle(Particle.SOUL,effectLoc,50,0.4,0.4,0.4,0.1));
        DiveRegion.plugin.delayedTask(18,() -> player.spawnParticle(Particle.SOUL,effectLoc,50,0.4,0.4,0.4,0.1));
        DiveRegion.plugin.delayedTask(24,() -> player.spawnParticle(Particle.SOUL,effectLoc,50,0.4,0.4,0.4,0.1));
        DiveRegion.plugin.delayedTask(40,() -> PacketUtil.INSTANCE.fadeOutBorderPacket(player,1));

        // 上昇負荷を実行する
        for (Penalty penalty : penaltyMap.get(layerName)){
            penalty.execute(player);
        }
    }

    public static void avoidPenalty(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        data.getLayerData().setCurse(0);
        removeDisplay(player);
    }
}
