package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * SoulArrow: Killing an entity with an arrow restores 2/3/4 HP to the shooter.
 * Soul harvest — reclaim life from the fallen.
 */
public class SoulArrowEnchant extends VortexEnchant {

    public SoulArrowEnchant() {
        super("soularrow", "Soul Arrow", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double afterDmg = victim.getHealth() - event.getFinalDamage();
        if (afterDmg > 0) return;

        double heal = cfgd("heal", 1.0 + level);
        double maxHp = shooter.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        shooter.setHealth(Math.min(shooter.getHealth() + heal, maxHp));

        ParticleUtil.spawn(shooter.getLocation().add(0, 1, 0), Particle.HEART, 4, 0.3);
        SoundUtil.play(shooter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.2f);
    }

    @Override
    public String getDescription(int level) {
        double heal = 1 + level;
        return "§7Kill with arrow: §c+" + heal + " HP §7restored.";
    }
}
