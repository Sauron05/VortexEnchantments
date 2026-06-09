package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/**
 * Quickdraw: Partially drawn shots still deal 70/80/90% of full damage
 * instead of the normal reduced amount. Rewards snap shots.
 */
public class QuickdrawEnchant extends VortexEnchant {

    public QuickdrawEnchant() {
        super("quickdraw", "Quickdraw", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        float force = event.getForce();
        if (force >= 1.0f) return; // Full draw, no adjustment needed

        double minDmgPct = cfgd("min_damage_pct", 0.60 + level * 0.10);

        // Scale so partial draws do at least minDmgPct of full damage
        // force ranges from 0.0 to 1.0; we remap it
        double adjusted = minDmgPct + (1.0 - minDmgPct) * force;
        // Apply via velocity scaling (arrow damage is velocity-based)
        event.getProjectile().setVelocity(
                event.getProjectile().getVelocity().normalize().multiply(adjusted * 3.0));
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.60 + level * 0.10) * 100);
        return "§7Partial draws deal at least §e" + pct + "% §7full damage.";
    }
}
