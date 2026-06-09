package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Eternal Harvest: Harvested crops regrow instantly, double drops, auto-collect. */
public class EternalHarvestEnchant extends VortexEnchant {

    public EternalHarvestEnchant() { super("eternal_harvest", "Eternal Harvest", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;

        // Cancel default drop, handle manually
        event.setDropItems(false);
        var drops = block.getDrops(player.getInventory().getItemInMainHand());
        int multiplier = cfgi("multiplier", 1 + level);
        for (var drop : drops) {
            drop.setAmount(drop.getAmount() * multiplier);
            var leftover = player.getInventory().addItem(drop);
            leftover.values().forEach(item ->
                    player.getWorld().dropItemNaturally(player.getLocation(), item));
        }

        // Instant regrow
        Material cropType = block.getType();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Block replant = block.getLocation().getBlock();
            replant.setType(cropType);
        }, 1L);

        ParticleUtil.burst(block.getLocation().add(0.5, 0.5, 0.5), Particle.HAPPY_VILLAGER, 15, 0.5);
    }

    @Override public String getDescription() { return "Crops regrow instantly with multiplied drops."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §6" + (1 + level) + "x§7 drops + §ainstant regrow§7 + auto-collect."; }
}
