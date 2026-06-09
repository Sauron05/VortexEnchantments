package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Omniscient: Passively reveals ALL entities (including invisible ones) within X blocks.
 * Invisible players receive a brief Glowing outline particle for the wearer.
 */
public class OmniscientEnchant extends VortexEnchant {
    public OmniscientEnchant() { super("omniscient", "Omniscient", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 12.0 + level * 6.0);
        for (org.bukkit.entity.LivingEntity e : player.getWorld().getNearbyLivingEntities(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            if (e.isInvisible() || e.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) {
                ParticleUtil.spawn(e.getLocation().add(0, 1, 0), Particle.WITCH, 3, 0.3);
            }
        }
    }

    @Override public String getDescription(int level) {
        return "§7Reveals invisible entities within §a" + (int)(12 + level * 6) + " §7blocks.";
    }
}
