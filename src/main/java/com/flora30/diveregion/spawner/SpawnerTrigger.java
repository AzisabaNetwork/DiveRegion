package com.flora30.diveregion.spawner;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;

public class SpawnerTrigger {
    public static final String spawnerName = "Portal";

    public static void onSpawn(MythicMobSpawnEvent e){
        String name = e.getMobType().getInternalName();
        if (!name.equals(spawnerName)){
            return;
        }
        SpawnerMain.execute(e.getLocation());
        e.setCancelled();
    }
}
