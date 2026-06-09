package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Sirocco: Arrow impact pushes target in the arrow's direction + applies Slowness I for 2s.
 * Desert wind — shoves enemies backward and slows them.
 */
public class SiroccoEnchant extends VortexEnchant {

    public SiroccoEnchant() {
        super("sirocco", "Sirocco", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double pushForce = cfgd("push_force", 0.5 + level * 0.3);
        Vector direction = shooter.getLocation().getDirection().normalize().multiply(pushForce).setY(0.2);
        victim.setVelocity(direction);

        int slowDuration = cfgi("slow_duration", 40);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDuration, 0, false, false));
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrow §epushes §7target + §eSlowness I §72s.";
    }
}
