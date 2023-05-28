package com.flora30.diveregion;

import com.flora30.divelib.event.FirstJoinEvent;
import com.flora30.diveregion.layer.LayerMain;
import com.flora30.diveregion.penalty.PenaltyMain;
import com.flora30.diveregion.spawner.SpawnerTrigger;
import com.flora30.diveregion.teleport.TeleportTrigger;
import com.flora30.diveregion.travel.TravelGUI;
import com.flora30.diveregion.travel.TravelMain;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class Listeners implements Listener, CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            //コマンドの実行者がプレイヤーだった時

            Player player = (Player) sender;
            //Player型変数playerに今の実行者を代入する

            String subCommand = args.length == 0 ? "" : args[0];
            String[] subCommands = new String[10];
            for (int i = 1; i <= 10; i++) {
                try {
                    subCommands[i - 1] = args[i];
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | NullPointerException e) {
                    subCommands[i - 1] = "";
                }
            }
            //subCommandに引数を入れる（null対応）

            switch (command.getName()) {
                case "tel" -> TeleportTrigger.onCommand(player, subCommand, subCommands[0], subCommands[1]);
                case "travel" -> TravelMain.onCommand(player, subCommand, subCommands[0]);
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        String title = e.getView().getTitle();
        if (title.contains("ファストトラベル ‣ ")) {
            TravelGUI.onClick(e);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        TeleportTrigger.onRespawn(e);
    }

    @EventHandler
    public void onFirstJoin(FirstJoinEvent e) {
        TeleportTrigger.onFirstJoin(e);
    }

    @EventHandler
    public void onMythicMobSpawn(MythicMobSpawnEvent e){
        SpawnerTrigger.onSpawn(e);
    }

    public static int moveTick = 6;

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        PenaltyMain.onMove(e);
        if (count % moveTick == 0){
            TeleportTrigger.onMove(e);
            LayerMain.layerCheck(e.getPlayer());
        }
    }

    //とりあえずshiftキーで
    //1Tickごとに送られている
    private static int count = 0;
    public void onTimer() {
        count++;
    }
}
