package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fragmentation: On entity hit, bolt splits into 3/5/7 secondary projectiles
 * flying outward in random directions, each dealing 25% of original damage.
 */
public class FragmentationEnchant extends VortexEnchant {

    public FragmentationEnchant() {
        super("fragmentation", "Fragmentation", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int fragments = cfgi("fragments", 1 + level * 2);
        double fragDmg = event.getDamage() * cfgd("fragment_pct", 0.25);

        for (int i = 0; i < fragments; i++) {
            Vector dir = new Vector(
                    ThreadLocalRandom.current().nextDouble(-1, 1),
                    ThreadLocalRandom.current().nextDouble(0.1, 0.5),
                    ThreadLocalRandom.current().nextDouble(-1, 1)
            ).normalize().multiply(0.8);

            Arrow frag = victim.getWorld().spawn(victim.getLocation().add(0, 1, 0), Arrow.class);
            frag.setShooter(shooter);
            frag.setVelocity(dir);
            frag.setDamage(fragDmg);
            frag.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }

        ParticleUtil.burst(victim.getLocation().add(0, 1, 0), Particle.CRIT, 10, 0.5);
    }

    @Override
    public String getDescription(int level) {
        int count = 1 + level * 2;
        return "§7Hit: §csplits §7into §e" + count + " fragments §7(25% dmg each).";
    }
}
