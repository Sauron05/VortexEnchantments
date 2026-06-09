package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Flameguard: Fire damage heals you for a portion instead of hurting.
 */
public class FlameguardEnchant extends VortexEnchant {
    public FlameguardEnchant() { super("flameguard", "Flameguard", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.FIRE && cause != EntityDamageEvent.DamageCause.FIRE_TICK
                && cause != EntityDamageEvent.DamageCause.LAVA) return;
        double convertPct = cfgd("convert_pct", 0.20 * level);
        double heal = event.getDamage() * convertPct;
        event.setCancelled(true);
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
        ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.HEART, 3, 0.4);
    }

    @Override public String getDescription(int level) {
        return "§7Fire damage is converted to §a" + (20 * level) + "% §7healing.";
    }
}
