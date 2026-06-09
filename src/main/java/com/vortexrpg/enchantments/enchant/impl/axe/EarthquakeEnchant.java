package com.vortexrpg.enchantments.enchant.impl.axe;

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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Earthquake: On hit, smash the ground creating a radial shockwave
 * that launches all nearby enemies into the air and deals 3/5/7 damage.
 */
public class EarthquakeEnchant extends VortexEnchant {

    public EarthquakeEnchant() {
        super("earthquake", "Earthquake", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 10.0);
        double radius = cfgd("radius", 5.0);
        double damage = cfgd("damage", 1.0 + level * 2.0);
        double launchPower = cfgd("launch_power", 0.8);

        setCooldownSeconds(attacker, cooldown);

        ParticleUtil.drawCircle(attacker.getLocation(), radius, 30, Particle.BLOCK);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 0.8f, 0.5f);

        for (Entity e : attacker.getNearbyEntities(radius, radius, radius)) {
            if (e.equals(attacker) || !(e instanceof LivingEntity le)) continue;
            le.damage(damage, attacker);
            Vector launch = new Vector(0, launchPower, 0);
            le.setVelocity(le.getVelocity().add(launch));
        }
    }

    @Override
    public String getDescription(int level) {
        double dmg = 1.0 + level * 2.0;
        return "§7Smash the ground: §4shockwave§7 launches enemies + §c" + String.format("%.0f", dmg) + " damage§7.";
    }
}
