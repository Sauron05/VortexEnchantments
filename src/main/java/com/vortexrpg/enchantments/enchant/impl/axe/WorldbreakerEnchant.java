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
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Worldbreaker: Killing an enemy triggers a massive shockwave.
 * AoE damage: 4/6/8 in 5/7/9 block radius. Launches enemies.
 */
public class WorldbreakerEnchant extends VortexEnchant {

    public WorldbreakerEnchant() {
        super("worldbreaker", "Worldbreaker", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 3.0 + level * 2.0);
        double aoeDamage = cfgd("aoe_damage", 2.0 + level * 2.0);
        double launchPower = cfgd("launch_power", 0.5 + level * 0.3);
        Location center = victim.getLocation();

        for (LivingEntity nearby : MathUtil.getNearbyLiving(center, radius)) {
            if (nearby.equals(killer)) continue;
            nearby.damage(aoeDamage, killer);

            org.bukkit.util.Vector push = nearby.getLocation().toVector()
                    .subtract(center.toVector()).normalize()
                    .multiply(launchPower).setY(launchPower);
            nearby.setVelocity(push);
        }

        ParticleUtil.drawCircle(center, radius, (int) radius, Particle.EXPLOSION);
        ParticleUtil.spawn(center, Particle.LARGE_SMOKE, 40, radius * 0.5);
        SoundUtil.play(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
        SoundUtil.play(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 0.8f);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 2.0 + level * 2.0;
        int r = (int) (3 + level * 2);
        return "§7Kills trigger a §4shockwave§7 dealing §c" + dmg + " damage §7in §e" + r + " blocks§7.";
    }
}
