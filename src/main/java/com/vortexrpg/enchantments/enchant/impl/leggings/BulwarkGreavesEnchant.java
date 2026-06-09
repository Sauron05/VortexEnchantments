package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * BulwarkGreaves: Reduces knockback taken from melee attacks.
 */
public class BulwarkGreavesEnchant extends VortexEnchant {
    public BulwarkGreavesEnchant() { super("bulwark_greaves", "Bulwark Greaves", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double reduction = cfgd("kb_reduction", 0.25 * level);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Vector vel = victim.getVelocity();
            victim.setVelocity(vel.multiply(1.0 - reduction));
        }, 1L);
    }

    @Override public String getDescription(int level) {
        return "§7Reduces melee knockback by §a" + (25 * level) + "%§7.";
    }
}
