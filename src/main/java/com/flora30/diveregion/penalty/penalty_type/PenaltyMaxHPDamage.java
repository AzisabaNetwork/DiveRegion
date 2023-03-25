package com.flora30.diveregion.penalty.penalty_type;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class PenaltyMaxHPDamage extends Penalty {
    public PenaltyMaxHPDamage(int pAmount) {
        super(pAmount, 0,0, "0");
    }

    @Override
    public void execute(Player player) {
        AttributeInstance maxHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHP == null){
            Bukkit.getLogger().info("[DiveCore-Penalty]maxHP=null | player : "+player.getDisplayName());
            return;
        }
        double current = maxHP.getBaseValue();
        //0以下になったらkill
        if(current-getAmount()>=0){
            player.setHealth(0.0D);
        }
        else {
            //生き残ったら最大HP減少
            maxHP.setBaseValue(current - getAmount());
        }
    }
}
