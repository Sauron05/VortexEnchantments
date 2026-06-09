package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
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
 * Netherspike: Converts all damage to fire damage and, when thrown, the
 * trident leaves a 4/6/8 block fire trail along the ground below.
 */
public class NetherspikeEnchant extends VortexEnchant {

    public NetherspikeEnchant() {
        super("netherspike", "Netherspike", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int fireTicks = cfgi("fire_ticks", 40 + level * 20);
        victim.setFireTicks(Math.max(victim.getFireTicks(), fireTicks));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.FLAME, 12, 0.4);
        SoundUtil.play(victim.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.6f, 1.2f);
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int fireTicks = cfgi("fire_ticks", 40 + level * 20);
        victim.setFireTicks(Math.max(victim.getFireTicks(), fireTicks));

        // Create fire trail from shooter to victim
        Location from = shooter.getLocation();
        Location to = victim.getLocation();
        int trailLength = cfgi("trail_length", 2 + level * 2);

        org.bukkit.util.Vector dir = to.toVector().subtract(from.toVector()).normalize();
        Location pos = to.clone();

        for (int i = 0; i < trailLength; i++) {
            Location trail = pos.clone().subtract(dir.clone().multiply(i));
            org.bukkit.block.Block block = trail.getBlock();
            org.bukkit.block.Block above = block.getRelative(org.bukkit.block.BlockFace.UP);
            if (block.getType().isSolid() && above.getType() == org.bukkit.Material.AIR) {
                above.setType(org.bukkit.Material.FIRE);
            }
        }

        ParticleUtil.drawLine(from.add(0, 1, 0), to.add(0, 1, 0), Particle.FLAME, 0.3);
        SoundUtil.play(to, Sound.ITEM_FIRECHARGE_USE, 0.8f, 0.8f);
    }

    @Override
    public String getDescription(int level) {
        int secs = (40 + level * 20) / 20;
        return "§7Ignites targets for §c" + secs + "s§7. Thrown: leaves §6fire trail§7.";
    }
}
