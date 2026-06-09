package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Cushion: Reduces knockback taken from melee hits.
 */
public class CushionEnchant extends VortexEnchant {
    public CushionEnchant() { super("cushion", "Cushion", EnchantRarity.COMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double reduction = cfgd("kb_reduction", 0.20 * level);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Vector vel = victim.getVelocity();
            victim.setVelocity(vel.multiply(1.0 - reduction));
        }, 1L);
    }

    @Override public String getDescription(int level) {
        return "§7Reduces knockback taken by §a" + (20 * level) + "%§7.";
    }
}
