package com.yech.heavenRTPQueue;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class RandomLocationGenerator {

    public static Location generateRandomLocation(World world, Plugin plugin) {
        int minRadius = plugin.getConfig().getInt("settings.minRadius");
        int maxRadius = plugin.getConfig().getInt("settings.maxRadius");

        int x = maxRadius - minRadius;

        long seed = System.nanoTime();
        nnrandomxoroshiro128plus random = new nnrandomxoroshiro128plus(seed);

        int randomX = minRadius + random.nextInt(x + 1);
        int randomZ = minRadius + random.nextInt(x + 1);
        int highestY = world.getHighestBlockYAt(randomX, randomZ) + 1;

        return new Location(world, randomX, highestY, randomZ);
    }
}