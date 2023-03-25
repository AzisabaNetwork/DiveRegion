package com.flora30.diveregion.penalty.penalty_type;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.plugins.CoreAPI;
import org.bukkit.entity.Player;

public class PenaltyFood extends Penalty {
    public PenaltyFood(int pAmount) {
        super(pAmount,0,0,"0");
    }

    @Override
    public void execute(Player player) {
        PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());

        int current = data.food;

        // 最小は0
        data.food = Math.max(current - getAmount(), 0);
    }
}
