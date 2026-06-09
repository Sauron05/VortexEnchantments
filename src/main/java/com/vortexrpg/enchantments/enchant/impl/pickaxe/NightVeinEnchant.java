package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

/** Night Vein: Mining in darkness increases drops by 1 (Fortune-like). */
public class NightVeinEnchant extends VortexEnchant {
    public NightVeinEnchant() { super("night_vein", "Night Vein", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getLightLevel() > cfgi("max_light", 4)) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        Collection<ItemStack> drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand());
        for (ItemStack drop : drops) {
            ItemStack bonus = drop.clone();
            bonus.setAmount(level);
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), bonus);
        }
    }

    @Override public String getDescription() { return "Dark mining increases ore drops."; }
    @Override public String getDescription(int level) {
        return "§7Dark areas: ore drops §a+" + level + "§7 bonus items."; }
}
