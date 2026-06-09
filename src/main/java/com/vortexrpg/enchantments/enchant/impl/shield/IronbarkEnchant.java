package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Ironbark — Shield (Uncommon, Max 3)
 * Passive 3/5/8% damage reduction while the shield is in offhand (even when not blocking).
 */
public class IronbarkEnchant extends VortexEnchant {

    public IronbarkEnchant() {
        super("ironbark", "Ironbark", "shield");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] pct = {3, 5, 8};
        return "Passively reduces incoming damage by §a" + pct[level - 1] + "%§7 while your shield is in your offhand.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        double[] reductions = {0.03, 0.05, 0.08};
        double reduce = cfgd("reduce", reductions[level - 1]);
        event.setDamage(event.getDamage() * (1.0 - reduce));
    }
}
