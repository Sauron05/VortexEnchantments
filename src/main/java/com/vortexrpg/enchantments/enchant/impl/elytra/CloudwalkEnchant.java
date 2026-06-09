package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Cloudwalk — Elytra (Rare, Max 3)
 * While gliding, receive Slow Falling permanently so you can adjust landing gracefully.
 */
public class CloudwalkEnchant extends VortexEnchant {

    public CloudwalkEnchant() {
        super("cloudwalk", "Cloudwalk", "elytra");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "Passively grants §aSlow Falling§7 while gliding.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0, true, false, false));
    }
}
