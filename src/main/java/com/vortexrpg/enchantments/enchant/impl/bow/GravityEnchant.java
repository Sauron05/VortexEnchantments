package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Gravity: Damage scales with vertical distance arrow fell. +2%/3%/4% per block, max +100%.
 */
public class GravityEnchant extends VortexEnchant {

    private static final double[] BONUS_PER_BLOCK = {0.02, 0.03, 0.04};

    public GravityEnchant() {
        super("gravity", "Gravity", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof Arrow)) return;

        // Approximate fall distance using shooter eye Y and victim Y
        double shootY = shooter.getEyeLocation().getY();
        double hitY = victim.getLocation().getY();
        double yDrop = Math.max(0, shootY - hitY);

        double bonusPct = Math.min(yDrop * cfg("bonus_per_block", BONUS_PER_BLOCK[level - 1]),
            cfg("max_bonus_percent", 100.0) / 100.0);
        event.setDamage(event.getDamage() * (1.0 + bonusPct));
    }

    @Override
    public String getDescription() { return "Arrows deal more damage the higher you shoot from."; }

    @Override
    public String getDescription(int level) {
        return "§7+§e" + (int)(BONUS_PER_BLOCK[level-1]*100) + "%§7 damage per block of height (max §a+100%§7).";
    }
}
