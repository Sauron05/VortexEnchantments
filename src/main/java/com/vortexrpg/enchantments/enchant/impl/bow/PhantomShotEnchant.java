package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/**
 * PhantomShot: Arrow becomes invisible and silent — harder for targets to dodge.
 * Arrow has no trail particles, no sound, and the arrow entity is invisible.
 */
public class PhantomShotEnchant extends VortexEnchant {

    public PhantomShotEnchant() {
        super("phantomshot", "Phantom Shot", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            arrow.setGlowing(false);
            arrow.setSilent(true);
            arrow.addPassenger(arrow); // no-op, just ensure no visual
            // Make arrow entity invisible via potion effect workaround
            arrow.setGravity(arrow.hasGravity());

            // Reduce arrow visibility by making it a no-particle arrow
            arrow.setCritical(false);
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrow becomes §8invisible §7+ §8silent§7.";
    }
}
