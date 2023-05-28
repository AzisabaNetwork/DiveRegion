package com.flora30.diveregion.travel;

import com.flora30.diveconstant.data.teleport.TravelData;
import com.flora30.diveconstant.data.teleport.TravelObject;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.HelpEvent;
import com.flora30.divelib.event.HelpType;
import com.flora30.divelib.util.GuiItem;
import com.flora30.diveregion.teleport.TeleportMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public class TravelGUI {

    public static final List<Integer> travelRegion = List.of(10,11,12,13,14,15,16);

    public static Inventory getGui(Player player) {
        Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.FastTravelGUI));
        PlayerData playerData = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (playerData == null) return null;
        String layerName = playerData.getLayerData().getLayer();
        if (!TravelObject.INSTANCE.getTravelMap().containsKey(layerName)) {
            Bukkit.getLogger().info("[DiveRegion-TravelGUI] "+layerName+" のTravelデータがありません");
            return null;
        }
        List<TravelData> travelList = TravelObject.INSTANCE.getTravelMap().get(layerName);
        int travelSize = Math.min(travelList.size(), travelRegion.size());

        Story story = QuestAPI.getStory(layerName);
        if (story == null) {
            Bukkit.getLogger().info("[DiveRegion-TravelGUI] "+layerName+" の表示名がありません");
            return null;
        }
        String layerDisplayName = story.displayName;

        Inventory inv = Bukkit.createInventory(null,27,"ファストトラベル ‣ "+layerDisplayName);
        GuiItem.INSTANCE.grayBack(inv);

        for (int i = 0; i < travelSize; i++) {
            TravelData data = travelList.get(i);
            inv.setItem(travelRegion.get(i),data.getIcon().clone());
        }

        inv.setItem(inv.getSize() - 1, GuiItem.INSTANCE.getReturn());

        return inv;
    }

    public static void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;

        // TravelGUIにおいて、アイテムの移動は行われない
        event.setCancelled(true);

        // 戻る = メニューを開く
        if (event.getSlot() == event.getClickedInventory().getSize() - 1) {
            Player player = ((Player) event.getWhoClicked());
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
            CoreAPI.openMenu(player);
            return;
        }

        if (travelRegion.contains(event.getSlot())) {
            int i = travelRegion.indexOf(event.getSlot());
            PlayerData playerData = PlayerDataObject.INSTANCE.getPlayerDataMap().get(event.getWhoClicked().getUniqueId());
            if (playerData == null) {
                Bukkit.getLogger().info("[DiveRegion-TravelGUI] "+event.getWhoClicked().getName()+"のプレイヤーデータがありません");
                return;
            }
            String layerName = playerData.getLayerData().getLayer();

            if (!TravelObject.INSTANCE.getTravelMap().containsKey(layerName)) {
                Bukkit.getLogger().info("[DiveRegion-TravelGUI] "+layerName+"のTravelデータがありません（GUI表示名："+event.getWhoClicked().getOpenInventory().getTitle()+"）");
                return;
            }

            List<TravelData> travelList = TravelObject.INSTANCE.getTravelMap().get(layerName);
            if (travelList.size() <= i) {
                Bukkit.getLogger().info("[DiveRegion-TravelGUI]"+layerName+"の"+i+"番目Travelデータはありません");
                return;
            }

            TravelData data = travelList.get(i);

            // テレポートを行う
            // too moved quickly 対策：blockを一度呼び出す
            Block block = data.getLocation().getBlock();
            TeleportMain.doTeleport((Player) event.getWhoClicked(),block.getLocation());
            event.getWhoClicked().sendMessage(ChatColor.GOLD+"ファストトラベルを行いました ‣ "+ Objects.requireNonNull(data.getIcon().getItemMeta()).getDisplayName());
        }
    }
}
