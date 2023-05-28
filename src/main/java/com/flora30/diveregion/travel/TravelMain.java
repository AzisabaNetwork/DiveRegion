package com.flora30.diveregion.travel;

import com.flora30.diveconstant.data.LayerObject;
import com.flora30.diveconstant.data.teleport.TravelData;
import com.flora30.diveconstant.data.teleport.TravelObject;
import com.flora30.divelib.data.MenuSlot;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.MenuClickEvent;
import com.flora30.divelib.event.MenuOpenEvent;
import com.flora30.diveregion.DiveRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelMain {
    // LayerName | ファストトラベルで行ける場所
    private static Map<String, List<TravelData>> travelMap = TravelObject.INSTANCE.getTravelMap();

    public static void onCommand(Player player, String sub1, String sub2) {
        String layerName = LayerObject.INSTANCE.getLayerName(player.getLocation());
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
                boolean removed = travelList.removeIf(o -> ChatColor.stripColor(""+o.getName()).equals(sub2));
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
                    player.sendMessage(" -> "+data.getName());
                }
            }
        }
    }

    public static void onMenuOpen(MenuOpenEvent e){
        ItemStack icon = new ItemStack(Material.IRON_BOOTS);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "ファストトラベル");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        // ファストトラベルが使えない場合
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(e.getPlayer().getUniqueId());
        if (data == null || !TravelObject.INSTANCE.getTravelMap().containsKey(data.getLayerData().getLayer())) {
            icon.setType(Material.BARRIER);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.WHITE + "現在の場所ではファストトラベルができません");
            meta.setLore(lore);
        }

        icon.setItemMeta(meta);

        e.getIconMap().put(MenuSlot.Slot3,icon);
    }
    public static void onMenuClick(MenuClickEvent e){
        if (e.getSlot() == MenuSlot.Slot3){
            if (e.getIcon().getType() == Material.IRON_BOOTS){
                e.getPlayer().openInventory(TravelGUI.getGui(e.getPlayer()));
            }
            else{
                e.setUseClickSound(false);
            }
        }
    }
}
