package com.vortexrpg.enchantments.enchant.impl.helmet;

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
 * Transcendence: When taking damage that would reduce HP below 20%,
 * release a massive AoE knockback + heal 30% of max HP. 45s CD.
 */
public class TranscendenceEnchant extends VortexEnchant {
    public TranscendenceEnchant() { super("transcendence", "Transcendence", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double afterHp = victim.getHealth() - event.getFinalDamage();
        if (afterHp > maxHp * 0.2) return;

        double healPct = cfgd("heal_pct", 0.20 + level * 0.05);
        double radius = cfgd("radius", 5.0 + level);
        double force = cfgd("force", 1.0 + level * 0.3);

        event.setDamage(0);
        victim.setHealth(Math.min(maxHp, victim.getHealth() + maxHp * healPct));

        for (LivingEntity e : com.vortexrpg.enchantments.util.MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (e.equals(victim)) continue;
            org.bukkit.util.Vector push = e.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize().multiply(force);
            push.setY(0.5);
            e.setVelocity(push);
        }

        ParticleUtil.burst(victim.getLocation(), Particle.END_ROD, 40, 2.0);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.8f, 1.2f);
        setCooldownFromConfig(victim, "cooldown", 45.0);
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.20 + level * 0.05) * 100);
        return "§7Below 20% HP: §dAoE knockback §7+ heal §a" + pct + "%§7 max HP. §845s CD.";
    }
}
