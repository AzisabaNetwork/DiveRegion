package com.flora30.diveregion;

import com.flora30.diveconstant.DiveConstant;
import com.flora30.divelib.DiveLib;
import com.flora30.diveregion.layer.LayerConfig;
import com.flora30.diveregion.penalty.PenaltyMain;
import com.flora30.diveregion.teleport.TeleportConfig;
import com.flora30.diveregion.travel.TravelConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class DiveRegion extends JavaPlugin {

    public static DiveRegion plugin;
    public static TravelConfig travelConfig;
    final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;

        // ロード
        layerLoad();
        new TeleportConfig().load();
        travelConfig = new TravelConfig();
        travelConfig.load();

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginCommand("tel").setExecutor(new Listeners());
        getServer().getPluginCommand("travel").setExecutor(new Listeners());
    }

    // Core、Questのイベント登録後（LayerLoadEvent）
    public void layerLoad() {
        if (DiveLib.plugin.getCoreEventReady() && DiveLib.plugin.getQuestEventReady()) {
            new LayerConfig().load();
            return;
        }

        DiveLib.plugin.delayedTask(2, this::layerLoad);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PenaltyMain.removeAllDisplay();

        TeleportConfig teleportLS = new TeleportConfig();
        teleportLS.save();
    }

    public void asyncTask(Runnable task) {
        scheduler.runTaskAsynchronously(this,task);
    }

    public void delayedTask(int delay, Runnable task) {
        scheduler.runTaskLater(this,task,delay);
    }
}
