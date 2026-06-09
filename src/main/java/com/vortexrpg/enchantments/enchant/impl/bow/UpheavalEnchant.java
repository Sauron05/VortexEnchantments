package com.vortexrpg.enchantments.enchant.impl.bow;

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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Upheaval: Arrow impact launches nearby entities upward.
 * Disrupts groups by tossing them into the air.
 */
public class UpheavalEnchant extends VortexEnchant {

    public UpheavalEnchant() {
        super("upheaval", "Upheaval", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 3.0);
        double launchPower = cfgd("launch_power", 0.4 + level * 0.2);
        Location center = victim.getLocation();

        for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
            if (entity.equals(shooter)) continue;
            entity.setVelocity(entity.getVelocity().add(new Vector(0, launchPower, 0)));
        }

        ParticleUtil.drawCircle(center, radius, 16, Particle.CLOUD);
        SoundUtil.play(center, Sound.ENTITY_IRON_GOLEM_ATTACK, 0.8f, 0.6f);
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrow §elaunch §7nearby entities upward in §e3 block §7radius.";
    }
}
