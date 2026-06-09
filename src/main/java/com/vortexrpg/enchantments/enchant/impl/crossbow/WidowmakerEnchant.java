package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Widowmaker: 10/15/20% chance for a headshot dealing 3x damage.
 * Lethal precision strike.
 */
public class WidowmakerEnchant extends VortexEnchant {

    public WidowmakerEnchant() {
        super("widowmaker", "Widowmaker", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        boolean headshot = MathUtil.isHeadshot(victim, event.getDamager().getLocation(), 0.25);
        if (!headshot) {
            double chance = cfgd("chance", 0.05 + level * 0.05);
            if (!MathUtil.chance(chance)) return;
        }

        double multiplier = cfgd("multiplier", 3.0);
        event.setDamage(event.getDamage() * multiplier);

        ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.CRIT, 15, 0.2);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 0.5f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7" + pct + "% §cheadshot chance §7— §c3x damage§7.";
    }
}
