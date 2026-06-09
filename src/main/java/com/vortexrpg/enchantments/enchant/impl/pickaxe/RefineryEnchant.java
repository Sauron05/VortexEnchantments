package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/** Refinery: Auto-smelts ore drops and has chance to double smelted output. */
public class RefineryEnchant extends VortexEnchant {
    private static final double[] DOUBLE_CHANCE = {10, 15, 20};
    private static final Map<Material, Material> SMELT = Map.of(
            Material.RAW_IRON, Material.IRON_INGOT,
            Material.RAW_GOLD, Material.GOLD_INGOT,
            Material.RAW_COPPER, Material.COPPER_INGOT);

    public RefineryEnchant() { super("refinery", "Refinery", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        event.setDropItems(false);
        for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            Material smelted = SMELT.get(drop.getType());
            if (smelted != null) {
                int amount = drop.getAmount();
                if (MathUtil.chance(cfg("double_chance", DOUBLE_CHANCE[level - 1]))) {
                    amount *= 2;
                }
                drop = new ItemStack(smelted, amount);
            }
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), drop);
        }
    }

    @Override public String getDescription() { return "Auto-smelts ore with chance for double output."; }
    @Override public String getDescription(int level) {
        return "§7Auto-smelt + §a" + (int) DOUBLE_CHANCE[level - 1] + "%§7 double output."; }
}
