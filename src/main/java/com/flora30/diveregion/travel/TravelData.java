package com.flora30.diveregion.travel;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class TravelData {
    public String name;
    public ItemStack icon;
    public Location location;

    public TravelData(ItemStack icon, Location location) {
        this.icon = icon;
        this.location = location;
        if (icon.getItemMeta() == null) {
            name = null;
            return;
        }
        name = icon.getItemMeta().getDisplayName();
    }
}
