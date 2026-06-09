package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.List;

/**
 * Colossus: Falling hits (attacking while falling) deal massive bonus damage
 * that scales with fall distance, plus AoE ground slam.
 */
public class ColossusEnchant extends VortexEnchant {

    public ColossusEnchant() {
        super("colossus", "Colossus", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        float fallDist = attacker.getFallDistance();
        if (fallDist < cfgd("min_fall", 1.5)) return;

        double dmgPerBlock = cfgd("damage_per_block", 0.5 + level * 0.5);
        double maxBonus = cfgd("max_bonus", 6.0 + level * 4.0);
        double bonus = Math.min(fallDist * dmgPerBlock, maxBonus);
        event.setDamage(event.getDamage() + bonus);

        double aoeRadius = cfgd("aoe_radius", 2.0 + level);
        double aoeDamage = cfgd("aoe_damage", 2.0 + level);
        Location loc = victim.getLocation();

        for (LivingEntity nearby : MathUtil.getNearbyLiving(loc, aoeRadius)) {
            if (nearby.equals(attacker) || nearby.equals(victim)) continue;
            nearby.damage(aoeDamage, attacker);
        }

        ParticleUtil.drawCircle(loc, aoeRadius, (int) (aoeRadius * 8), Particle.BLOCK);
        ParticleUtil.spawn(loc, Particle.EXPLOSION, 1, 0.0);
        SoundUtil.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 0.6f);
        attacker.setFallDistance(0);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 0.5 + level * 0.5;
        return "§7Falling attacks deal §c+" + dmg + "/block §7bonus + AoE ground slam.";
    }
}
