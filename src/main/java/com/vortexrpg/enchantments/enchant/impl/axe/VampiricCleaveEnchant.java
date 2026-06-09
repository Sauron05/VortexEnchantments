package com.vortexrpg.enchantments.enchant.impl.axe;

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
 * VampiricCleave: AoE hit that heals for a percentage of damage dealt to all targets.
 * Lifesteal: 15/20/25% of AoE damage. AoE radius: 3/4/5.
 */
public class VampiricCleaveEnchant extends VortexEnchant {

    public VampiricCleaveEnchant() {
        super("vampiriccleave", "Vampiric Cleave", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 2.0 + level);
        double aoeDmgPct = cfgd("aoe_pct", 0.4 + level * 0.1);
        double lifesteal = cfgd("lifesteal", 0.10 + level * 0.05);
        double baseDmg = event.getDamage();

        double totalDmg = baseDmg;
        for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (nearby.equals(attacker) || nearby.equals(victim)) continue;
            double aoeDmg = baseDmg * aoeDmgPct;
            nearby.damage(aoeDmg, attacker);
            totalDmg += aoeDmg;
        }

        double heal = totalDmg * lifesteal;
        double maxHealth = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        attacker.setHealth(Math.min(attacker.getHealth() + heal, maxHealth));

        ParticleUtil.spawn(attacker.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 5, 0.3);
        ParticleUtil.drawCircle(victim.getLocation(), radius, 10, Particle.DUST_PLUME);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.3f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        int r = (int) (2 + level);
        return "§7AoE cleave within §e" + r + " blocks§7. Heal for §c" + pct + "% §7of total damage.";
    }
}
