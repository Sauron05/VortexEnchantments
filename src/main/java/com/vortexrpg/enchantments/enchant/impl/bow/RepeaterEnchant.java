package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/**
 * Repeater: 10/15/20% chance to not consume an arrow on shot.
 * Economy enchantment — more shots per quiver.
 */
public class RepeaterEnchant extends VortexEnchant {

    public RepeaterEnchant() {
        super("repeater", "Repeater", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOW));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.05 + level * 0.05);
        if (Math.random() < chance) {
            if (event.getProjectile() instanceof AbstractArrow arrow) {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
            }
            event.setConsumeItem(false);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7" + pct + "% chance to §enot consume §7an arrow.";
    }
}
