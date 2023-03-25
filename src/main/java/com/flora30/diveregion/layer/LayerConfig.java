package com.flora30.diveregion.layer;

import com.flora30.diveapi.event.LayerLoadEvent;
import com.flora30.diveapi.tools.Config;
import com.flora30.diveregion.DiveRegion;
import com.flora30.diveregion.penalty.Penalties;
import com.flora30.diveregion.penalty.PenaltyMain;
import com.flora30.diveregion.penalty.penalty_type.*;
import com.flora30.diveregion.spawner.Spawner;
import com.flora30.diveregion.spawner.SpawnerMain;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class LayerConfig extends Config {
    private static File[] areaFiles = new File[100];
    private static final LayerMain main = new LayerMain();

    public LayerConfig(){
        folderCheck(DiveRegion.plugin.getDataFolder().getAbsolutePath() + "/area");
        areaFiles = new File(DiveRegion.plugin.getDataFolder().getAbsolutePath() + "/area").listFiles();
    }

    //ファイルを作成・読み取りする機能
    //プレイヤーデータではないので内部更新はなし

    @Override
    public void load(){
        //areaフォルダでループ
        for(File file2 : areaFiles){
            FileConfiguration config = YamlConfiguration.loadConfiguration(file2);

            //フォルダ内の1番目でループ(key=layerID)
            for (String key : config.getKeys(false)){
                ConfigurationSection section = config.getConfigurationSection(key);
                if (section == null){
                    continue;
                }
                //座標取得→Layer新規作成
                String x = section.getString("location.X");
                String z = section.getString("location.Z");
                if(x == null || z == null){
                    continue;
                }
                String[] x2 = x.split(",");
                String[] z2 = z.split(",");
                String world = section.getString("location.world");

                //エリア作成
                LayerArea area = new LayerArea(Integer.parseInt(x2[0]),Integer.parseInt(x2[1]),Integer.parseInt(z2[0]),Integer.parseInt(z2[1]),world);
                Layer layer = new Layer(area);
                Penalties penalties = new Penalties();
                Spawner spawner = new Spawner();

                //表示名
                layer.displayName = section.getString("displayName");
                layer.groupName = section.getString("groupName", key);
                layer.fall = section.getInt("fall", 0);

                //ペナルティがあるか
                if (section.getBoolean("hasPenalty")){
                    //first
                    Set<Penalty> penaltySet = generatePenalty(section.getStringList("penalty"));
                    for (Penalty penalty : penaltySet){
                        penalties.addPenalty(penalty);
                    }
                }

                main.setLayer(key, layer);
                PenaltyMain.setPenalties(key,penalties);

                //mob
                if (section.isConfigurationSection("mob")){
                    for (String mobName : section.getConfigurationSection("mob").getKeys(false)){
                        MythicMob mob = MythicMobs.inst().getAPIHelper().getMythicMob(mobName);
                        if (mob == null){
                            Bukkit.getLogger().info("[DiveCore-Spawn]階層「"+ layer.displayName+"」のmob["+mobName+"]の取得に失敗しました");
                            continue;
                        }
                        double rate = loadOrDefault("Spawn",section,"mob."+mobName,0);
                        spawner.putMob(mobName,rate);
                    }
                }
                else{
                    Bukkit.getLogger().info("[DiveRegion-Spawn]階層「"+ layer.displayName+"」のmob判定に失敗しました");
                }
                SpawnerMain.putSpawner(key,spawner);

                LayerLoadEvent event = new LayerLoadEvent(key, section);
                Bukkit.getLogger().info("[LayerLoad]Event fired");
                DiveRegion.plugin.getServer().getPluginManager().callEvent(event);

                Bukkit.getLogger().info("[DiveRegion-Layer]階層「"+ layer.displayName+"」");
            }
        }
        Bukkit.getLogger().info("[DiveRegion-Layer]階層のロードが完了しました");
    }

    @Override
    public void save() {

    }


    //penaltyを読む部分
    private Set<Penalty> generatePenalty(List<String>loadedList){
        Set<Penalty> generated = new HashSet<>();

        for(String key : loadedList) {
            List<String> separatedKeys = Arrays.asList(key.split(" "));
            //type分岐
            try {
                switch (separatedKeys.get(0)) {
                    case "HP":
                        int amount = Integer.parseInt(separatedKeys.get(1));
                        generated.add(new PenaltyDamage(amount));
                        break;
                    case "MaxHP":
                        int amount2 = Integer.parseInt(separatedKeys.get(1));
                        generated.add(new PenaltyMaxHPDamage(amount2));
                        break;
                    case "Food":
                        int amount3 = Integer.parseInt(separatedKeys.get(1));
                        generated.add(new PenaltyFood(amount3));
                        break;
                    case "Potion":
                        String type = separatedKeys.get(1);
                        int time = Integer.parseInt(separatedKeys.get(2));
                        int level = Integer.parseInt(separatedKeys.get(3));
                        generated.add(new PenaltyPotion(type,time,level));
                }
            } catch (NumberFormatException | PatternSyntaxException e) {
                e.printStackTrace();
            }
        }return generated;
    }
}
