package com.flora30.diveregion.penalty.penalty_type;

import org.bukkit.entity.Player;

public class PenaltyDamage extends Penalty {
    public PenaltyDamage(int pAmount) {
        super(pAmount, 0, 0, "0");
    }

    @Override
    public void execute(Player player) {
        player.damage(getAmount());
    }
}
