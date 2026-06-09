package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/**
 * Perforator: Arrows pierce through 1/2/3 entities without stopping.
 * Turns every shot into a skewer through crowds.
 */
public class PerforatorEnchant extends VortexEnchant {

    public PerforatorEnchant() {
        super("perforator", "Perforator", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            int pierce = cfgi("pierce_level", level);
            arrow.setPierceLevel(pierce);
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrows pierce through §e" + level + " §7entities.";
    }
}
