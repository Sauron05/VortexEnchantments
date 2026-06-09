package com.vortexrpg.enchantments.enchant.impl.spear;

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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Harpoon: Hits hook the target and pull them 3/4/5 blocks toward the attacker.
 * Turns the spear into a grappling weapon.
 */
public class HarpoonEnchant extends VortexEnchant {

    public HarpoonEnchant() {
        super("harpoon", "Harpoon", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double pullStrength = cfgd("pull_strength", 0.3 + level * 0.15);

        Vector dir = attacker.getLocation().toVector()
                .subtract(victim.getLocation().toVector()).normalize().multiply(pullStrength);
        dir.setY(Math.max(dir.getY(), 0.2));
        victim.setVelocity(dir);

        ParticleUtil.drawLine(attacker.getLocation().add(0, 1, 0),
                victim.getLocation().add(0, 1, 0), Particle.FISHING, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0f, 0.8f);

        setCooldownFromConfig(attacker, "cooldown", 6);
    }

    @Override
    public String getDescription(int level) {
        return "§7Hits §bhook §7and §bpull §7the target toward you.";
    }
}
