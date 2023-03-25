package com.flora30.diveregion.teleport;

import com.flora30.diveapi.data.ItemData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.tools.ItemType;
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
            ItemData data = ItemAPI.getItemData(ItemAPI.getItemID(item));
            if (data == null) continue;

            if (data.type == ItemType.Artifact) {
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
