package com.vortexrpg.enchantments.enchant.impl.crossbow;

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
 * Armageddon: Bolt calls 3/5/7 lightning strikes in the area around the target.
 * The heavens rain down judgment. 15s cooldown.
 */
public class ArmageddonEnchant extends VortexEnchant {

    public ArmageddonEnchant() {
        super("armageddon", "Armageddon", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        int strikes = cfgi("strikes", 1 + level * 2);
        double radius = cfgd("radius", 4.0);
        Location center = victim.getLocation();

        for (int i = 0; i < strikes; i++) {
            double angle = (2 * Math.PI / strikes) * i;
            double x = center.getX() + radius * Math.cos(angle) * Math.random();
            double z = center.getZ() + radius * Math.sin(angle) * Math.random();
            Location strike = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().strikeLightningEffect(strike);

            for (LivingEntity entity : MathUtil.getNearbyLiving(strike, 1.5)) {
                if (entity.equals(shooter)) continue;
                entity.damage(cfgd("strike_damage", 3.0), shooter);
            }
        }

        ParticleUtil.burst(center, Particle.FLAME, 20, radius);
        SoundUtil.play(center, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);

        setCooldownFromConfig(shooter, "cooldown", 15.0);
    }

    @Override
    public String getDescription(int level) {
        int strikes = 1 + level * 2;
        return "§7Bolt: §e§l" + strikes + " LIGHTNING STRIKES §7in area. 15s CD.";
    }
}
