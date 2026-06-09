package com.vortexrpg.enchantments.fabric.core;

import net.minecraft.util.Formatting;

/** Rarity tiers, mirroring the Paper edition's colours/weights. */
public enum EnchantRarity {

    COMMON(Formatting.WHITE, "Common", 100),
    UNCOMMON(Formatting.GREEN, "Uncommon", 60),
    RARE(Formatting.BLUE, "Rare", 30),
    EPIC(Formatting.DARK_PURPLE, "Epic", 15),
    LEGENDARY(Formatting.GOLD, "Legendary", 6),
    MYTHIC(Formatting.RED, "Mythic", 2);

    private final Formatting color;
    private final String displayName;
    private final int defaultWeight;

    EnchantRarity(Formatting color, String displayName, int defaultWeight) {
        this.color = color;
        this.displayName = displayName;
        this.defaultWeight = defaultWeight;
    }

    public Formatting getColor() { return color; }
    public String getDisplayName() { return displayName; }
    public int getDefaultWeight() { return defaultWeight; }
}
