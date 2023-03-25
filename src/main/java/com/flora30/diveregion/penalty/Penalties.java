package com.flora30.diveregion.penalty;

import com.flora30.diveregion.penalty.penalty_type.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Penalties {
    private final Set<Penalty> penaltySet1 = new HashSet<>();

    public boolean isNoPenalty(){
        return penaltySet1.isEmpty();
    }

    public void execute(Player player){
        for(Penalty penalty : getPenaltySet()){
            penalty.execute(player);

            /*
            // イベントを発生させる理由が分からないのでCO
            if (penalty instanceof PenaltyDamage penaltyDamage){
                PenaltyDamageEvent penaltyDamageEvent = new PenaltyDamageEvent(penaltyDamage);
                Bukkit.getPluginManager().callEvent(penaltyDamageEvent);
                penaltyDamageEvent.getPenaltyDamage().execute(player);
            } else if(penalty instanceof PenaltyFood penaltyFood){
                PenaltyFoodEvent penaltyFoodEvent = new PenaltyFoodEvent(penaltyFood);
                Bukkit.getPluginManager().callEvent(penaltyFoodEvent);
                penaltyFoodEvent.getPenaltyFood().execute(player);
            } else if(penalty instanceof PenaltyMaxHPDamage penaltyMaxHPDamage){
                PenaltyMaxHPDamageEvent penaltyMaxHPDamageEvent = new PenaltyMaxHPDamageEvent(penaltyMaxHPDamage);
                Bukkit.getPluginManager().callEvent(penaltyMaxHPDamageEvent);
                penaltyMaxHPDamageEvent.getPenaltyMaxHPDamage().execute(player);
            } else {
                PenaltyPotion penaltyPotion = (PenaltyPotion) penalty;
                PenaltyPotionEvent penaltyPotionEvent = new PenaltyPotionEvent(penaltyPotion);
                Bukkit.getPluginManager().callEvent(penaltyPotionEvent);
                penaltyPotionEvent.getPenalty_potion().execute(player);
            }
             */
        }
    }

    public Set<Penalty> getPenaltySet(){
        return penaltySet1;
    }

    public void addPenalty(Penalty penalty){
        penaltySet1.add(penalty);
    }
}
