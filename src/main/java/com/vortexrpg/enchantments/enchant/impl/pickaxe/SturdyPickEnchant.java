package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Sturdy Pick: Chance to not consume durability when mining. */
public class SturdyPickEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};

    public SturdyPickEnchant() { super("sturdy_pick", "Sturdy Pick", EnchantRarity.COMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (MathUtil.chance(cfg("chance", CHANCE[level - 1]))) {
            org.bukkit.inventory.ItemStack tool = player.getInventory().getItemInMainHand();
            org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) tool.getItemMeta();
            if (meta != null && meta.getDamage() > 0) {
                meta.setDamage(meta.getDamage() - 1);
                tool.setItemMeta(meta);
            }
        }
    }

    @Override public String getDescription() { return "Chance to negate durability loss when mining."; }
    @Override public String getDescription(int level) {
        return "§7Mining: §a" + (int) CHANCE[level - 1] + "%§7 chance to not consume durability."; }
}
