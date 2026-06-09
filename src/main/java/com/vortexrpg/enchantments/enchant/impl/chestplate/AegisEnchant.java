package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Aegis: 10/15/20% chance to completely negate damage from a hit. */
public class AegisEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.10, 0.15, 0.20};

    public AegisEnchant() { super("aegis", "Aegis", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            event.setCancelled(true);
            com.vortexrpg.enchantments.util.ParticleUtil.ring(player.getLocation(), org.bukkit.Particle.CLOUD, 12, 1.0f);
        }
    }

    @Override public String getDescription() { return "Chance to completely block a hit."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 chance to negate all damage."; }
}
