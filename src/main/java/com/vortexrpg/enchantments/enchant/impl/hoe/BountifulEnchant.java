package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Bountiful: Critical harvest — chance for 3x drops. */
public class BountifulEnchant extends VortexEnchant {

    public BountifulEnchant() { super("bountiful", "Bountiful", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;
        double chance = cfg("chance", 5.0 + level * 3);
        if (!MathUtil.chance(chance)) return;
        // Triple the drops by adding 2x extra
        var drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand());
        var loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        for (int i = 0; i < 2; i++) {
            for (var drop : drops) {
                event.getBlock().getWorld().dropItemNaturally(loc, drop.clone());
            }
        }
        ParticleUtil.burst(loc, Particle.HAPPY_VILLAGER, 20, 1.0);
    }

    @Override public String getDescription() { return "Chance for triple crop drops."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §e" + (int)(5 + level * 3) + "%§7 chance for §63x drops§7."; }
}
