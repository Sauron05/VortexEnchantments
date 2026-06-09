package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Nightfall: Night-time shots deal 20/30/40% bonus damage.
 * The darkness empowers your arrows.
 */
public class NightfallEnchant extends VortexEnchant {

    public NightfallEnchant() {
        super("nightfall", "Nightfall", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        long time = shooter.getWorld().getTime();
        // Night = 13000-23000
        if (time < 13000 || time > 23000) return;

        double bonus = cfgd("bonus", 0.10 + level * 0.10);
        event.setDamage(event.getDamage() * (1.0 + bonus));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, 8, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.10) * 100);
        return "§7Night: §8+" + pct + "% §7arrow damage.";
    }
}
