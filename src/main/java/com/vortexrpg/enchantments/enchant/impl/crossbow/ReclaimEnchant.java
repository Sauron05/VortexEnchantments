package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Reclaim: 25/35/45% chance to recover the bolt after hitting a target or block.
 */
public class ReclaimEnchant extends VortexEnchant {
    private static final double[] CHANCE = {25, 35, 45};
    public ReclaimEnchant() { super("reclaim", "Reclaim", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        tryReclaim(shooter, level);
    }

    private void tryReclaim(Player shooter, int level) {
        if (!isEnabled()) return;
        if (MathUtil.chance(cfg("chance_" + level, CHANCE[level-1]))) {
            shooter.getInventory().addItem(new ItemStack(Material.ARROW));
        }
    }

    @Override public String getDescription() { return "Chance to recover the bolt after firing."; }
    @Override public String getDescription(int level) {
        return "§7§a" + (int)CHANCE[level-1] + "%§7 chance to recover the bolt after impact."; }
}
