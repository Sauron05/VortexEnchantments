package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Magnetbolt: Bolt impact pulls all dropped items and XP orbs within
 * 5/8/12 blocks toward the impact location.
 */
public class MagnetboltEnchant extends VortexEnchant {

    public MagnetboltEnchant() {
        super("magnetbolt", "Magnetbolt", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        Location center = event.getEntity().getLocation();
        double radius = cfgd("radius", 3.0 + level * 3.0);

        center.getWorld().getNearbyEntities(center, radius, radius, radius).forEach(entity -> {
            if (entity instanceof Item || entity instanceof ExperienceOrb) {
                Vector pull = center.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(0.5);
                entity.setVelocity(pull);
            }
        });

        ParticleUtil.drawCircle(center, radius, 16, Particle.ELECTRIC_SPARK);
        SoundUtil.play(center, Sound.BLOCK_BEACON_ACTIVATE, 0.6f, 2.0f);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (3 + level * 3);
        return "§7Block hit: §epulls items + XP §7in §e" + r + " block §7radius.";
    }
}
