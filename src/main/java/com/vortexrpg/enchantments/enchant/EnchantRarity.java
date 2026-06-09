package com.vortexrpg.enchantments.enchant;

import org.bukkit.Color;

public enum EnchantRarity {

    COMMON("§f", "Common", 100, Color.fromRGB(220, 220, 220)),
    UNCOMMON("§a", "Uncommon", 60, Color.fromRGB(85, 255, 85)),
    RARE("§9", "Rare", 30, Color.fromRGB(85, 85, 255)),
    EPIC("§5", "Epic", 15, Color.fromRGB(170, 0, 170)),
    LEGENDARY("§6", "Legendary", 6, Color.fromRGB(255, 170, 0)),
    MYTHIC("§c", "Mythic", 2, Color.fromRGB(255, 55, 55));

    private final String color;
    private final String displayName;
    private final int defaultWeight;
    private final Color particleColor;

    EnchantRarity(String color, String displayName, int defaultWeight, Color particleColor) {
        this.color = color;
        this.displayName = displayName;
        this.defaultWeight = defaultWeight;
        this.particleColor = particleColor;
    }

    public String getColor() { return color; }
    public String getDisplayName() { return displayName; }
    public int getDefaultWeight() { return defaultWeight; }
    public Color getParticleColor() { return particleColor; }

    public String format(String name) {
        return color + name;
    }

    public String formatWithRarity(String name) {
        return color + name + " §8[" + color + displayName + "§8]";
    }
}
