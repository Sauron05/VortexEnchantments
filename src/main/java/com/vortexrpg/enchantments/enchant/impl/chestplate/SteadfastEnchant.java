package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Steadfast: Immune to knockback from explosions; reduces explosion damage.
 */
public class SteadfastEnchant extends VortexEnchant {
    public SteadfastEnchant() { super("steadfast", "Steadfast", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;
        double pct = cfgd("reduction_pct", 0.10 * level);
        event.setDamage(event.getDamage() * (1.0 - pct));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.setVelocity(player.getVelocity().multiply(0.1)), 1L);
    }

    @Override public String getDescription(int level) {
        return "§7Reduces explosion damage by §a" + (10 * level) + "% §7and negates explosion knockback.";
    }
}
