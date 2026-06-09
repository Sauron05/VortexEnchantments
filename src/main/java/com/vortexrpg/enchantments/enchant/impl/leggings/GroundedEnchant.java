package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Grounded: No knockback from attacks while wearing. */
public class GroundedEnchant extends VortexEnchant {
    public GroundedEnchant() { super("grounded", "Grounded", EnchantRarity.RARE, 1, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.setVelocity(new org.bukkit.util.Vector(0, player.getVelocity().getY(), 0)), 1L);
    }

    @Override public String getDescription() { return "Prevents knockback while worn."; }
    @Override public String getDescription(int level) { return "§7No knockback from attacks."; }
}
