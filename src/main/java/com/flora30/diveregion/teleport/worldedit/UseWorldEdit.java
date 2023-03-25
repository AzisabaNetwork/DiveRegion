package com.flora30.diveregion.teleport.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.*;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
public class UseWorldEdit{

    public static WorldEditRegion getRegion(Player player) {
        //playerのlocationに初期化

        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(worldEdit == null){
            return null;
        }
        World we_world = BukkitAdapter.adapt(player.getWorld());
        try {
            Region select = worldEdit.getSession(player).getSelection(we_world);
            if (select != null) {
                BlockVector3 min = select.getMinimumPoint();
                BlockVector3 max = select.getMaximumPoint();
                //min_location.setX(min.getBlockX());
                //min_location.setY(min.getBlockY());
                //min_location.setZ(min.getBlockZ());
                Location min_location = new Location (player.getWorld(),min.getX(),min.getY(),min.getZ());

                //max_location.setX(max.getBlockX());
                //max_location.setY(max.getBlockY());
                //max_location.setZ(max.getBlockZ());
                Location max_location = new Location (player.getWorld(),max.getX(),max.getY(),max.getZ());
                return new WorldEditRegion(min_location,max_location);
            }
        } catch (IncompleteRegionException e){
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "[" + ChatColor.RESET + "" + ChatColor.GREEN + "BossFinder" + ChatColor.AQUA + "" + ChatColor.BOLD + "]" + ChatColor.RESET + ChatColor.RED + "Worldeditで範囲を選択してください");
            return null;
        }
        return null;
    }
}