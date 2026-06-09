package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Siphonbolt: Steal 15/25/35% of damage dealt as HP.
 * Vampiric crossbow bolt — sustain through aggression.
 */
public class SiphonboltEnchant extends VortexEnchant {

    public SiphonboltEnchant() {
        super("siphonbolt", "Siphonbolt", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double siphonPct = cfgd("siphon_pct", 0.10 + level * 0.05);
        double heal = event.getDamage() * siphonPct;
        double maxHp = shooter.getAttribute(Attribute.MAX_HEALTH).getValue();
        shooter.setHealth(Math.min(shooter.getHealth() + heal, maxHp));

        ParticleUtil.drawLine(victim.getLocation().add(0, 1, 0),
                shooter.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 0.4);
        ParticleUtil.spawn(shooter.getLocation().add(0, 1, 0), Particle.HEART, 2, 0.2);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7Steal §c" + pct + "% §7of damage as §cHP§7.";
    }
}
