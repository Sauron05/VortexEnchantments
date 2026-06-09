package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/** Smelt Pick: Auto-smelts ore drops. */
public class SmeltPickEnchant extends VortexEnchant {
    private static final Map<Material, Material> SMELT = Map.of(
            Material.RAW_IRON, Material.IRON_INGOT,
            Material.RAW_GOLD, Material.GOLD_INGOT,
            Material.RAW_COPPER, Material.COPPER_INGOT
    );

    public SmeltPickEnchant() { super("smelt_pick", "Smelt Pick", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (!mat.name().endsWith("_ORE")) return;
        event.setDropItems(false);
        for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            Material smelted = SMELT.get(drop.getType());
            if (smelted != null) {
                drop = new ItemStack(smelted, drop.getAmount());
            }
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), drop);
        }
    }

    @Override public String getDescription() { return "Auto-smelts ore drops."; }
    @Override public String getDescription(int level) {
        return "§7Ore drops are §6auto-smelted§7 into ingots."; }
}
