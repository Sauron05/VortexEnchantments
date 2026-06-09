package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/** Alertness: Glowing particles appear on hostile mobs within X blocks. */
public class AlertnessEnchant extends VortexEnchant {
    public AlertnessEnchant() { super("alertness", "Alertness", EnchantRarity.COMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 8.0 + level * 4.0);
        for (LivingEntity e : player.getWorld().getNearbyLivingEntities(player.getLocation(), radius)) {
            if (e instanceof org.bukkit.entity.Monster) {
                ParticleUtil.spawn(e.getLocation().add(0, e.getHeight() + 0.3, 0), Particle.SMALL_FLAME, 1, 0.1);
            }
        }
    }

    @Override public String getDescription(int level) {
        return "§7Hostile mobs within §a" + (int)(8 + level * 4) + " §7blocks are highlighted.";
    }
}
