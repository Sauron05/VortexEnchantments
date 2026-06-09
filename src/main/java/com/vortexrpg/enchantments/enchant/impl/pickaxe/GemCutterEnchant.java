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
import java.util.Set;

/** Gem Cutter: Chance to double diamond/emerald drops. */
public class GemCutterEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};
    private static final Set<Material> GEMS = Set.of(
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE);

    public GemCutterEnchant() { super("gem_cutter", "Gem Cutter", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!GEMS.contains(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), drop.clone());
        }
    }

    @Override public String getDescription() { return "Chance to double diamond/emerald drops."; }
    @Override public String getDescription(int level) {
        return "§7Gems: §a" + (int) CHANCE[level - 1] + "%§7 to §bdouble§7 drops."; }
}
