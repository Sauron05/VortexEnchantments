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
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

/**
 * Shrapnel: Arrow that hits a block explodes into fragment damage, hitting
 * all entities within 3/4/5 blocks for 2/3/4 hearts.
 */
public class ShrapnelEnchant extends VortexEnchant {

    public ShrapnelEnchant() {
        super("shrapnel", "Shrapnel", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        Location impact = event.getEntity().getLocation();
        double radius = cfgd("radius", 2.0 + level);
        double damage = cfgd("damage", 2.0 + level * 2.0);

        for (LivingEntity target : MathUtil.getNearbyLiving(impact, radius)) {
            if (target.equals(shooter)) continue;
            target.damage(damage, shooter);
        }

        ParticleUtil.burst(impact, Particle.CRIT, 20, radius);
        SoundUtil.play(impact, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (2 + level);
        int dmg = 2 + level * 2;
        return "§7Block hit: §cfragment burst §7— §c" + dmg + " damage §7in §e" + r + " blocks§7.";
    }
}
