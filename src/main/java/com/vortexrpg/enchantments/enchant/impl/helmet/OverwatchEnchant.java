package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Overwatch: Shows all players within X blocks through walls with Glowing effect.
 */
public class OverwatchEnchant extends VortexEnchant {
    public OverwatchEnchant() { super("overwatch", "Overwatch", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 16.0 + level * 8.0);
        for (org.bukkit.entity.LivingEntity e : player.getWorld().getNearbyLivingEntities(player.getLocation(), radius)) {
            if (e instanceof Player target && !target.equals(player)) {
                ParticleUtil.spawn(target.getLocation().add(0, 2.2, 0), Particle.END_ROD, 1, 0.0);
            }
        }
    }

    @Override public String getDescription(int level) {
        return "§7Reveals players within §a" + (int)(16 + level * 8) + " §7blocks with particles.";
    }
}
