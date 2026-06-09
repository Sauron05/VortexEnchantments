package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Skylord: Allies within range gain Slow Falling while you glide. */
public class SkylordEnchant extends VortexEnchant {

    public SkylordEnchant() { super("skylord", "Skylord", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double radius = cfgd("radius", 8.0 + level * 2);
        for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (e == player || !(e instanceof Player ally)) continue;
            ally.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0, true, false, true));
        }
    }

    @Override public String getDescription() { return "Allies gain Slow Falling near your flight."; }
    @Override public String getDescription(int level) {
        return "§7Allies within §e" + (int)(8.0 + level * 2) + "§7 blocks gain §aSlow Falling§7 while you glide."; }
}
