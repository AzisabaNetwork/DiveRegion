package com.flora30.diveregion.layer;

import com.flora30.divelib.data.Story;
import com.flora30.divelib.event.LayerLoadEvent;
import com.flora30.divelib.util.Config;
import com.flora30.divelib.data.Layer;
import com.flora30.divelib.data.LayerArea;
import com.flora30.divelib.data.LayerObject;
import com.flora30.divelib.data.penalty.*;
import com.flora30.diveregion.DiveRegion;
import com.flora30.diveregion.spawner.SpawnerMain;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

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
                String y = section.getString("location.Y");
                String z = section.getString("location.Z");
                if(x == null || y == null || z == null){
                    continue;
                }
                String[] x2 = x.split(",");
                String[] y2 = y.split(",");
                String[] z2 = z.split(",");
                String world = section.getString("location.world");

                //エリア作成
                LayerArea area = new LayerArea(Integer.parseInt(x2[0]),Integer.parseInt(x2[1]),
                        Integer.parseInt(y2[0]),Integer.parseInt(y2[1]),
                        Integer.parseInt(z2[0]),Integer.parseInt(z2[1]),
                        world);
                Layer layer = new Layer(
                        area,
                        section.getString("displayName"),
                        section.getString("groupName", key),
                        section.getInt("fall", 0),
                        section.getBoolean("isTown",false),
                        section.getInt("exp",0),
                        new Story(
                                section.getString("displaySub"),
                                applyColor(section.getStringList("story")),
                                section.getBoolean("noticeDisplay")
                        ),
                        section.getStringList("gimmickList")
                );
                List<Penalty> penaltyList = new ArrayList<>();
                List<LayerObject.MobData> mobDataList = new ArrayList<>();

                //ペナルティがあるか
                if (section.getBoolean("hasPenalty")){
                    //first
                    Set<Penalty> penaltySet = generatePenalty(section.getStringList("penalty"));
                    penaltyList.addAll(penaltySet);
                }

                LayerObject.INSTANCE.getLayerMap().put(key, layer);
                LayerObject.INSTANCE.getPenaltyMap().put(key,penaltyList);

                //mob
                if (section.isConfigurationSection("mob")){
                    for (String mobName : section.getConfigurationSection("mob").getKeys(false)){
                        MythicMob mob = MythicMobs.inst().getAPIHelper().getMythicMob(mobName);
                        if (mob == null){
                            Bukkit.getLogger().info("[DiveCore-Spawn]階層「"+ layer.getDisplayName()+"」のmob["+mobName+"]の取得に失敗しました");
                            continue;
                        }
                        double rate = loadOrDefault("Spawn",section,"mob."+mobName,0);
                        mobDataList.add(new LayerObject.MobData(mobName,rate));
                    }
                }
                else{
                    Bukkit.getLogger().info("[DiveRegion-Spawn]階層「"+ layer.getDisplayName()+"」のmob判定に失敗しました");
                }
                LayerObject.INSTANCE.getMobMap().put(key,mobDataList);

                LayerLoadEvent event = new LayerLoadEvent(key, section);
                Bukkit.getLogger().info("[LayerLoad]Event fired");
                DiveRegion.plugin.getServer().getPluginManager().callEvent(event);

                Bukkit.getLogger().info("[DiveRegion-Layer]階層「"+ layer.getDisplayName()+"」");
            }
        }
        Bukkit.getLogger().info("[DiveRegion-Layer]階層のロードが完了しました");
    }
    private Story getStory(ConfigurationSection section){
        return new Story(
                section.getString("displaySub"),
                applyColor(section.getStringList("story")),
                section.getBoolean("noticeDisplay")
        );
    }
    private ArrayList<String> applyColor(List<String> list){
        ArrayList<String> generated = new ArrayList<>();
        for(String str : list){
            generated.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return generated;
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
                    case "HP" -> {
                        int amount = Integer.parseInt(separatedKeys.get(1));
                        generated.add(new PenaltyDamage(amount));
                    }
                    case "MaxHP" -> {
                        int amount2 = Integer.parseInt(separatedKeys.get(1));
                        generated.add(new PenaltyMaxHPDamage(amount2));
                    }
                    case "Food" -> {
                        int amount3 = Integer.parseInt(separatedKeys.get(1));
                        generated.add(new PenaltyFood(amount3));
                    }
                    case "Potion" -> {
                        String type = separatedKeys.get(1);
                        int time = Integer.parseInt(separatedKeys.get(2));
                        int level = Integer.parseInt(separatedKeys.get(3));
                        generated.add(new PenaltyPotion(PotionEffectType.getByName(type), time, level));
                    }
                }
            } catch (NumberFormatException | PatternSyntaxException | NullPointerException e) {
                e.printStackTrace();
            }
        }return generated;
    }
}
