package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/** Winnow: Auto-processes harvested raw crops: wheat→bread, potato→baked potato, cactus→dye. */
public class WinnowEnchant extends VortexEnchant {
    private static final Map<Material, Material> RECIPES = Map.of(
        Material.WHEAT, Material.BREAD,
        Material.POTATO, Material.BAKED_POTATO,
        Material.CACTUS, Material.GREEN_DYE
    );

    public WinnowEnchant() { super("winnow", "Winnow", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        event.setDropItems(false);
        for (ItemStack drop : event.getBlock().getDrops()) {
            Material processed = RECIPES.get(drop.getType());
            if (processed != null) {
                player.getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(processed, drop.getAmount()));
            } else {
                player.getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
            }
        }
    }

    @Override public String getDescription() { return "Auto-processes raw crops on harvest."; }
    @Override public String getDescription(int level) {
        return "§7Wheat→Bread, Potato→Baked, Cactus→Dye on harvest."; }
}
