package com.flora30.diveregion.spawner;

import com.flora30.diveapin.util.Mathing;
import com.flora30.divenew.data.LayerObject;
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
    private static final HashMap<String, List<LayerObject.MobData>> mobMap = LayerObject.INSTANCE.getMobMap();

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
        if (!mobMap.containsKey(layerName)) return null;

        List<LayerObject.MobData> mobDataList = mobMap.get(layerName);

        double calcedRate = 0;
        int randomized = Mathing.INSTANCE.getRandomInt( 100);
        for (LayerObject.MobData data : mobDataList){
            double rate = calcedRate + data.getRate();
            if (randomized <= rate){
                return MythicMobs.inst().getAPIHelper().getMythicMob(data.getMobName());
            }
            calcedRate = rate;
        }

        return null;
    }
}
