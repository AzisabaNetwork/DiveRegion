package com.flora30.diveregion.penalty.penalty_type;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PenaltyPotion extends Penalty {

    private final PotionEffect effect;

    public PenaltyPotion(String pType, int pTime, int pLevel){
        super(0,pTime,pLevel,pType);
        String potionType = getType();
        int time = getTime();
        int level = getLevel();
        effect = new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(potionType)),time,level,false,false,true);
    }


    @Override
    public void execute(Player player){
        player.addPotionEffect(effect);
    }
}
