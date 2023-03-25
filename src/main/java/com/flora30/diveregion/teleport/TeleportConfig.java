package com.flora30.diveregion.teleport;

import com.flora30.diveapi.tools.Config;
import com.flora30.diveregion.DiveRegion;
import com.flora30.diveregion.teleport.region.AreaRegion;
import com.flora30.diveregion.teleport.region.StartRegion;
import com.flora30.diveregion.teleport.region.VoidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TeleportConfig extends Config {
    private final File file;
    private final File startFile;
    private final File voidFile;

    public TeleportConfig(){
        folderCheck(DiveRegion.plugin.getDataFolder().getAbsolutePath() + "/teleport");
        file = new File(DiveRegion.plugin.getDataFolder().getAbsolutePath() + "/teleport",File.separator+"area.yml");
        startFile = new File(DiveRegion.plugin.getDataFolder().getAbsolutePath() + "/teleport",File.separator+"start.yml");
        fileCheck(startFile);
        voidFile = new File(DiveRegion.plugin.getDataFolder().getAbsolutePath() + "/teleport",File.separator+"void.yml");
        fileCheck(voidFile);
    }

    @Override
    public void load() {
        loadVoid();
        loadStart();
        loadArea();
        Bukkit.getLogger().info("[DiveCore-Teleport]ロードが完了しました");
    }

    private void loadVoid(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(voidFile);
        //ファイル内のkeyを検索
        int count = 0;
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            assert section != null;

            if (key.equals("returnPoint")){
                if (config.isLocation(key)){
                    VoidTP.returnPoint = config.getLocation(key);
                }
                else{
                    Bukkit.getLogger().info("[DiveCore-Teleport]returnPointが設定されていません");
                }
                continue;
            }


            if (!section.isInt("range") || !section.isLocation("centerPoint")){
                Bukkit.getLogger().info("[DiveCore-Teleport]ID「"+key+"」(void)の読み込みに失敗しました");
                continue;
            }
            VoidRegion region = new VoidRegion();
            region.setNext(section.getString("next",null));
            region.setBefore(section.getString("before",null));
            region.setRange(section.getInt("range"));
            region.setCenterPoint(section.getLocation("centerPoint"));
            VoidTP.putRegion(key,region);
            count++;
        }
        Bukkit.getLogger().info("[DiveCore-Teleport]奈落タイプを読み込みました["+count+"]");
    }

    private void loadStart(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(startFile);
        //ファイル内のkeyを検索
        int count = 0;
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null){
                continue;
            }

            if (!section.isLocation("loc1") || !section.isLocation("loc2")){
                Bukkit.getLogger().info("[DiveCore-Teleport]ID「"+key+"」(start)の読み込みに失敗しました");
                continue;
            }
            Location loc1 = section.getLocation("loc1");
            Location loc2 = section.getLocation("loc2");
            if (loc1 == null || loc2 == null){
                Bukkit.getLogger().info("[DiveCore-Teleport]ID「"+key+"」(start)の読み込みに失敗しました");
                continue;
            }
            StartRegion region = new StartRegion(loc1,loc2);
            if ( section.isConfigurationSection("toLocs")){
                for (String key2 : Objects.requireNonNull(section.getConfigurationSection("toLocs")).getKeys(false)){
                    if (section.isLocation("toLocs."+key2)){
                        if (section.getLocation("toLocs."+key2) != null){
                            region.addLocation(section.getLocation("toLocs."+key2));
                        }
                    }
                }
            }

            StartTP.putRegion(key,region);
            count++;
        }
        Bukkit.getLogger().info("[DiveCore-Teleport]開始タイプを読み込みました["+count+"]");
    }

    private void loadArea(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        //ファイル内のkeyを検索
        int count = 0;
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null){
                continue;
            }

            if (!section.isLocation("loc1") || !section.isLocation("loc2") || !section.isLocation("error")
             || !section.isLocation("to") || !section.isInt("npcToTalk.id") || !section.isInt("npcToTalk.progress")
             || !section.isString("layerName") || !section.isBoolean("onlyError")){
                Bukkit.getLogger().info("[DiveCore-Teleport]ID「"+key+"」(area)の読み込みに失敗しました");
                continue;
            }
            String layerName = section.getString("layerName");
            Location loc1 = section.getLocation("loc1");
            Location loc2 = section.getLocation("loc2");
            if (loc1 == null || loc2 == null){
                Bukkit.getLogger().info("[DiveCore-Teleport]ID「"+key+"」(area)の読み込みに失敗しました");
                continue;
            }
            AreaRegion region = new AreaRegion(layerName,loc1,loc2);
            region.setError(section.getLocation("error"));
            region.setTo(section.getLocation("to"));
            region.setNPC(section.getInt("npcToTalk.id"),section.getInt("npcToTalk.progress"));

            region.setOnlyError(section.getBoolean("onlyError"));

            TeleportMain.putRegion(key,region);
            count++;
        }
        Bukkit.getLogger().info("[DiveCore-Teleport]範囲タイプを読み込みました["+count+"]");
    }


    @Override
    public void save(){
        for (String id : TeleportMain.getIdSet()){
            saveArea(id);
        }
        for (String id : VoidTP.getIdSet()){
            saveVoid(id);
        }
        for (String id : StartTP.getIdSet()){
            saveStart(id);
        }
    }

    public void saveVoid(String id){
        FileConfiguration config = YamlConfiguration.loadConfiguration(voidFile);
        if (!config.isConfigurationSection(id)){
            config.createSection(id);
        }
        ConfigurationSection section = config.getConfigurationSection(id);
        assert section != null;

        VoidRegion region = VoidTP.getRegion(id);
        checkAndWrite(section,"next",region.getNext());
        checkAndWrite(section,"before",region.getBefore());
        checkAndWrite(section,"range",region.getRange());
        checkAndWrite(section,"centerPoint",region.getCenterPoint());

        try{
            config.save(voidFile);
            Bukkit.getLogger().info("[DiveCore-Teleport]奈落タイプを保存しました["+id+"]");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveStart(String id){
        FileConfiguration config = YamlConfiguration.loadConfiguration(startFile);
        if (!config.isConfigurationSection(id)){
            config.createSection(id);
        }
        ConfigurationSection section = config.getConfigurationSection(id);
        assert section != null;

        StartRegion region = StartTP.getRegion(id);
        checkAndWrite(section,"loc1",region.getLoc1());
        checkAndWrite(section,"loc2",region.getLoc2());
        //tolocs保存
        if (!section.isConfigurationSection("toLocs")){
            section.createSection("toLocs");
        }
        ConfigurationSection section2 = section.getConfigurationSection("toLocs");
        assert section2 != null;
        for (int i = 0; i < region.getLocations().size(); i++){
            checkAndWrite(section2,String.valueOf(i),region.getLocations().get(i));
        }

        try{
            config.save(startFile);
            Bukkit.getLogger().info("[DiveCore-Teleport]開始タイプを保存しました["+id+"]");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void saveArea(String id){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isConfigurationSection(id)){
            config.createSection(id);
        }
        ConfigurationSection section = config.getConfigurationSection(id);
        assert section != null;

        AreaRegion areaRegion = TeleportMain.getAreaTeleport(id);
        checkAndWrite(section,"layerName",areaRegion.getLayerName());
        checkAndWrite(section,"npcToTalk.id",areaRegion.getNpc()[0]);
        checkAndWrite(section,"npcToTalk.progress",areaRegion.getNpc()[1]);
        checkAndWrite(section,"loc1",areaRegion.getLoc1());
        checkAndWrite(section,"loc2",areaRegion.getLoc2());
        checkAndWrite(section,"error",areaRegion.getError());
        checkAndWrite(section,"onlyError",areaRegion.isOnlyError());
        checkAndWrite(section,"to",areaRegion.getTo());

        try{
            config.save(file);
            Bukkit.getLogger().info("[DiveCore-Teleport]範囲タイプを保存しました["+id+"]");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
