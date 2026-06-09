package com.vortexrpg.enchantments.enchant;

import com.vortexrpg.enchantments.VortexEnchantments;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages success/destroy rates when applying enchantment books to items.
 * Rate is per-rarity and can be modified by dust bonus and config overrides.
 */
public class SuccessRateManager {

    // Default success/destroy rates per rarity ordinal (COMMON=0 to MYTHIC=5)
    private static final double[] DEFAULT_SUCCESS = {95, 85, 70, 50, 35, 20};
    private static final double[] DEFAULT_DESTROY = {0, 0, 2, 5, 10, 15};

    private final VortexEnchantments plugin;

    public SuccessRateManager(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    /** Get the base success rate for a rarity (0-100). */
    public double getSuccessRate(EnchantRarity rarity) {
        String path = "success-rates." + rarity.name().toLowerCase() + ".success";
        return plugin.getConfig().getDouble(path, DEFAULT_SUCCESS[rarity.ordinal()]);
    }

    /** Get the destroy rate for a rarity (0-100). Destroy is checked only on failure. */
    public double getDestroyRate(EnchantRarity rarity) {
        String path = "success-rates." + rarity.name().toLowerCase() + ".destroy";
        return plugin.getConfig().getDouble(path, DEFAULT_DESTROY[rarity.ordinal()]);
    }

    /**
     * Roll the outcome for applying an enchant book to an item.
     * @param rarity The rarity of the enchantment being applied
     * @param dustBonus Additional success rate bonus from dust (0-100)
     * @return Result: SUCCESS, FAIL, or DESTROY
     */
    public ApplyResult rollApplication(EnchantRarity rarity, int dustBonus) {
        double successChance = Math.min(100, getSuccessRate(rarity) + dustBonus);
        double destroyChance = getDestroyRate(rarity);

        double roll = ThreadLocalRandom.current().nextDouble(100);

        if (roll < successChance) {
            return ApplyResult.SUCCESS;
        }

        // Failed — check destroy
        double destroyRoll = ThreadLocalRandom.current().nextDouble(100);
        if (destroyRoll < destroyChance) {
            return ApplyResult.DESTROY;
        }

        return ApplyResult.FAIL;
    }

    public enum ApplyResult {
        SUCCESS,   // Enchant applied successfully
        FAIL,      // Enchant not applied, book consumed, item safe
        DESTROY    // Enchant not applied, book consumed, item destroyed (unless White Scroll)
    }
}
