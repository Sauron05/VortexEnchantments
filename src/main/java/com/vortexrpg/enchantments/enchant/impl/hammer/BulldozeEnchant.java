package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Bulldoze: Crush targets backward with massive knockback 2/3/4 blocks.
 */
public class BulldozeEnchant extends VortexEnchant {

    public BulldozeEnchant() {
        super("bulldoze", "Bulldoze", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double distance = cfgd("knockback", 1.0 + level);
        Vector direction = attacker.getLocation().getDirection().normalize().multiply(distance).setY(0.3);
        victim.setVelocity(direction);

        ParticleUtil.spawn(victim.getLocation(), Particle.CLOUD, 10, 0.5);
    }

    @Override
    public String getDescription(int level) {
        int dist = 1 + level;
        return "§7Smash target back §e" + dist + " blocks§7.";
    }
}
