package com.flora30.diveregion.teleport;

import com.flora30.diveconstant.data.LayerObject;
import com.flora30.diveconstant.data.Whistle;
import com.flora30.diveconstant.data.WhistleObject;
import com.flora30.diveconstant.data.teleport.AreaRegion;
import com.flora30.diveconstant.data.teleport.StartRegion;
import com.flora30.divelib.data.MenuSlot;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.*;
import com.flora30.divelib.util.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeleportTrigger {
    public static void onMove(PlayerMoveEvent e){
        StartTP.check(e.getPlayer());
        VoidTP.check(e.getPlayer());
        TeleportMain.check(e.getPlayer());
    }

    public static void onCommand(Player player,String subCommand,String sub2, String sub3){
        switch (subCommand){
            case "curse":
                PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
                player.sendMessage("curse = "+data.getLayerData().getCurse());
                break;
            case "void":
                VoidTP.putRegion(player,Integer.parseInt(sub2));
                break;
            case "area":
                if (sub3.equals("to")){
                    AreaRegion areaRegion = TeleportMain.getAreaTeleport(sub2);
                    if (areaRegion == null){
                        Bukkit.getLogger().info("[DiveCore-Teleport]region取得に失敗しました");
                        return;
                    }
                    areaRegion.setTo(player.getLocation().clone());
                    player.sendMessage("Toを登録しました");
                }
                else if(sub3.equals("error")){
                    AreaRegion areaRegion = TeleportMain.getAreaTeleport(sub2);
                    if (areaRegion == null){
                        Bukkit.getLogger().info("[DiveCore-Teleport]region取得に失敗しました");
                        return;
                    }
                    areaRegion.setError(player.getLocation().clone());
                    player.sendMessage("Errorを登録しました");
                }
                else{
                    TeleportMain.addAreaTeleport(player,sub2);
                }
                break;
            case "start":
                if (sub3.equals("addStart")){
                    StartRegion startRegion = StartTP.getRegion(sub2);
                    if (startRegion == null){
                        Bukkit.getLogger().info("[DiveCore-Teleport]region取得に失敗しました");
                        return;
                    }
                    startRegion.getLocations().add(player.getLocation().clone());
                    player.sendMessage("スタート座標を追加しました");
                    return;
                }
                StartTP.putRegion(player,sub2);
                break;
        }
    }

    /**
     * 初期地点へGO
     */
    public static void onFirstJoin(FirstJoinEvent event) {
        assert VoidTP.returnPoint.getWorld() != null;
        event.getPlayer().teleport(VoidTP.returnPoint.getWorld().getSpawnLocation());
    }

    public static void onRespawn(PlayerRespawnEvent event){
        event.setRespawnLocation(VoidTP.returnPoint);
    }

    // Menu対応
    public static void onMenuOpen(MenuOpenEvent e){
        ItemStack icon = GuiItem.INSTANCE.getReturn();
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "オースに帰還する");

        // オースの場合は不要
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(e.getPlayer().getUniqueId());
        if (data.getLayerData().getLayer().equals("oldOrth")){
            icon.setType(Material.BARRIER);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "探窟中にのみ使用可能です");
            meta.setLore(lore);
            icon.setItemMeta(meta);
            e.getIconMap().put(MenuSlot.Slot4,icon);
            return;
        }

        // 条件を確認
        Whistle whistle = WhistleObject.INSTANCE.getWhistleMap().get(data.getLevelData().getWhistleRank());
        int returnDepth = whistle.getReturnDepth();

        //　現在の深さを確認
        double fallPlus = 200 - e.getPlayer().getLocation().getY();
        double fallLayer = LayerObject.INSTANCE.getLayerMap().get(data.getLayerData().getLayer()).getFall();
        int fall = (int) (fallPlus+fallLayer);

        // Loreを設定
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GOLD + "帰還可能な深度 ‣ " + ChatColor.WHITE + returnDepth + "m");
        lore.add(ChatColor.GOLD + "現在の深度 ‣ " + ChatColor.WHITE + fall + "m");
        lore.add("");
        if (fall <= returnDepth) {
            Bukkit.getPluginManager().callEvent(new HelpEvent(e.getPlayer(), HelpType.ReturnAble));
            lore.add(ChatColor.GREEN + "帰還可能です" + ChatColor.YELLOW + "（手持ちの遺物が、50%の確率で失われます）");
            lore.add(ChatColor.GRAY + "<<" + ChatColor.WHITE +  " クリックで帰還 " + ChatColor.GRAY + ">>");
        }
        else {
            icon.setType(Material.BARRIER);
            lore.add(ChatColor.RED + "帰還できません");
        }
        meta.setLore(lore);
        icon.setItemMeta(meta);
        e.getIconMap().put(MenuSlot.Slot4,icon);
    }

    public static void onMenuClick(MenuClickEvent e){
        if (e.getSlot() == MenuSlot.Slot4){
            if (e.getIcon().getType() != Material.BARRIER){
                ReturnTP.teleport(e.getPlayer());
            }
            else{
                e.setUseClickSound(false);
            }
        }
    }
}
