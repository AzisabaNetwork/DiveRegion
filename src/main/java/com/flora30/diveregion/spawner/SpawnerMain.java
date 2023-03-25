package com.flora30.diveregion.spawner;

import com.flora30.diveapi.tools.Mathing;
import com.flora30.diveregion.layer.LayerMain;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class SpawnerMain {

    //layerID | spawner
    private static final Map<String,Spawner> spawnerMap = new HashMap<>();

    public static void execute(Location location){
        assert location.getWorld() != null;
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location,20,20,20);
        entities.removeIf(entity -> entity instanceof Player);
        if (entities.size() > 3) return;


        // モブを取得
        MythicMob mob = getRandomMob(LayerMain.getLayerName(location));
        if (mob == null) return;

        // モブを召喚する
        mob.spawn(BukkitAdapter.adapt(location),1);
    }

    public static MythicMob getRandomMob(String layerName){
        if (!spawnerMap.containsKey(layerName)) return null;

        Spawner spawner = spawnerMap.get(layerName);

        double calcedRate = 0;
        int randomized = Mathing.getRandomInt( 100);
        for (String name : spawner.getMobList()){
            double rate = calcedRate+spawner.getRate(name);
            if (randomized <= rate){
                return MythicMobs.inst().getAPIHelper().getMythicMob(name);
            }
            calcedRate = rate;
        }

        return null;
    }

    public static void putSpawner(String layer, Spawner spawner){
        spawnerMap.put(layer,spawner);
    }
}
