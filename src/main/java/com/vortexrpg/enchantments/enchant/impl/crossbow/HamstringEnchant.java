package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Hamstring: Bolt slows target and reduces jump height for 3/5/7 seconds.
 * Crippling shot that grounds the enemy.
 */
public class HamstringEnchant extends VortexEnchant {

    public HamstringEnchant() {
        super("hamstring", "Hamstring", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int duration = cfgi("duration", (1 + level * 2)) * 20;
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1, false, false));
        // Negative jump boost = reduced jump height
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, 250, false, false));

        ParticleUtil.spawn(victim.getLocation(), Particle.DAMAGE_INDICATOR, 4, 0.2);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level * 2;
        return "§7Bolt: §cSlow II §7+ §creduced jump §7for §e" + dur + "s§7.";
    }
}
