package com.flora30.diveregion.teleport;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class RelateParticle {
    private final double x;
    private final double y;
    private final double z;
    private final Particle particle;
    private final double speed;

    public RelateParticle(double x, double y, double z, String particleType, double speed){
        this.x = x;
        this.y = y;
        this.z = z;
        particle = Particle.valueOf(particleType);
        this.speed = speed;
    }

    public void spawnParticle(Player player){
        Location loc = player.getLocation().clone();
        loc.add(x,y,z);
        player.spawnParticle(particle,loc,1,0,0,0,speed);
    }
}
