package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Splinter Shot: Arrow shatters into 2/3/4 smaller fragments on entity hit.
 */
public class SplinterShotEnchant extends VortexEnchant {

    private static final int[] FRAGMENT_COUNT = {2, 3, 4};

    public SplinterShotEnchant() {
        super("splinter_shot", "Splinter Shot", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int frags = cfgi("fragment_count", FRAGMENT_COUNT[level - 1]);
        double fragDamagePct = cfg("fragment_damage_percent", 0.30);
        double spread = Math.toRadians(cfg("spread_angle", 40.0));

        double baseDamage = event.getDamage() * fragDamagePct;
        Vector base = victim.getLocation().subtract(shooter.getEyeLocation()).toVector().normalize();

        for (int i = 0; i < frags; i++) {
            double angle = (i * (360.0 / frags)) * Math.PI / 180.0;
            double cos = Math.cos(angle), sin = Math.sin(angle);
            Vector right = base.clone().crossProduct(new Vector(0, 1, 0)).normalize();
            Vector up = base.clone().crossProduct(right).normalize();
            Vector fragDir = base.clone()
                .add(right.clone().multiply(Math.sin(spread) * cos))
                .add(up.clone().multiply(Math.sin(spread) * sin))
                .normalize().multiply(2.0);

            Arrow frag = victim.getWorld().spawn(victim.getLocation().add(0, victim.getHeight() / 2, 0), Arrow.class);
            frag.setShooter(shooter);
            frag.setDamage(baseDamage);
            frag.setVelocity(fragDir);
        }
    }

    @Override
    public String getDescription() { return "Arrows shatter into fragments on impact."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow hit: §eshatters§7 into §e" + FRAGMENT_COUNT[level-1] + " fragments§7 dealing §c30%§7 damage each.";
    }
}
