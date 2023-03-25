package com.flora30.diveregion.travel;

import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.diveregion.DiveRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelMain {
    // LayerName | ファストトラベルで行ける場所
    public static Map<String, List<TravelData>> travelMap = new HashMap<>();

    public static void onCommand(Player player, String sub1, String sub2) {
        String layerName = RegionAPI.getLayerName(player.getLocation());
        if (layerName == null) {
            player.sendMessage(ChatColor.RED + "どこかのエリア内にいる必要があります");
            return;
        }
        if (!travelMap.containsKey(layerName)) {
            travelMap.put(layerName,new ArrayList<>());
        }

        List<TravelData> travelList = travelMap.get(layerName);

        switch (sub1) {
            case "add" -> {
                ItemStack icon = new ItemStack(Material.STONE);
                ItemMeta meta = icon.getItemMeta();
                assert meta != null;
                meta.setDisplayName(ChatColor.GOLD+sub2);
                icon.setItemMeta(meta);

                TravelData data = new TravelData(icon,player.getLocation().clone());
                travelList.add(data);
                player.sendMessage(layerName+"のファストトラベルに"+sub2+"を追加しました");
                DiveRegion.plugin.asyncTask(() -> DiveRegion.travelConfig.save(layerName));
            }
            case "remove" -> {
                boolean removed = travelList.removeIf(o -> ChatColor.stripColor(o.name).equals(sub2));
                if (removed) {
                    player.sendMessage(layerName+"のファストトラベルから"+sub2+"を削除しました");
                    DiveRegion.plugin.asyncTask(() -> DiveRegion.travelConfig.save(layerName));
                } else {
                    player.sendMessage(layerName+"のファストトラベルに"+sub2+"は存在しません");
                }
            }
            case "list" -> {
                player.sendMessage("==== "+layerName+"のファストトラベル一覧 ====");
                for (TravelData data : travelList) {
                    player.sendMessage(" -> "+data.name);
                }
            }
        }
    }
}
