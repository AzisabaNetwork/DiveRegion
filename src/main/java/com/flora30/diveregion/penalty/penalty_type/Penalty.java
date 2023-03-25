package com.flora30.diveregion.penalty.penalty_type;

import org.bukkit.entity.Player;

public class Penalty {
    //layerのMapに登録されるペナルティ

    private final int amount;
    private final int time;
    private final int level;
    private final String type;
    public Penalty(int pAmount, int pTime, int pLevel, String pType){
        amount = pAmount;
        time = pTime;
        level = pLevel;
        type = pType;
    }

    public void execute(Player player){
    }

    public int getAmount() {
        return amount;
    }

    public int getTime() {
        return time;
    }

    public int getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }
}
