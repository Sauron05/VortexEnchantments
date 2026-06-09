package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Omni-Mine: Mined blocks give both silk-touch and fortune drops. */
public class OmniMineEnchant extends VortexEnchant {
    public OmniMineEnchant() { super("omni_mine", "Omni-Mine", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        // Drop the silk-touch version (the ore block itself) in addition to normal drops
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                new ItemStack(event.getBlock().getType()));
    }

    @Override public String getDescription() { return "Ore gives both silk-touch and fortune drops."; }
    @Override public String getDescription(int level) {
        return "§7Ore: drops §bboth§7 the ore block + normal drops."; }
}
