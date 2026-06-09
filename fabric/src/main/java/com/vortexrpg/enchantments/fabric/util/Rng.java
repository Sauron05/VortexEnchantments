package com.vortexrpg.enchantments.fabric.util;

import java.util.concurrent.ThreadLocalRandom;

/** Small random helpers. */
public final class Rng {

    private Rng() {}

    /** @param percent 0-100 chance of returning true. */
    public static boolean chance(double percent) {
        if (percent >= 100.0) return true;
        if (percent <= 0.0) return false;
        return ThreadLocalRandom.current().nextDouble(100.0) < percent;
    }

    public static int range(int minInclusive, int maxInclusive) {
        if (maxInclusive <= minInclusive) return minInclusive;
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    public static double range(double min, double max) {
        if (max <= min) return min;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}
