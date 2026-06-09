package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Ballast: Target pushed in exact arrow direction.
 */
public class BallastEnchant extends VortexEnchant {

    public BallastEnchant() {
        super("ballast", "Ballast", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        double mult = cfg("knockback_multiplier", 0.5 + level * 0.3);
        Vector dir = arrow.getVelocity().normalize().multiply(mult);
        victim.setVelocity(dir);
    }

    @Override
    public String getDescription() { return "Pushes targets in the exact direction the arrow traveled."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow knockback follows exact arrow direction with §e×" + (0.5 + level*0.3) + "§7 force.";
    }
}
