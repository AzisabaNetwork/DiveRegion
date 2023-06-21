package com.flora30.diveregion.teleport;

import com.flora30.divelib.data.item.ItemData;
import com.flora30.divelib.data.item.ItemDataObject;
import com.flora30.divelib.data.item.ItemType;
import com.flora30.divelib.ItemMain;
import com.flora30.diveregion.penalty.PenaltyMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReturnTP {
    public static void teleport(Player player) {
        // 50%の確率で遺物を削除
        int size = player.getInventory().getSize();
        for (int i = 0; i < size; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getItemMeta() == null) continue;
            ItemData data = ItemDataObject.INSTANCE.getItemDataMap().get(ItemMain.INSTANCE.getItemId(item));
            if (data == null) continue;

            if (data.getType() == ItemType.Artifact) {
                if (Math.random() <= 0.5) {
                    player.sendMessage(ChatColor.WHITE + "遺物「"+item.getItemMeta().getDisplayName() + ChatColor.WHITE +"」が失われた・・・");
                    player.getInventory().setItem(i,null);
                }
            }
        }

        // オースの町へTP
        PenaltyMain.avoidPenalty(player);
        player.teleport(VoidTP.returnPoint);
    }
}
