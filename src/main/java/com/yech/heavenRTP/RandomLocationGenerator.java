package com.yech.heavenRTP;

import org.bukkit.Location;
import org.bukkit.World;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLocationGenerator {
    public static Location generateRandomLocation(World world, int range) {
        long seed = ThreadLocalRandom.current().nextLong();
        nnrandomxoroshiro128plus random = new nnrandomxoroshiro128plus(seed);

        int x = random.nextInt(range * 2) - range;
        int z = random.nextInt(range * 2) - range;
        int y = world.getHighestBlockYAt(x, z) + 1;

        return new Location(world, x, y, z);
    }
}