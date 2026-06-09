package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Barrage: Shoots 1/2/3 extra arrows in a cone spread alongside your main arrow.
 * Extra arrows deal 50% damage.
 */
public class BarrageEnchant extends VortexEnchant {

    public BarrageEnchant() {
        super("barrage", "Barrage", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        int extra = cfgi("extra_arrows", level);
        double spread = cfgd("spread", 0.15);
        Vector baseVel = event.getProjectile().getVelocity();

        for (int i = 0; i < extra; i++) {
            double offsetX = (Math.random() - 0.5) * spread * 2;
            double offsetY = (Math.random() - 0.5) * spread;
            double offsetZ = (Math.random() - 0.5) * spread * 2;

            Arrow arrow = shooter.getWorld().spawn(shooter.getEyeLocation(), Arrow.class);
            arrow.setShooter(shooter);
            arrow.setVelocity(baseVel.clone().add(new Vector(offsetX, offsetY, offsetZ)));
            arrow.setDamage(arrow.getDamage() * 0.5);
            arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Fires §e" + level + " §7extra arrows in a spread §8(50% dmg each).";
    }
}
