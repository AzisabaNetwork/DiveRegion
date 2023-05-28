package com.flora30.diveregion.travel;

import com.flora30.diveconstant.data.teleport.TravelData;
import com.flora30.diveconstant.data.teleport.TravelObject;
import com.flora30.divelib.util.Config;
import com.flora30.diveregion.DiveRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TravelConfig extends Config {
    public final File file;

    public TravelConfig() {
        file = new File(DiveRegion.plugin.getDataFolder().getAbsolutePath(),"travel.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().info("[DiveRegion-Travel] 設定ファイルの作成に失敗しました");
            }
        }
    }

    @Override
    public void load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String layerName : config.getKeys(false)) {
            ConfigurationSection layerSec = config.getConfigurationSection(layerName);
            assert layerSec != null;

            if (!TravelObject.INSTANCE.getTravelMap().containsKey(layerName)) {
                TravelObject.INSTANCE.getTravelMap().put(layerName,new ArrayList<>());
            }
            List<TravelData> travelList = TravelObject.INSTANCE.getTravelMap().get(layerName);

            for (String index : layerSec.getKeys(false)) {
                ConfigurationSection indexSec = layerSec.getConfigurationSection(index);
                assert indexSec != null;

                // 必要なデータを設定ファイルから取得
                Material material;
                try {
                    material = Material.valueOf(indexSec.getString("material"));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().info("[DiveRegion-Travel] "+layerName+" - "+index+" の取得に失敗しました");
                    continue;
                }

                String name = indexSec.getString("name");
                List<String> lore = indexSec.getStringList("lore");
                Location loc = indexSec.getLocation("location");

                if (name == null || loc == null) {
                    Bukkit.getLogger().info("[DiveRegion-Travel] "+layerName+" - "+index+" の取得に失敗しました");
                    continue;
                }

                // TravelDataに必要なIconを作成
                ItemStack icon = new ItemStack(material);
                ItemMeta meta = icon.getItemMeta();
                assert meta != null;
                meta.setDisplayName(ChatColor.GOLD + name);
                lore.replaceAll(s -> ChatColor.WHITE + s);
                meta.setLore(lore);
                icon.setItemMeta(meta);

                // TravelDataを作成
                TravelData data = new TravelData(icon,loc);
                travelList.add(data);
            }


            Bukkit.getLogger().info("[DiveRegion-Travel] "+layerName+" のファストトラベル情報を読み込みました");
        }
    }

    @Override
    public void save() {

    }

    public void save(String layerName) {
        List<TravelData> travelList = TravelObject.INSTANCE.getTravelMap().get(layerName);
        if (travelList == null) {
            Bukkit.getLogger().info("[DiveRegion-Travel] "+layerName+"のファストトラベルデータは存在しないため、保存できません");
            return;
        }

        // createSection : 以前のデータを完全に消して上書きする
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection layerSec = config.createSection(layerName);

        // configに内容を保存していく
        int size = travelList.size();
        for (int i = 0; i < size; i++) {
            ConfigurationSection indexSec = layerSec.createSection(String.valueOf(i));
            TravelData data = travelList.get(i);
            ItemMeta meta = data.getIcon().getItemMeta();
            assert meta != null;

            indexSec.set("name",""+data.getName());
            indexSec.set("lore",meta.getLore());
            indexSec.set("material",data.getIcon().getType().toString());
            indexSec.set("location",data.getLocation());
        }

        // ファイルに保存する
        try{
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().info("[DiveRegion-Travel] "+layerName+"のファストトラベル情報の保存に失敗しました");
        }
        Bukkit.getLogger().info("[DiveRegion-Travel] "+layerName+"のファストトラベル情報を保存しました");
    }
}
