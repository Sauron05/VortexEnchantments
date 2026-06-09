package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Turbulence: Enemies near you get debuffed while you glide. */
public class TurbulenceEnchant extends VortexEnchant {

    public TurbulenceEnchant() { super("turbulence", "Turbulence", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double radius = cfgd("radius", 4.0 + level);
        int duration = 40;
        for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (e == player || !(e instanceof LivingEntity le)) continue;
            if (le instanceof Player) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 0, true, false));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 0, true, false));
        }
    }

    @Override public String getDescription() { return "Debuff nearby enemies while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Enemies within §e" + (int)(4.0 + level) + "§7 blocks get §cWeakness + Slowness§7 while you glide."; }
}
