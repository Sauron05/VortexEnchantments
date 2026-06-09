package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Desert King: In desert/badlands, double drops + auto-smelt sand→glass. */
public class DesertKingEnchant extends VortexEnchant {

    public DesertKingEnchant() { super("desert_king", "Desert King", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block block = event.getBlock();
        Biome biome = block.getBiome();
        String biomeName = biome.getKey().value().toLowerCase();
        if (!biomeName.contains("desert") && !biomeName.contains("badlands")) return;

        Material mat = block.getType();
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);

        // Auto-smelt sand → glass
        if ((mat == Material.SAND || mat == Material.RED_SAND) && cfgb("auto-smelt", true)) {
            event.setDropItems(false);
            int amount = level >= 3 ? 2 : 1;
            block.getWorld().dropItemNaturally(loc, new ItemStack(Material.GLASS, amount));
            ParticleUtil.burst(loc, Particle.FLAME, 8, 0.5);
            return;
        }

        // Double drops for other soft blocks
        double chance = cfg("double-drop-chance", 30.0 + level * 15);
        if (MathUtil.chance(chance)) {
            for (ItemStack drop : block.getDrops(player.getInventory().getItemInMainHand())) {
                block.getWorld().dropItemNaturally(loc, drop.clone());
            }
            ParticleUtil.burst(loc, Particle.HAPPY_VILLAGER, 10, 0.5);
        }
    }

    @Override public String getDescription() { return "Double drops + auto-smelt in desert/badlands."; }
    @Override public String getDescription(int level) {
        return "§7Desert/Badlands: §6auto-smelt sand§7 + §e" + (int)(30 + level * 15) + "%§7 double drops."; }
}
