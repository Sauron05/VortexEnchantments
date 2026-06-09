package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Buckler: 15/25/35% chance to negate incoming hit + counter for 50% of your attack damage.
 */
public class BucklerEnchant extends VortexEnchant {

    public BucklerEnchant() {
        super("buckler", "Buckler", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.05 + level * 0.10);
        if (Math.random() > chance) return;

        // Negate the hit
        event.setDamage(0);

        // Counter-attack if attacker is living
        if (attacker instanceof LivingEntity living) {
            double counterDmg = cfgd("counter_multiplier", 0.50) * event.getDamage();
            living.damage(Math.max(counterDmg, 1.0), victim);
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 12, 0.4);
        SoundUtil.play(victim.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.8f, 1.2f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7" + pct + "% chance to §eblock §7+ counter for §c50% §7your damage.";
    }
}
