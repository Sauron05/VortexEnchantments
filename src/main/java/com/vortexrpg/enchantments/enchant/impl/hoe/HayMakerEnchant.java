package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Hay Maker: Harvesting wheat has a chance to auto-create hay bales. */
public class HayMakerEnchant extends VortexEnchant {

    public HayMakerEnchant() { super("hay_maker", "Hay Maker", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getType() != Material.WHEAT) return;
        double chance = cfg("chance", 8.0 + level * 4);
        if (!MathUtil.chance(chance)) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.HAY_BLOCK, 1));
        ParticleUtil.burst(event.getBlock().getLocation().add(0.5, 0.5, 0.5), Particle.HAPPY_VILLAGER, 8, 0.5);
    }

    @Override public String getDescription() { return "Harvesting wheat may yield hay bales."; }
    @Override public String getDescription(int level) {
        return "§7Wheat harvest: §e" + (int)(8 + level * 4) + "%§7 chance for §6hay bale§7."; }
}
