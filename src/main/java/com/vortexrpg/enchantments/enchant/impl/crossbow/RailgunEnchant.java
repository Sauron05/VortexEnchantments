package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/**
 * Railgun: Bolt pierces through ALL entities in its flight path.
 * A single devastating shot that goes through everything.
 */
public class RailgunEnchant extends VortexEnchant {

    public RailgunEnchant() {
        super("railgun", "Railgun", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            int pierce = cfgi("pierce", 3 + level * 2);
            arrow.setPierceLevel(pierce);
            arrow.setCritical(true);

            ParticleUtil.spawn(shooter.getLocation().add(0, 1.5, 0), Particle.ELECTRIC_SPARK, 8, 0.2);
            SoundUtil.play(shooter.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 2.0f);
        }
    }

    @Override
    public String getDescription(int level) {
        int pierce = 3 + level * 2;
        return "§7Bolt pierces through §e" + pierce + " §7entities.";
    }
}
