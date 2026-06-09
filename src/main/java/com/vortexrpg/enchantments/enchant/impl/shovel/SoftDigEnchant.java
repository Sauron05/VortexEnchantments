package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Soft Dig: Reduced durability loss on soft blocks. */
public class SoftDigEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL,
            Material.COARSE_DIRT, Material.SOUL_SAND, Material.SOUL_SOIL, Material.CLAY);

    public SoftDigEnchant() { super("soft_dig", "Soft Dig", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        if (MathUtil.chance(cfg("chance", CHANCE[level - 1]))) {
            org.bukkit.inventory.ItemStack tool = player.getInventory().getItemInMainHand();
            org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) tool.getItemMeta();
            if (meta != null && meta.getDamage() > 0) {
                meta.setDamage(meta.getDamage() - 1);
                tool.setItemMeta(meta);
            }
        }
    }

    @Override public String getDescription() { return "Chance to negate durability loss on soft blocks."; }
    @Override public String getDescription(int level) {
        return "§7Soft blocks: §a" + (int) CHANCE[level - 1] + "%§7 to not consume durability."; }
}
