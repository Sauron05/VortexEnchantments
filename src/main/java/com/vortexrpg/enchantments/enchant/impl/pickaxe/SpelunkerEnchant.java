package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Spelunker: Mining in dark areas grants Night Vision. */
public class SpelunkerEnchant extends VortexEnchant {
    private static final int[] DURATION = {60, 100, 160};

    public SpelunkerEnchant() { super("spelunker", "Spelunker", EnchantRarity.COMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getLightLevel() < 7) {
            int ticks = cfgi("duration_ticks", DURATION[level - 1]);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, ticks, 0, true, false));
        }
    }

    @Override public String getDescription() { return "Mining in dark areas grants Night Vision."; }
    @Override public String getDescription(int level) {
        return "§7Dark mining: §eNight Vision§7 for §a" + (DURATION[level - 1] / 20) + "s§7."; }
}
