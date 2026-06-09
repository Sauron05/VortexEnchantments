package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/** Recycle: 8/10/12% chance that mining returns 1 durability to the pickaxe. */
public class RecycleEnchant extends VortexEnchant {
    private static final double[] REPAIR_CHANCE = {8, 10, 12};

    public RecycleEnchant() { super("recycle", "Recycle", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("repair_chance", REPAIR_CHANCE[level-1]))) return;
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!(tool.getItemMeta() instanceof Damageable meta)) return;
        if (meta.getDamage() <= 0) return;
        meta.setDamage(meta.getDamage() - 1);
        tool.setItemMeta(meta);
    }

    @Override public String getDescription() { return "Mining may repair the pickaxe's durability."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)REPAIR_CHANCE[level-1] + "%§7 chance per block mined to restore §e1 durability§7."; }
}
